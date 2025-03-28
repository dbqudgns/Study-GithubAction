package com.happiness.budtree.domain.member.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record MemberChangePWRQ (

        @NotBlank(message = "필수 입력 항목입니다.")
        String newPassword,

        @NotBlank(message = "필수 입력 항목입니다.")
        String verifyPassword

) {

    public boolean checkPassword() {
        return newPassword.equals(verifyPassword);
    }

}
