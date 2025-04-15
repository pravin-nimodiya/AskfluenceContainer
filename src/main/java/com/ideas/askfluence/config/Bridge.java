package com.ideas.askfluence.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class Bridge {
    
    @Autowired
    private ConfigData config;

    public  final String BASE_PAGE_ID = "2703425864";

    private  String getAuthString(){
        return Base64.getEncoder().encodeToString((config.getConfluenceUserId()+":"+config.getConfluenceToken()).getBytes());

    }

    public  HttpGet getLoginRequest(String url) {
        log.info("URL: " + url);
        log.info("Authorization: " + config.getConfluenceUserId()+":"+config.getConfluenceToken());
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", "Basic " + getAuthString());
        request.setHeader("Accept", ConfigData.CONTENT_TYPE);
        return request;
    }

    public  String executeHttpRequest(String url) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = client.execute(getLoginRequest(url))) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }
}
