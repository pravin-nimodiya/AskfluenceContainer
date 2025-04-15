package com.ideas.askfluence.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class ConfigData {

    public static final String CONTENT_TYPE = "application/json";

    @Value("${bedrock.llm.model.id:meta.llama3-3-70b-instruct-v1:0}")
    private String llmModelId;

    @Value("${bedrock.embedding.model.id:amazon.titan-embed-text-v2:0}")
    private String embeddingModelId;

    @Value("${confluence.userid:dummy}")
    private String confluenceUserId;

    @Value("${confluence.token:Pravin@123}")
    private String confluenceToken;

    @Value("${confluence.url:https://askfluence.atlassian.net/wiki/rest/api}")
    private String confluenceUrl;

}
