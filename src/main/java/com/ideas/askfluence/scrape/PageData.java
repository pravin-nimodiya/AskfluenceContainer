package com.ideas.askfluence.scrape;

import com.fasterxml.jackson.databind.JsonNode;
import com.ideas.askfluence.config.Sanitizer;

public class PageData {

    final static String BASE_REF = "https://ideasinc.atlassian.net/wiki";
    String title;
    String content;
    String references;

    public PageData(JsonNode result) {
        this.title = result.get("title").asText();
        this.content = Sanitizer.sanitize(result.get("body").get("view").get("value").asText());
        this.references = BASE_REF + result.get("_links").get("webui").asText();
    }

    @Override
    public String toString() {
        return "Data {" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", references=" + references +
                '}';
    }

}
