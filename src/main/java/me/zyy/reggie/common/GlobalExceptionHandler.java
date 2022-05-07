package me.zyy.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 *  全局异常处理
 */

@ControllerAdvice(annotations = {RestController.class, Controller.class}) // 对于哪些注解的类做处理
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class}) // 处理哪种异常
    public R<String> exception(SQLIntegrityConstraintViolationException e) {
        log.error(e.getMessage());
        return e.getMessage().contains("Duplicate entry") ?
                R.error(0, "已经存在了，换个名字吧") : R.error(0, "发生未知错误");
    }

    @ExceptionHandler({CustomException.class}) // 处理哪种异常
    public R<String> exception(CustomException e) {
        log.error(e.getMessage());
        return R.error(0, e.getMessage());
    }
}
