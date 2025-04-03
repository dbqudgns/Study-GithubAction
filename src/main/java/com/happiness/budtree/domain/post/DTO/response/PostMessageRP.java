package com.happiness.budtree.domain.post.DTO.response;

import com.happiness.budtree.domain.post.Emotion;
import lombok.Builder;

import java.time.LocalDateTime;

//특정 일기장 조회시, 일기장 내용, 감정, 생성일 나오게, 추후 일기장 수정 및 감정 열매 내용에 확인
@Builder
public record PostMessageRP(
        Long postId,
        String content,
        Emotion emotion,
        LocalDateTime createdDate
        ) {
}
