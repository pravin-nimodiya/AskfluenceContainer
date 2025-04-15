package com.ideas.askfluence.llm;

import com.ideas.askfluence.config.ConfigData;
import com.ideas.askfluence.config.Connections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;


@Service
@RequiredArgsConstructor
@Slf4j
public class FluenceResponseEngine {
    @Autowired
    Connections connections;

    @Autowired
    ConfigData configData;
    public String generateLLMResponse(String context, String userQuery) {

        String prompt = new Prompt(context, userQuery).getPrompt();
        // Updated payload with explicit inference type
        String payload = "{"
                + "\"prompt\": \"" + prompt + "\","
                + "\"max_gen_len\": 500,"
                + "\"temperature\": 0.7,"
                + "\"top_p\": 0.9"
                + "}";

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(configData.getLlmModelId()) // Ensure correct modelId
                .contentType(ConfigData.CONTENT_TYPE)
                .accept(ConfigData.CONTENT_TYPE)
                .body(SdkBytes.fromUtf8String(payload))
                .build();

        InvokeModelResponse response = connections.getBedrockClient().invokeModel(request);
        return response.body().asUtf8String();
    }
}
