package com.ideas.askfluence.agent;

import com.ideas.askfluence.api.AskRequest;
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

    public String askAgent(AskRequest query) {
        Map<String, List<Float>> embeddings = embeddingGenerator.embedAllContent(List.of(query.question()));
        String context = contextResolver.resolve(embeddings.get(query.question()),query.spaces());
        String llmResponse = fluenceResponseEngine.generateLLMResponse(context, query.question());
        return Sanitizer.formatLLMResponse(llmResponse);
    }
}
