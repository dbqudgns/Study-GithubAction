package com.happiness.budtree.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

//Spring Boot 3.4.0 부터 ControllerAdviceBean의 init 함수가 존재하지 않는다고 나옴
//Swagger가 예외 처리 컨트롤러(@RestControllerAdvice)를 자동으로 API 문서에 포함하는 동작이 변경되어 충돌이 일어난 것으로 보임
@Hidden
@RestControllerAdvice(basePackages = {"com.happiness.budtree.domain", "com.happiness.budtree.util"})
public class GlobalExceptionHandler {

    //DTO Validation 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        for(FieldError error : e.getBindingResult().getFieldErrors())
            errors.put(error.getField(), error.getDefaultMessage());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResult> notEnoughData(IllegalArgumentException e) {

        ErrorResult errorResult = ErrorResult.builder()
                .status(400)
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResult> notFoundMember(UsernameNotFoundException e) {

        ErrorResult errorResult = ErrorResult.builder()
                .status(404)
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResult> notFoundEntity(EntityNotFoundException e) {

        ErrorResult errorResult = ErrorResult.builder()
                .status(404)
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResult> notUseResource(AccessDeniedException e) {

        ErrorResult errorResult = ErrorResult.builder()
                .status(400)
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }
}
