package com.ideas.askfluence.embed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.ideas.askfluence.config.ConfigData;
import com.ideas.askfluence.config.Sanitizer;
import com.ideas.askfluence.llm.FluenceResponseEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.services.bedrockruntime.model.ValidationException;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ideas.askfluence.config.ConfigData.CONTENT_TYPE;
import static com.ideas.askfluence.config.Sanitizer.convertJsonArrayToList;
import static com.ideas.askfluence.config.Sanitizer.sanitize;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmbeddingGenerator {


    @Autowired
    ConfigData configData;

    @Autowired
    BedrockRuntimeClient bedrockClient;

    @Autowired
    FluenceResponseEngine fluenceResponseEngine;

    public Map<String,List<Float>> embedAllContent(List<String> allContent) {
        return new HashSet<>(allContent).stream().collect(Collectors.toMap(text -> text, this::embedd));
    }

    private List<Float> embedd(String rawData) {
        String requestPayload = "{ \"inputText\": \"" + sanitize(rawData) + "\" }";
        JsonNode embedding;
        try {
            InvokeModelResponse response = getEmbeddings(requestPayload);
            embedding = Sanitizer.OBJECT_MAPPER.readTree(response.body().asUtf8String()).get("embedding");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse response from Bedrock model", e);
        }catch (ValidationException e) {
            if(e.statusCode() == 400){
                log.error("Bad request received. Using LLM to summarize: " + e.getMessage());
                log.error("Raw data: " + rawData);
                embedding = getSummarizedOutputFromLLM(rawData);
            }
            else{
                throw new RuntimeException("Failed to validate response from Bedrock model", e);
            }
        }
        return convertJsonArrayToList(embedding);

    }

    private JsonNode getSummarizedOutputFromLLM(String rawData) {
        String summarizedMessage = fluenceResponseEngine.generateLLMSummaryResponse(rawData);
        log.error("Summarized message: " + summarizedMessage);
        String summarizedPayload = "{ \"inputText\": \"" + sanitize(summarizedMessage) + "\" }";
        InvokeModelResponse summarizedResponse = getEmbeddings(summarizedPayload);
        try {
            return Sanitizer.OBJECT_MAPPER.readTree(summarizedResponse.body().asUtf8String()).get("embedding");
        }catch (JsonProcessingException ex){
            log.error("Failed to again parse response from Bedrock model", ex);
        }
        return null;
    }

    private InvokeModelResponse getEmbeddings(String requestPayload) {
        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(configData.getEmbeddingModelId())  // Use Cohere for embeddings
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE)
                .body(SdkBytes.fromByteArray(requestPayload.getBytes(StandardCharsets.UTF_8)))
                .build();

        return bedrockClient.invokeModel(request);
    }
}
