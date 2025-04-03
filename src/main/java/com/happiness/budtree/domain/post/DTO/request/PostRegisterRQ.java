package com.happiness.budtree.domain.post.DTO.request;

import com.happiness.budtree.domain.post.Emotion;
import jakarta.validation.constraints.NotBlank;

public record PostRegisterRQ(
        String content,
        
        @NotBlank(message = "감정 선택해주세요")
        Emotion emotion


) {
}
