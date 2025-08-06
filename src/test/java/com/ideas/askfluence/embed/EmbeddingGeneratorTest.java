package com.ideas.askfluence.embed;

import com.ideas.askfluence.config.ConfigData;
import com.ideas.askfluence.llm.FluenceResponseEngine;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.services.bedrockruntime.model.ValidationException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@MockitoSettings
class EmbeddingGeneratorTest {

    static final String CONTENT = "This is a test content for embedding.";
    static final String EMBEDDED_TEXT = "{\"embedding\": [0.1, 0.2, 0.3]}";
    static final String EMBEDDING_MODEL = "amazon.titan-embed-text-v2:0";
    static final String expectedEmbedding = "[0.1, 0.2, 0.3]";
    @InjectMocks
    EmbeddingGenerator embeddingGenerator;

    @Mock

    ConfigData configData;

    @Mock
    BedrockRuntimeClient bedrockClient;

    @Mock
    FluenceResponseEngine fluenceResponseEngine;


    @Test
    void embedAllContent() {
        //GIVEN
        when(configData.getEmbeddingModelId()).thenReturn(EMBEDDING_MODEL);
        when(bedrockClient.invokeModel(any(InvokeModelRequest.class))).thenReturn(
                InvokeModelResponse.builder()
                        .body(SdkBytes.fromUtf8String(EMBEDDED_TEXT))
                        .build()
        );
        // When
        Map<String, List<Float>> result = embeddingGenerator.embedAllContent(List.of(CONTENT));
        // Then
        assertContent(result);
    }

    private static void assertContent(Map<String, List<Float>> result) {
        assertNotNull(result);
        assertTrue(result.containsKey(CONTENT));
        assertEquals(expectedEmbedding, result.get(CONTENT).toString());
    }

    @Test
    void embedAllContentAboveThreshold() {
        //GIVEN
        when(configData.getEmbeddingModelId()).thenReturn(EMBEDDING_MODEL);
        when(fluenceResponseEngine.generateLLMSummaryResponse(anyString())).thenReturn(EMBEDDED_TEXT);
        when(bedrockClient.invokeModel(any(InvokeModelRequest.class)))
                .thenThrow(ValidationException.builder().statusCode(400).message("Bad Request").build())
                .thenReturn(InvokeModelResponse.builder()
                        .body(SdkBytes.fromUtf8String(EMBEDDED_TEXT))
                        .build()
                );
        // When
        Map<String, List<Float>> result = embeddingGenerator.embedAllContent(List.of(CONTENT));

        // Then
        assertContent(result);
        verify(fluenceResponseEngine).generateLLMSummaryResponse(anyString());
        verify(bedrockClient, times(2)).invokeModel(any(InvokeModelRequest.class));
        verify(configData, times(2)).getEmbeddingModelId();
    }
}