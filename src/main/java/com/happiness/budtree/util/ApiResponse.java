package com.happiness.budtree.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

public class ApiResponse {

    //API 성공 or 실패시 응답하는 메서드
    public static Map<String, Object> SuccessOrFail(Object code, String msg) {

        Map<String, Object> map = new HashMap<>();

        map.put("status", code);
        map.put("message", msg);

        return map;
    }

    //인증되지 않은 사용자에게 보내는 메서드
    public static ResponseEntity<?> Unauthorized(String msg) {

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", 401);
        responseData.put("message", msg);

        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(responseData);

    }


}
