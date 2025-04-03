package com.happiness.budtree.domain.post.DTO.response;

import com.happiness.budtree.domain.post.Emotion;
import lombok.Builder;

import java.time.LocalDateTime;

//전체 일기장 조회시 -> 감정,생성날짜만, 조회 후 일기장 수정시 - postId값 필요.
@Builder
public record PostAllRP(
        Long postId,
        Emotion emotion,
        LocalDateTime createdDate
) {
}
