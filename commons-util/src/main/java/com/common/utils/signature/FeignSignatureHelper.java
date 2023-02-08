package com.common.utils.signature;

import cn.hutool.core.net.URLDecoder;
import feign.RequestTemplate;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * the help class sign feign request
 * @author lyon
 * @since 1.0.2
 */
public class FeignSignatureHelper {

    private FeignSignatureHelper() {

    }
    private static final String HTTP_HEADER_SIGN_AK = "x-sec-sign-ak";
    private static final String HTTP_HEADER_SIGN_NONCE = "x-sec-sign-nonce";
    private static final String HTTP_HEADER_SIGN_TIMESTAMP = "x-sec-sign-timestamp";

    private static final String HTTP_HEADER_SIGN_SIGNATURE = "Authorization";

    /**
     * sign the feign request object
     * @param template
     */
    public static void sign(String ak, String sk, RequestTemplate template) {
        final String nonce = LocalDateTime.now().toString();
        final Long timestamp = System.currentTimeMillis();

        // get the content to be sign
        String method = template.method().toUpperCase();
        String uri = template.path();
        Map<String, String> parameters = template.queries()
                .entrySet()
                .stream()
                .map(entry -> Pair.of(entry.getKey(), join(entry.getValue(), ",")))
                .map(entry -> Pair.of(entry.getKey(), URLDecoder.decode(entry.getValue(), StandardCharsets.UTF_8)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        byte[] bytes = template.body();
        String body = null;
        if(bytes != null) {
            body = new String(bytes, StandardCharsets.UTF_8);
        }

        // sign it
        String signature = new Signature.Builder()
                .ak(ak)
                .nonce(nonce)
                .timestamp(timestamp)
                .method(method)
                .uri(uri)
                .parameters(parameters)
                .payload(body)
                .build()
                .signHmacSha1(sk);

        // set http header
        template.header(HTTP_HEADER_SIGN_AK, ak);
        template.header(HTTP_HEADER_SIGN_NONCE, nonce);
        template.header(HTTP_HEADER_SIGN_TIMESTAMP, String.valueOf(timestamp));

        template.header(HTTP_HEADER_SIGN_SIGNATURE, signature);
    }

    private static String join(Collection<String> collection, String joiner) {
        return collection
                .stream()
                .collect(Collectors.joining(joiner));
    }
}
