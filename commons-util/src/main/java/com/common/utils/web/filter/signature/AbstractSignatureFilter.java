package com.common.utils.web.filter.signature;

import cn.hutool.core.io.IoUtil;
import com.common.utils.json.JsonUtils;
import com.common.utils.signature.Signature;
import com.common.api.Response;
import com.common.api.response.CommonResponseCode;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lyon
 * @since 1.0.1
 */
public abstract class AbstractSignatureFilter implements Filter {

    private static final String HTTP_HEADER_SIGN_AK = "x-sec-sign-ak";
    private static final String HTTP_HEADER_SIGN_NONCE = "x-sec-sign-nonce";
    private static final String HTTP_HEADER_SIGN_TIMESTAMP = "x-sec-sign-timestamp";

    private static final String HTTP_HEADER_SIGN_SIGNATURE = "Authorization";

    private boolean enable;

    protected AbstractSignatureFilter(boolean enable) {
        this.enable = enable;
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain
    ) throws IOException, ServletException {

        if(!this.enable) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String ak = request.getHeader(HTTP_HEADER_SIGN_AK);
        String nonce = request.getHeader(HTTP_HEADER_SIGN_NONCE);
        String timestampString = request.getHeader(HTTP_HEADER_SIGN_TIMESTAMP);
        String signature = request.getHeader(HTTP_HEADER_SIGN_SIGNATURE);

        // validate the sign parameters in the HTTP header
        // validate HTTP_HEADER_SIGN_AK
        if (StringUtils.isEmpty(ak)) {
            this.response(
                    response,
                    Response.failure(CommonResponseCode.BAD_REQUEST_HEADER_PARAMETER_MISSING, null, HTTP_HEADER_SIGN_AK)
            );
            return;
        }

        // validate HTTP_HEADER_SIGN_NONCE
        if (StringUtils.isEmpty(nonce)) {
            this.response(
                    response,
                    Response.failure(CommonResponseCode.BAD_REQUEST_HEADER_PARAMETER_MISSING, null, HTTP_HEADER_SIGN_NONCE)
            );
            return;
        }

        // validate HTTP_HEADER_SIGN_TIMESTAMP
        long timestamp;
        if (StringUtils.isEmpty(timestampString)) {
            this.response(
                    response,
                    Response.failure(CommonResponseCode.BAD_REQUEST_HEADER_PARAMETER_MISSING, null, HTTP_HEADER_SIGN_TIMESTAMP)
            );
            return;
        }
        try {
            timestamp = Long.parseLong(timestampString);
        } catch (NumberFormatException nfe) {
            this.response(
                    response,
                    Response.failure(CommonResponseCode.BAD_REQUEST_HEADER_PARAMETER_INVALID, null, HTTP_HEADER_SIGN_TIMESTAMP)
            );
            return;
        }

        // validate HTTP_HEADER_SIGN_SIGNATURE
        if (StringUtils.isEmpty(signature)) {
            this.response(
                    response,
                    Response.failure(CommonResponseCode.BAD_REQUEST_HEADER_PARAMETER_MISSING, null, HTTP_HEADER_SIGN_SIGNATURE)
            );
            return;
        }

        // get the content to be sign
        String method = request.getMethod().toUpperCase();
        String uri = request.getRequestURI();
        Map<String, String> parameters = getParameters(request);

        // wrapper the HTTP request
        request = new BufferedServletRequestWrapper(request);
        String body = getBody(request);

        // get the sk
        String sk = getAccessKeySecret(ak);
        if(StringUtils.isEmpty(sk)) {
            this.response(
                    response,
                    Response.failure(CommonResponseCode.BAD_REQUEST_SECURITY_APPID_INVALID, null, ak)
            );
            return;
        }

        // sign it
        String signed = new Signature.Builder()
                .ak(ak)
                .nonce(nonce)
                .timestamp(timestamp)
                .method(method)
                .uri(uri)
                .parameters(parameters)
                .payload(body)
                .build()
                .signHmacSha1(sk);

        if(!signature.equals(signed)) {
            this.response(response, Response.failure(CommonResponseCode.UNAUTHORIZED_SYS_SIGN_INVALID));
            return;
        }

        //
        filterChain.doFilter(request, servletResponse);
    }

    private void response(HttpServletResponse httpServletResponse, Response<?> response) throws IOException {
        httpServletResponse.setStatus(200);
        httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
        httpServletResponse.getWriter().println(JsonUtils.writeValueAsString(response));
        httpServletResponse.flushBuffer();
    }

    private Map<String, String> getParameters(HttpServletRequest request) {
        Enumeration<String> enumeration = request.getParameterNames();
        String key;
        String value;
        Map<String, String> map = new HashMap<>(8);
        while (enumeration.hasMoreElements()) {
            key = enumeration.nextElement();
            value = request.getParameter(key);
            map.put(key, value);
        }

        return map;
    }

    private String getBody(HttpServletRequest request) throws IOException {
        return IoUtil.read(request.getInputStream(), StandardCharsets.UTF_8);
    }



    /**
     * 根据accessKeySecret 查询对应 securityKey
     * @param accessKeySecret ak和sk中的ak
     * @return 对应的securityKey（ak和sk中的sk）
     */
    protected abstract String getAccessKeySecret(String accessKeySecret);
}
