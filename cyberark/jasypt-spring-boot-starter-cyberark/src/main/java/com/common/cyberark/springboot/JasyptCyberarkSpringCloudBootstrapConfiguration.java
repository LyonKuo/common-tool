package com.common.cyberark.springboot;

import com.common.cyberark.springboot.configuration.CyberarkConfiguration;
import com.ulisesbocchio.jasyptspringboot.configuration.EnableEncryptablePropertiesConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(name = "jasypt.encryptor.bootstrap", havingValue = "true", matchIfMissing = true)
@Import({EnableEncryptablePropertiesConfiguration.class, CyberarkConfiguration.class})
public class JasyptCyberarkSpringCloudBootstrapConfiguration {

}
