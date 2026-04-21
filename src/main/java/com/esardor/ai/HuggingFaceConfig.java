package com.esardor.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HuggingFaceConfig {

    @Value("${ai.huggingface.token}")
    private String token;

    @Value("${ai.huggingface.base-url}")
    private String baseurl;

    @Bean
    public HuggingFaceClient huggingFaceClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseurl)
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Content-type", "application/json")
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .build();

        return factory.createClient(HuggingFaceClient.class);
    }

}
