package com.common.utils.web.controller;

import com.common.api.Response;
import com.common.api.ResponseCode;
import com.common.api.response.CommonResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 统一异常处理类
 *
 * @author lyon
 * @since 1.1.0
 */
@Slf4j
@RestControllerAdvice
public class DefaultExceptionHandler {

    /**
     * 处理Throwable异常
     *
     * @param t
     * @return
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.OK)
    public Response<Void> handle(Throwable t) {
        log.error("unexpected exception: {}", t);
        return Response
                .failure(CommonResponseCode.INTERNAL_SERVER_ERROR)
                .setMessage(t.getMessage());
    }

    /**
     * 处理Exception异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Response<Void> handle(Exception e) {
        log.error("unexpected exception: {}", e);
        return Response
                .failure(CommonResponseCode.INTERNAL_SERVER_ERROR)
                .setMessage(e.getMessage());
    }

    /**
     * 处理进入controller之前的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MissingServletRequestPartException.class,
            BindException.class,
//            NoHandlerFoundException.class,
            AsyncRequestTimeoutException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<Void> handleSpringFrameworkException(Exception e) {
        log.error("unexpected exception: {}", e);

        String message = String.format(
                CommonResponseCode.BAD_REQUEST_PARAMETER_INVALID.message(), e.getMessage()
        );
        return Response
                .failure(CommonResponseCode.BAD_REQUEST_PARAMETER_INVALID)
                .setMessage(message);
    }

    /**
     * 处理参与验证相关的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response<Void> handle(ConstraintViolationException e) {
        // 获取具体的错误信息
        String message = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","));
        return Response.failure(CommonResponseCode.BAD_REQUEST_PARAMETER_INVALID).setMessage(message);
    }

    /**
     * 处理参与验证相关的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response<Void> handle(WebExchangeBindException e) {
        // 获取具体的错误信息
        String message = e.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(","));
        return Response.failure(CommonResponseCode.BAD_REQUEST_PARAMETER_INVALID).setMessage(message);
    }
}
