package com.common.cyberark.springboot.configuration;

import com.common.cyberark.springboot.properties.CyberarkProperties;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnMissingBean(name = "restTemplate")
public class ConnectionConfiguration {

    private CyberarkProperties cyberarkProperties;

    @Autowired
    public ConnectionConfiguration(CyberarkProperties cyberarkProperties) {
        this.cyberarkProperties = cyberarkProperties;
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder.build();
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        return restTemplate;
    }

    @Bean
    public HttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(cyberarkProperties.getConnectionMaxTotal());
        poolingConnectionManager.setDefaultMaxPerRoute(cyberarkProperties.getConnectionDefaultMaxPerRoute());
        return poolingConnectionManager;
    }

    @Bean
    public HttpClientBuilder httpClientBuilder(HttpClientConnectionManager poolingConnectionManager) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(poolingConnectionManager);
        return httpClientBuilder;
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClientBuilder httpClientBuilder) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClientBuilder.build());
        clientHttpRequestFactory.setConnectTimeout(cyberarkProperties.getConnectionConnectTimeout());
        clientHttpRequestFactory.setReadTimeout(cyberarkProperties.getConnectionReadTimeout());
        clientHttpRequestFactory.setConnectionRequestTimeout(cyberarkProperties.getConnectionRequestTimeout());
        return clientHttpRequestFactory;
    }
}
