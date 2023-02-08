package com.common.cyberark.springboot.configuration;

import com.common.cyberark.client.CyberarkClient;
import com.common.cyberark.springboot.properties.CyberarkProperties;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({ConnectionConfiguration.class})
@EnableConfigurationProperties(CyberarkProperties.class)
@ConditionalOnProperty(name = "jasypt.encryptor.algorithm", havingValue = "cyberark", matchIfMissing = false)
public class CyberarkConfiguration {

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor(final CyberarkClient cyberarkClient) {
        return new StringEncryptor() {
            public String encrypt(String s) {
                return null;
            }

            public String decrypt(String s) {
                return cyberarkClient.getPassword(s);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "cyberarkClient")
    public CyberarkClient cyberarkClient(RestTemplate restTemplate, CyberarkProperties cyberarkProperties) {
        return new CyberarkClient(
                restTemplate,
                cyberarkProperties.getUrl(),
                cyberarkProperties.getAppId(),
                cyberarkProperties.getSafe(),
                cyberarkProperties.getFolder(),
                cyberarkProperties.getKey()
        );
    }
}
