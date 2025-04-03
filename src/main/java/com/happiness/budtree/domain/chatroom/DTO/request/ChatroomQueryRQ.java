package com.happiness.budtree.domain.chatroom.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record ChatroomQueryRQ(
        @NotBlank(message = "사용자의 요청문은 필수 입니다.")
        String query
) {
}
