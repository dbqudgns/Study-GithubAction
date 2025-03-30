package com.happiness.budtree.util;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

public class ApiResponse {

    //API 성공 시 실행
    public static <T> ApiSuccess<T> success(T response) {
        return new ApiSuccess<>(200, response);
    }

    //API 실패 시 실행
    public static ApiFail fail(int status, String message) {
        return new ApiFail(status, message);
    }

    @Getter
    @AllArgsConstructor
    public static class ApiSuccess<T> {
        private final int status;
        private final T message;
    }

    @Getter
    @AllArgsConstructor
    public static class ApiFail {
        private final int status;
        private final String message;
    }

    // Servlet :  인증되지 않은 사용자에게 보내는 메서드
    public static ResponseEntity<?> Unauthorized(String msg) {

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", 401);
        responseData.put("message", msg);

        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(responseData);

    }


}
