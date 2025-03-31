package com.happiness.budtree.domain.chatroom;

import com.happiness.budtree.domain.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatroom")
@Getter
public class Chatroom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    public Chatroom() {}

    @Builder
    public Chatroom(Member member) {
        this.member = member;
        this.createdDate = LocalDateTime.now();
    }

}
