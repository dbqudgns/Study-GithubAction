package com.happiness.budtree.domain.post.DTO.response;

import com.happiness.budtree.domain.post.Emotion;
import lombok.Builder;


//나무에 걸려있는 감정. 감정 상태 및 PostId필요
@Builder
public record PostEmotionRP(
        Long postId,

        Emotion emotion
) {
}
