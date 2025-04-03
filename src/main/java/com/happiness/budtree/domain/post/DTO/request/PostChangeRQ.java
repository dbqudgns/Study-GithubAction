package com.happiness.budtree.domain.post.DTO.request;

import com.happiness.budtree.domain.post.Emotion;

public record PostChangeRQ(
        String content,

        Emotion emotion
) {
}
