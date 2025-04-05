package com.happiness.budtree.domain.chatroom.DTO.response;

import com.happiness.budtree.domain.message.SenderType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatroomMessageRP(
        SenderType sender,
        String content,
        LocalDateTime createdDate
) {
}
