package com.believe.common.exception;

import com.believe.common.core.constants.ErrorCode;
import com.believe.common.core.exception.BelieveException;
import com.believe.common.core.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BelieveException.class)
    public ResponseEntity<Result<Void>> handleBelieveException(BelieveException e, HttpServletRequest request) {
        log.warn("业务异常 [{}] {} - {}", e.getCode(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(mapHttpStatus(e.getCode()))
                .body(Result.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败 [{}] {}", request.getRequestURI(), msg);
        return ResponseEntity.badRequest()
                .body(Result.error(ErrorCode.PARAM_INVALID, msg));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Void>> handleMissingParam(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少请求参数 [{}] {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(Result.error(ErrorCode.PARAM_MISSING, e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("请求体解析失败 [{}]", request.getRequestURI());
        return ResponseEntity.badRequest()
                .body(Result.error(ErrorCode.PARAM_INVALID, "请求体格式错误"));
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<Result<Void>> handleNotFound(Exception e, HttpServletRequest request) {
        log.warn("资源不存在 [{}]", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Result.error(ErrorCode.BIZ_DATA_NOT_FOUND, "资源不存在"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求方法不支持 [{}] {}", request.getRequestURI(), e.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Result.error(ErrorCode.PARAM_INVALID, "请求方法不支持: " + e.getMethod()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常 [{}] {}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(ErrorCode.SYS_GENERAL, "系统内部错误"));
    }

    private HttpStatus mapHttpStatus(int code) {
        int category = code / 1000;
        return switch (category) {
            case 1 -> HttpStatus.BAD_REQUEST;
            case 2 -> HttpStatus.BAD_REQUEST;
            case 3 -> HttpStatus.INTERNAL_SERVER_ERROR;
            case 4 -> switch (code) {
                case ErrorCode.AUTH_UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
                case ErrorCode.AUTH_FORBIDDEN -> HttpStatus.FORBIDDEN;
                default -> HttpStatus.FORBIDDEN;
            };
            case 5 -> HttpStatus.BAD_GATEWAY;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
