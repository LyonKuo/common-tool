package com.common.cyberark.client.exception;

/**
 * 调用cyberark出错
 */
public class CyberarkException extends RuntimeException {

    public CyberarkException(Exception e) {
        super("Invoke Cyberark error", e);
    }

    public CyberarkException(String code, String msg) {
        super("Invoke Cyberark error, code = " + code + ", msg = " + msg);
    }
}
