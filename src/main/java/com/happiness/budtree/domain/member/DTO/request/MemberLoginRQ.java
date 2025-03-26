package com.happiness.budtree.domain.member.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record MemberLoginRQ(

        @NotBlank(message = "아이디는 필수 입력 항목입니다.")
        String username,

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        String password
) {
}
