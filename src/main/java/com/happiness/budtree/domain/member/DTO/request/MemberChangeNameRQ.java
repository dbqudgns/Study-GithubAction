package com.happiness.budtree.domain.member.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record MemberChangeNameRQ(
                @NotBlank(message = "필수 입력 항목입니다.")
                String name)
{}
