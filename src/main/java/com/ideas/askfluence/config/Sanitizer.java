package com.ideas.askfluence.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Sanitizer {
    final static int CHUNK_SIZE = 8000;
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String sanitize(String jsonText) {
        return  Jsoup.parse(jsonText).text()
                .replace("\\n", " ")
                .replace("\r", "")
                .replace("\\","\\\\")
                .replace("\"","")
                .replaceAll("[^\\x20-\\x7E]", "")
                .replaceAll("\\s+", " ");
    }

    public static String filterPrompt(String jsonText) {
        return  jsonText
                .replace("\\n", " ")
                .replace("\r", "")
                .replace("\\","\\\\")
                .replace("\"","")
                .replaceAll("[^\\x20-\\x7E]", "")
                .replaceAll("\\s+", " ");
    }

    public static List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += CHUNK_SIZE) {
            chunks.add(text.substring(i, Math.min(i + CHUNK_SIZE, text.length())));
        }
        return chunks;
    }

    public static String limitTokenLength(String text) {
        return text.length() > CHUNK_SIZE ? text.substring(0, CHUNK_SIZE) : text;
    }

    public static List<Float> convertJsonArrayToList(JsonNode embeddingNode) {
        List<Float> embeddingList = new ArrayList<>();
        if (embeddingNode.isArray()) {
            for (JsonNode node : embeddingNode) {
                embeddingList.add(node.floatValue());
            }
        }
        return embeddingList;
    }

    public static String formatLLMResponse(String response) {
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(response);
            //return jsonNode.get("generation").asText();
            return cleanParagraph(jsonNode.get("generation").asText());
        } catch (Exception e) {
            throw new RuntimeException("Sorry I did not understand");
        }
    }

        public static String cleanParagraph(String paragraph) {
            if (paragraph == null || paragraph.isEmpty()) return paragraph;
            String[] sentences = paragraph.split("(?<=[.!?])\\s+");

            Set<String> uniqueSentences = new LinkedHashSet<>();
            for (String sentence : sentences) {
                sentence = sentence.trim();
                if (sentence.matches(".*[.!?]$")) { // Check if sentence ends properly
                    uniqueSentences.add(sentence);
                }
            }
            return String.join(" ", uniqueSentences);
        }

}
