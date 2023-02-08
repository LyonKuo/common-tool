package com.common.cyberark.springboot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jasypt.encryptor.cyberark", ignoreUnknownFields = true)
public class CyberarkProperties {

    private String url;

    private String appId;

    private String safe;

    private String folder;

    private String key;

    private Integer connectionMaxTotal;

    private Integer connectionDefaultMaxPerRoute;

    private Integer connectionConnectTimeout;

    private Integer connectionReadTimeout;

    private Integer connectionRequestTimeout;
}
