package com.common.utils.exception;

import com.common.api.ResponseCode;
import lombok.Getter;

/**
 * 可传递异常<br>
 * 异常在处理过程中，可持续传播，包括跨模块调用的场景支持。
 * @Author lyon
 * @Date 2022/5/18 3:47 PM
 * @Since 1.0.8
 **/
@Getter
public class TransmissionException extends BizException{
    private Integer code;
    private String message;

    /**
     * 创建预定义异常
     * @param responseCode
     * @param placeholderReplaces 有些message里有占位符，需要传入替换的值
     */
    public TransmissionException(ResponseCode responseCode, String... placeholderReplaces) {
        this(responseCode.code(), parseMsg(responseCode.message(), placeholderReplaces));
    }

    /**
     * 捕获下级异常，如果下级异常不是ConductiveException，则使用responseCode的预定义异常
     *
     * @param responseCode
     * @param e
     * @param placeholderReplaces 有些message里有占位符，需要传入替换的值
     */
    public TransmissionException(ResponseCode responseCode, Exception e, String... placeholderReplaces) {
        super(e);
        if (e instanceof TransmissionException) {
            TransmissionException ex = (TransmissionException) e;
            this.code = ex.getCode();
            this.message = ex.getMessage();
        } else {
            this.code = responseCode.code();
            this.message = parseMsg(responseCode.message(), placeholderReplaces);
        }
    }

    /**
     * 调用其他模块时封装用，将Response的code和msg直接传递过来
     * 内部调用不建议使用该构造方法，请使用responseCode
     *
     * @param code
     * @param message
     */
    public TransmissionException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 自定义转换，用于第三方调用中的code和message映射
     * @param convertor
     * @param code
     * @param message
     */
    public TransmissionException(ExceptionConvertor convertor, Integer code, String message) {
        ResponseCode responseCode = convertor.convert(code, message);
        this.code = responseCode.code();
        this.message = responseCode.message();
    }


    private static String parseMsg(String msg, String...placeholderReplaces) {
        return String.format(msg, placeholderReplaces);
    }

}
