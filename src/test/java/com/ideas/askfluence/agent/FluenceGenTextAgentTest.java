package com.ideas.askfluence.agent;

import com.ideas.askfluence.api.AskRequest;
import com.ideas.askfluence.embed.EmbeddingGenerator;
import com.ideas.askfluence.llm.FluenceResponseEngine;
import com.ideas.askfluence.query.PostgresRAGContextResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings
class FluenceGenTextAgentTest {
    public static final String LAXMI_CHIT_FUND_QUESTIONS = "Is Mumbai the capital of India?";
    public static final String SAHI_JAWAB = "Delhi ka capital India hai, aur Mumbai ka capital Dharavi nahi hai. Mumbai ka capital Mumbai hi hai.";
    public static final String LLM_ANSWER = "{\"generation\":\"Delhi ka capital India hai, aur Mumbai ka capital Dharavi nahi hai. Mumbai ka capital Mumbai hi hai.\"}";
    public static final List<Long> SPACES = List.of(1L, 2L);
    public static final List<Float> EMBEDDING = List.of(0.1f, 0.2f, 0.3f);
    @InjectMocks
    FluenceGenTextAgent fluenceGenTextAgent;
    @Mock
    private PostgresRAGContextResolver contextResolver;

    @Mock
    private FluenceResponseEngine fluenceResponseEngine;

    @Mock
    private EmbeddingGenerator embeddingGenerator;

    @Test
    void askAgent() {
        when(fluenceResponseEngine.generateLLMResponse(null, LAXMI_CHIT_FUND_QUESTIONS)).thenReturn(LLM_ANSWER);
        List<String> laxmiChitFundQuestions = List.of(LAXMI_CHIT_FUND_QUESTIONS);
        when(embeddingGenerator.embedAllContent(laxmiChitFundQuestions)).thenReturn(Map.of(LAXMI_CHIT_FUND_QUESTIONS, EMBEDDING));
        String actualAnswer = fluenceGenTextAgent.askAgent(new AskRequest(LAXMI_CHIT_FUND_QUESTIONS, SPACES));
        verify(embeddingGenerator).embedAllContent(laxmiChitFundQuestions);
        verify(contextResolver).resolve(embeddingGenerator.embedAllContent(laxmiChitFundQuestions).get(LAXMI_CHIT_FUND_QUESTIONS), SPACES);
        verify(fluenceResponseEngine).generateLLMResponse(
                any(),
                eq(LAXMI_CHIT_FUND_QUESTIONS)
        );
        Assertions.assertEquals(SAHI_JAWAB, actualAnswer);
    }
}