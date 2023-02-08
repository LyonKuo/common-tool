package com.common.utils.signature;

import cn.hutool.crypto.digest.DigestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.StringJoiner;

/**
 * @author lyon
 * @description
 * @create 2022/6/22
 */
public class SignatureHelper {
    public static final String APP_ID = "App-Id";
    public static final String REQUEST_ID = "Request-Id";
    public static final String SIGN = "Sign";

    public static boolean validateSign(HttpServletRequest request, String isecAppKey) throws Exception {
        String isecAppId = request.getHeader(APP_ID);
        String isecRequestId = request.getHeader(REQUEST_ID);
        String isecSign = request.getHeader(SIGN);

        String origin = new StringJoiner("@@")
                .add(isecAppId)
                .add(isecAppKey)
                .add(isecRequestId)
                .toString();
        String sign = DigestUtil.sha1Hex(origin);
        return sign.equals(isecSign);
    }
}
