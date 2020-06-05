package ru.d1g.doceasy.core.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.d1g.doceasy.core.interceptor.LoggingRequestInterceptor;

@Configuration
public class ClientConfiguration {
    @Bean
    public RestTemplate moduleClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.additionalInterceptors((httpRequest, bytes, execution) -> {
            return execution.execute(httpRequest, bytes);
        }).build();

        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        restTemplate.getInterceptors().add(new LoggingRequestInterceptor());

        return restTemplate;
    }
}
