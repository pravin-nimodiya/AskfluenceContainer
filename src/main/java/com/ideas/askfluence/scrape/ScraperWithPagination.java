package com.ideas.askfluence.scrape;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideas.askfluence.config.Bridge;
import com.ideas.askfluence.config.ConfigData;
import com.ideas.askfluence.config.Sanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScraperWithPagination {

    @Autowired
    ConfigData configData;

    @Autowired
    Bridge bridge;
    public void deepScrape(String rootPageId, List<String> allContent) throws Exception {
        List<String> descendantPageIds = scrapeAllDescendantPageIds(rootPageId);
        log.info("Descendant page IDs: " + descendantPageIds);
        descendantPageIds.add(0, rootPageId); // Include root itself
        HashSet<String> visited = new HashSet<>();
        for (String pageId : descendantPageIds) {
            if (visited.contains(pageId)) continue;
            visited.add(pageId);

            List<String> pageContent = crawl(pageId);
            if (!pageContent.isEmpty()) {
                pageContent.forEach(log::info);
                allContent.addAll(pageContent);
            }
        }
    }

    private List<String> crawl(String pageId) {
        try {
            String rawData = bridge.executeHttpRequest(String.format(configData.getConfluenceUrl()+"/%s?expand=body.view", pageId));
            log.info("Raw data: " + rawData);
            JsonNode result = Sanitizer.OBJECT_MAPPER.readTree(rawData);
            List<String> pageContent = new ArrayList<>();
                    if (hasRelevantData(result)) {
                        PageData pageData = extractRelevantData(result);
                        pageContent.add(pageData.toString());
                    }
            return pageContent.stream().filter(content -> !StringUtil.isBlank(content)).toList();
        }catch (Exception e){
            e.printStackTrace();
            return List.of();
        }
    }

    private static boolean hasRelevantData(JsonNode result) {
        return result.get("body") != null && !StringUtil.isBlank(result.get("body").get("view").get("value").asText());
    }

    private static PageData extractRelevantData(JsonNode result) {
        return new PageData(result);
    }

    public List<String> scrapeAllDescendantPageIds(String rootPageId) throws Exception {
        List<String> allDescendantIds = new ArrayList<>();
        int start = 0;
        int limit = 25;
        boolean hasMore = true;
        log.info("confluenceUrl: " + configData.getConfluenceUrl());
        while (hasMore) {
            String url = String.format(configData.getConfluenceUrl()+"/%s/descendant/page?limit=%d&start=%d",
                    rootPageId, limit, start);

            String response = bridge.executeHttpRequest(url);
            JsonNode root = new ObjectMapper().readTree(response);
            JsonNode results = root.path("results");

            for (JsonNode page : results) {
                String pageId = page.path("id").asText();
                allDescendantIds.add(pageId);
            }

            int size = root.path("size").asInt();
            hasMore = size == limit;
            start += size;
        }

        return allDescendantIds;
    }
}
