package com.common.utils.signature;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 签名算法：<br />
 *  SignString =
 *       method + '\n' +
 *       uri + '\n' +
 *       queryString + '\n' +
 *       signedHeaderString + '\n' +
 *       base64(md5(payload))
 * 其中：<br />
 *  method：HTTP请求的方法名称<br />
 *      1. GET<br />
 *  uri：HTTP请求资源路径<br />
 *      1. ex: /v1/77b6a44cba5143ab91d13ab9a8ff44fd/vpcs<br />
 *  queryString：查询字符串<br />
 *      1. 如果没有查询参数，则为空字符串<br />
 *      2. 按照字符代码以升序顺序对参数名进行排序。例如，以大写字母F开头的参数名排在以小写字母b开头的参数名之前<br />
 *      3. ex: parm1=value1&parm2=<br />
 *  signedHeaderString：HTTP头中跟签名相关的部分<br />
 *      1. ak: **x-sec-sign-ak**<br />
 *      2. nonce（生成时需要保证短时间内生成 nonce 的唯一性）：**x-sec-sign-nonce**<br />
 *      3. timestamp（毫秒级别的时间戳）：**x-sec-sign-timestamp**<br />
 *      4. ex: x-sec-sign-ak=fjosdnieow&x-sec-sign-nonce=bsdb&x-sec-sign-timestamp=57384902384<br />
 *  base64(md5(payload))<br />
 *      1. 使用md5哈希函数以基于HTTP或HTTPS请求正文中的body体（**payload**），创建哈希值<br />
 *      2. 对于“payload==null”的场景，直接使用空字符串""来计算<br />
 *
 * @author lyon
 * @since 1.0.0
 */
public class Signature {

    public final static String HTTP_HEADER_SIGN_AK = "x-sec-sign-ak";
    public final static String HTTP_HEADER_SIGN_NONCE = "x-sec-sign-nonce";
    public final static String HTTP_HEADER_SIGN_TIMESTAMP = "x-sec-sign-timestamp";

    public final static String HTTP_HEADER_SIGN_SIGNATURE = "Authorization";

    private final static String SPLITTER_SECTION = "\n";
    private final static String SPLITTER_EXPRESSION = "&";
    private final static String SPLITTER_KV = "=";

    /**
     * access key
     */
    private String ak;

    /**
     * 随机数
     */
    private String nonce;

    /**
     * 当前时间戳
     */
    private Long timestamp;

    /**
     * HTTP请求的方法名称
     */
    private String method;

    /**
     * HTTP请求资源路径，是URI的绝对路径部分的URI编码
     */
    private String uri;

    /**
     * HTTP请求查询字符串
     */
    private Map<String, String> parameters;

    /**
     * HTTP请求体
     */
    private String payload;

    /**
     * 生成的待签名的部分
     */
    private String unsigned;


    private Signature() {

    }

    public String getAk() {
        return ak;
    }

    public String getNonce() {
        return nonce;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Signature{" +
                "unsigned='" + unsigned + '\'' +
                '}';
    }

    /**
     * 签名
     * @return
     */
    public String signHmacSha1(String sk) {
        return SecureUtil.hmacSha1(sk).digestHex(unsigned);
    }

    /**
     * @author lyon
     */
    public static class Builder {

        /**
         * access key
         */
        private String ak;

        /**
         * 随机数
         */
        private String nonce;

        /**
         * 当前时间戳
         */
        private Long timestamp;

        /**
         * HTTP请求的方法名称
         */
        private String method;

        /**
         * HTTP请求资源路径，是URI的绝对路径部分的URI编码
         */
        private String uri;

        /**
         * HTTP请求查询字符串
         */
        private Map<String, String> parameters;

        /**
         * HTTP请求体
         */
        private String payload;


        public Builder ak(String ak) {
            this.ak = ak;
            return this;
        }

        public Builder nonce(String nonce) {
            this.nonce = nonce;
            return this;
        }

        public Builder timestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder parameters(Map<String, String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        /**
         * 生成Signature对象
         * @return
         */
        public Signature build() {
            Objects.requireNonNull(this.ak, "ak can't be null");
            Objects.requireNonNull(this.nonce, "nonce can't be null");
            Objects.requireNonNull(this.timestamp, "timestamp can't be null");

            Objects.requireNonNull(this.method, "method can't be null");

            Objects.requireNonNull(this.uri, "uri can't be null");

            Signature signature = new Signature();
            signature.ak = this.ak;
            signature.nonce = this.nonce;
            signature.timestamp = this.timestamp;
            signature.method = this.method;
            signature.uri = this.uri;
            signature.parameters = this.parameters;
            signature.payload = this.payload;

            // generate the signature content to be sign
            String unsigned = new StringBuilder()
                    .append(method).append(SPLITTER_SECTION)
                    .append(uri).append(SPLITTER_SECTION)
                    .append(buildQueryString(parameters)).append(SPLITTER_SECTION)
                    .append(buildHeaderString(ak, nonce, timestamp)).append(SPLITTER_SECTION)
                    .append(buildMd5Body(payload))
                    .toString();
            signature.unsigned = unsigned;
            
            return signature;
        }

        private String buildQueryString(Map<String, String> parameters) {
            if(parameters == null || parameters.isEmpty()) {
                return "";
            }

            SortedMap<String, String> map = new TreeMap<>();
            parameters.entrySet()
                    .forEach(entry -> map.put(entry.getKey(), entry.getValue()));

            return map.entrySet()
                    .stream()
                    .map(entry -> new StringJoiner(SPLITTER_KV).add(entry.getKey()).add(entry.getValue()).toString())
                    .collect(Collectors.joining(SPLITTER_EXPRESSION));
        }

        private String buildHeaderString(String ak, String nonce, Long timestamp) {
            return new StringBuilder()
                    .append(HTTP_HEADER_SIGN_AK).append(SPLITTER_KV).append(ak).append(SPLITTER_EXPRESSION)
                    .append(HTTP_HEADER_SIGN_NONCE).append(SPLITTER_KV).append(nonce).append(SPLITTER_EXPRESSION)
                    .append(HTTP_HEADER_SIGN_TIMESTAMP).append(SPLITTER_KV).append(timestamp)
                    .toString();
        }

        private String buildMd5Body(String body) {
            if(body == null) {
                body = "";
            }
            return DigestUtil.md5Hex(body);
        }
    }
}
