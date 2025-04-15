package com.ideas.askfluence.agent;

import com.ideas.askfluence.embed.EmbeddingGenerator;
import com.ideas.askfluence.llm.FluenceResponseEngine;
import com.ideas.askfluence.query.PostgresRAGContextResolver;
import com.ideas.askfluence.config.Sanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class FluenceGenTextAgent {

    @Autowired
    private PostgresRAGContextResolver contextResolver;

    @Autowired
    private FluenceResponseEngine fluenceResponseEngine;

    @Autowired
    private EmbeddingGenerator embeddingGenerator;

    public String askAgent(String query) {
        Map<String, List<Float>> embeddings = embeddingGenerator.embedAllContent(List.of(query));
        String context = contextResolver.resolve(embeddings.get(query));
        String llmResponse = fluenceResponseEngine.generateLLMResponse(context, query);
        return Sanitizer.formatLLMResponse(llmResponse);
    }
}
