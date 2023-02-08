package com.common.cyberark.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class CyberarkClientTest {

    private CyberarkClient client = null;

    public CyberarkClientTest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        client = new CyberarkClient(
                restTemplate,
                "https://xxx/pidms/rest/pwd/getPassword",
                "App_ZEUS_ALERTCENTER__8e83f2",
                "AIM_ZEUS_ALERTCENTER",
                "root",
                "25736f60b29229d6"
        );
    }

    @Test
    public void getPassword() {
        String password = client.getPassword("postgresql-d0zeus-bmpasset-bmpassetopr");
        Assertions.assertEquals("Mlive_1234", password);
    }
}
