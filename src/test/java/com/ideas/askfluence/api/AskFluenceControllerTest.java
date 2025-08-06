package com.ideas.askfluence.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ideas.askfluence.agent.FluenceGenTextAgent;
import com.ideas.askfluence.agent.FluenceIndexPostgresAgent;
import com.ideas.askfluence.index.SpaceDetails;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@MockitoSettings
class AskFluenceControllerTest {

    @InjectMocks
    AskFluenceController askFluenceController;

    @Mock
    FluenceGenTextAgent fluenceGenTextAgent;

    @Mock
    FluenceIndexPostgresAgent fluenceIndexPostgresAgent;

    AskRequest askRequest;

    @Test
    void askFluence() throws JsonProcessingException {
        AskRequest askRequest = new AskRequest("What is the capital of France?", List.of(1L, 2L));
        askFluenceController.askFluence(askRequest);
        verify(fluenceGenTextAgent).askAgent(askRequest);
    }

    @Test
    void index() {
        String rootId = "12345";
        try {
            askFluenceController.index(rootId);
            verify(fluenceIndexPostgresAgent).index(rootId);
        } catch (Exception e) {
            fail("Indexing failed with exception: " + e.getMessage());
        }
    }

    @Test
    void health() {
        String response = askFluenceController.health().getBody();
        assertEquals("OK", response);
    }

    @Test
    void getSpaces() {
        try {
            List<SpaceDetails> spaces = askFluenceController.getSpaces();
            assertNotNull(spaces);
            verify(fluenceIndexPostgresAgent).getSpaces();
        } catch (JsonProcessingException e) {
            fail("Failed to get spaces with exception: " + e.getMessage());
        }
    }
}