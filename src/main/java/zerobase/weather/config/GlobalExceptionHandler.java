package zerobase.weather.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler { // @RestControllerAdvice전역 예외. 일단 예외 발생하면 모두 모우고. 나머지는 클래스안에 함수 핸들러가 처리함.

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Exception handleAllException() { //@ExceptionHandler(Exception.class):모든 예외에 대해 처리하기위해 클래스 이렇게 지정 . 컨트롤러 안에있는 예외처리함.
        //예외발생시 어떤 상태를 반환할꺼냐..@ResponseStatus
        System.out.println("error from GlobalExceptionHandler");
        return new Exception();
    }
}
