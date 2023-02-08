package com.common.utils.exception;

import com.common.api.ResponseCode;
import lombok.AllArgsConstructor;

/**
 * @ClassName ExceptionConvertor
 * @Author lyon
 * @Date 2022/4/27 3:54 PM
 * @Version 1.0.9
 **/
public interface ExceptionConvertor {

    /**
     * 将第三方的code与message转换为系统内定义的异常
     * @param code
     * @param msg
     * @return
     */
    ResponseCode convert(Integer code, String msg);


    /**
     * responseCode接口的一个bean实现
     */
    @AllArgsConstructor
    class ResponseCodeBean implements ResponseCode {

        private int code;

        private String message;

        @Override
        public Integer code() {
            return code;
        }

        @Override
        public String message() {
            return message;
        }
    }


}
