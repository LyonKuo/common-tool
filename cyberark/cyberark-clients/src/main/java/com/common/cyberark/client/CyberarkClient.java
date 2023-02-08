package com.common.cyberark.client;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import com.common.cyberark.client.dto.GetPasswordRequest;
import com.common.cyberark.client.dto.GetPasswordResponse;
import com.common.cyberark.client.exception.CyberarkException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class CyberarkClient {

    private static final String HTTP_RESPONSE_CODE = "200";

    private RestTemplate restTemplate = null;

    private String url;

    private String appId;

    private String safe;

    private String folder;

    private String key;

    public CyberarkClient(
            @NonNull RestTemplate restTemplate,
            @NonNull String url,
            @NonNull String appId,
            @NonNull String safe,
            @NonNull String folder,
            @NonNull String key
    ) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.appId = appId;
        this.safe = safe;
        this.folder = folder;
        this.key = key;

        Objects.requireNonNull(restTemplate, "parameter[restTemplate] can't be null");
        Objects.requireNonNull(url, "parameter[url] can't be null");
        Objects.requireNonNull(appId, "parameter[appId] can't be null");
        Objects.requireNonNull(safe, "parameter[safe] can't be null");
        Objects.requireNonNull(folder, "parameter[folder] can't be null");
        Objects.requireNonNull(key, "parameter[key] can't be null");
    }

    /**
     * 获取 cyberark 中配置的明文密码
     * @param object
     * @return
     */
    public String getPassword(String object) {
        if (!StringUtils.hasLength(object)) {
            return null;
        }
        try {
            GetPasswordRequest request = new GetPasswordRequest()
                    .setAppId(appId)
                    .setSafe(safe)
                    .setFolder(folder)
                    .setObject(object)
                    .setSign(DigestUtils.sha1Hex(appId + '&' + key));

            GetPasswordResponse response = restTemplate.postForObject(url, request, GetPasswordResponse.class);

            log.info("Call cyberark getPassword: code = [{}], msg = [{}]", response.getCode(), response.getMsg());

            if (CyberarkClient.HTTP_RESPONSE_CODE.equals(response.getCode())) {
                return SecureUtil
                        .aes(key.getBytes(StandardCharsets.UTF_8))
                        .decryptStr(HexUtil.decodeHex(response.getPassword()));
            }

            throw new CyberarkException(response.getCode(), response.getMsg());
        } catch (Exception t) {
            throw new CyberarkException(t);
        }
    }
}
