package com.happiness.budtree.domain.message;

import lombok.Getter;

public enum SenderType {

    MEMBER("사용자"),
    BUDDY("GPT");

    @Getter
    private String value;

    SenderType(String value) {
        this.value = value;
    }
}
