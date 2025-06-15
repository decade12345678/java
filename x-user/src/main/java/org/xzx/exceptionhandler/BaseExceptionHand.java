package org.xzx.exceptionhandler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.xzx.Result.Result;
import org.xzx.exception.BaseException;

@RestControllerAdvice
public class BaseExceptionHand {
    @ExceptionHandler
    public Result ExceptionHandler(BaseException e) {
        return Result.error(e.getMessage());
    }
}
