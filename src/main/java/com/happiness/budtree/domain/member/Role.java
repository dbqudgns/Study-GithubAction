package com.happiness.budtree.domain.member;

import lombok.Getter;

public enum Role {

    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    @Getter
    private String value;

    Role(String value) {
        this.value = value;
    }

}
