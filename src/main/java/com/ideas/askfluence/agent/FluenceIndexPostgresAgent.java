package com.ideas.askfluence.agent;

import com.ideas.askfluence.embed.EmbeddingGenerator;
import com.ideas.askfluence.index.PostgresVectorIndexer;
import com.ideas.askfluence.scrape.ScraperWithPagination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FluenceIndexPostgresAgent {

    @Autowired
    private ScraperWithPagination scraperWithPagination;

    @Autowired
    private EmbeddingGenerator embeddingGenerator;

    @Autowired
    private PostgresVectorIndexer postgresVectorIndexer;

    public String index(String rootId) throws Exception {
        log.info("Indexing with rootIdin FluenceIndexPostgresAgent: " + rootId);
        List<String> allContent = new ArrayList<>();
        scraperWithPagination.deepScrape(rootId, allContent);
        Map<String, List<Float>> embeddingsWithText = embeddingGenerator.embedAllContent(allContent);
        postgresVectorIndexer.indexToPostgresWithMetadata(embeddingsWithText);
        return "Indexing completed successfully.";
    }
}
