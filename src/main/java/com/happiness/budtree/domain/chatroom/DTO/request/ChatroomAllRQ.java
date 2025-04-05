package com.happiness.budtree.domain.chatroom.DTO.request;

import jakarta.validation.constraints.NotNull;

public record ChatroomAllRQ(

        @NotNull(message = "필수 입력 항목입니다. 전체 조회시 0을 보내주세요")
        Integer year,

        @NotNull(message = "필수 입력 항목입니다. 전체 조회시 0을 보내주세요")
        Integer month
) {
}
