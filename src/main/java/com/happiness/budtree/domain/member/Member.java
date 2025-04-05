package com.happiness.budtree.domain.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;


@Entity
@Table(name = "member")
@Getter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public Member() {}

    @Builder
    public Member(String name, String username, String password, Role role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

}
