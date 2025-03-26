package com.happiness.budtree.exception;


import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//Spring Boot 3.4.0 부터 ControllerAdviceBean의 init 함수가 존재하지 않는다고 나옴
//Swagger가 예외 처리 컨트롤러(@RestControllerAdvice)를 자동으로 API 문서에 포함하는 동작이 변경되어 충돌이 일어난 것으로 보임
@Hidden
@RestControllerAdvice(basePackages = {"com.happiness.budtree.domain"})
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResult> notEnoughData(IllegalArgumentException e) {

        ErrorResult errorResult = ErrorResult.builder()
                .status(400)
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);

    }
}
