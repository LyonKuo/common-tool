package com.common.utils.exception;

import com.common.api.ResponseCode;

/**
 * 统一异常
 *
 * @author lyon
 * @since 1.1.0
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected Integer code;

    /**
     * 错误信息
     */
    protected String message;

    public BizException() {
        super();
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(ResponseCode responseCode) {
        super(String.valueOf(responseCode.code()));
        this.code = responseCode.code();
        this.message = responseCode.message();
    }

    public BizException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.message(), cause);
        this.code = responseCode.code();
        this.message = responseCode.message();
    }

    public BizException(String message) {
        super(message);
        this.message = message;
    }

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
