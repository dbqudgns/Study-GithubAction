package com.happiness.budtree.domain.chatroom.DTO.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatroomAllRP(
        Long roomId,
        LocalDateTime createdTime

) {
}
