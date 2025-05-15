package com.ideas.askfluence.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideas.askfluence.agent.FluenceGenTextAgent;
import com.ideas.askfluence.agent.FluenceIndexPostgresAgent;
import com.ideas.askfluence.index.SpaceDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/askfluence")
@Slf4j
public class AskFluenceController {

    @Autowired
    FluenceGenTextAgent fluenceGenTextAgent;

    @Autowired
    FluenceIndexPostgresAgent fluenceIndexPostgresAgent;
    ObjectMapper RESPONSE_MAPPER = new ObjectMapper();

    @CrossOrigin(origins = {"*"})
    @PostMapping("/ask")
    public ResponseEntity<String>  askFluence(@RequestBody AskRequest request) throws JsonProcessingException {
        String response = fluenceGenTextAgent.askAgent(request);
        RESPONSE_MAPPER.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        return ResponseEntity.ok(RESPONSE_MAPPER.writeValueAsString(response));
    }

    @CrossOrigin(origins = {"*"})
    @PostMapping("/index")
    public ResponseEntity<String>  index(@RequestParam String rootId) throws Exception {
        log.info("Indexing with rootId: " + rootId);
        String response = fluenceIndexPostgresAgent.index(rootId);
        RESPONSE_MAPPER.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        return ResponseEntity.ok(RESPONSE_MAPPER.writeValueAsString(response));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/spaces")
    public List<SpaceDetails> getSpaces() throws JsonProcessingException {
        List<SpaceDetails> response = fluenceIndexPostgresAgent.getSpaces();
        RESPONSE_MAPPER.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        return response;
    }


}
