package com.happiness.budtree.domain.chatroom.DTO.response;

import lombok.Builder;

@Builder
public record ChatroomIdRP(
        String name,
        Long roomId
) {
}
