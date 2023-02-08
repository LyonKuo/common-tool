package com.common.cyberark.springboot;

import com.common.cyberark.springboot.configuration.CyberarkConfiguration;
import com.ulisesbocchio.jasyptspringboot.configuration.EnableEncryptablePropertiesConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({EnableEncryptablePropertiesConfiguration.class, CyberarkConfiguration.class})
public class JasyptCyberarkSpringBootAutoConfiguration {

}