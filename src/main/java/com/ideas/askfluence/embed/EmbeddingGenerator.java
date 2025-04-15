package com.ideas.askfluence.embed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.ideas.askfluence.config.ConfigData;
import com.ideas.askfluence.config.Sanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

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

    public Map<String,List<Float>> embedAllContent(List<String> allContent) {
        return new HashSet<>(allContent).stream().collect(Collectors.toMap(text -> text, this::embedd));
    }

    private List<Float> embedd(String rawData) {

        String requestPayload = "{ \"inputText\": \"" + sanitize(rawData) + "\" }";

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(configData.getEmbeddingModelId())  // Use Cohere for embeddings
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE)
                .body(SdkBytes.fromByteArray(requestPayload.getBytes(StandardCharsets.UTF_8)))
                .build();

        InvokeModelResponse response = bedrockClient.invokeModel(request);

        JsonNode embedding;
        try {
            embedding = Sanitizer.OBJECT_MAPPER.readTree(response.body().asUtf8String()).get("embedding");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse response from Bedrock model", e);
        }
        return convertJsonArrayToList(embedding);

    }
}
