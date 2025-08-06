package com.ideas.askfluence.llm;

import com.ideas.askfluence.config.ConfigData;
import com.ideas.askfluence.config.Connections;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings
class FluenceResponseEngineTest {
    public static final String CONTEXT = "THREE IDIOTS MOVIE";
    final String question = "What is a machine?";
    final String LLM_RESPONSE = "Sir Machines are any combination of bodies so connected that their " +
            "relative motions are constrained and by which means, force and motion may be transmitted and modified " +
            "as a screw and its nut, or a lever arranged to turn about a fulcrum or a pulley about its pivot, " +
            "etc especially, a construction, more or less complex consisting of a combination of moving parts, " +
            "or simple mechanical elements as wheels, levers, cams etc";


    @InjectMocks
    FluenceResponseEngine fluenceResponseEngine;

    @Mock
    Connections connections;

    @Test
    void generateLLMResponse() {
        // GIVEN
        BedrockRuntimeClient bedrockRuntimeClient = mock(BedrockRuntimeClient.class);
        when(connections.getBedrockClient()).thenReturn(bedrockRuntimeClient);
        when(bedrockRuntimeClient.invokeModel(any(InvokeModelRequest.class))).thenReturn(
                InvokeModelResponse.builder()
                        .body(software.amazon.awssdk.core.SdkBytes.fromUtf8String(LLM_RESPONSE))
                        .build()
        );
        // WHEN
        String actualResponse = fluenceResponseEngine.generateLLMResponse(CONTEXT, question);

        // THEN
        assertNotNull(actualResponse);
        assertEquals(LLM_RESPONSE, actualResponse);
        //WAH KYA BAAT HAI
    }
}