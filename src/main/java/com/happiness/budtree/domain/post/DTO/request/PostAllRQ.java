package com.happiness.budtree.domain.post.DTO.request;

import jakarta.validation.constraints.NotNull;

public record PostAllRQ(

        @NotNull(message = "전체 조회시 0을 보내주세요")
        Integer year,

        @NotNull(message = "전체 조회 시 0을 보내주세요")
        Integer month
) {
}
