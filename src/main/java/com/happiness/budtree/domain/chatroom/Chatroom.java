package com.happiness.budtree.domain.chatroom;

import com.happiness.budtree.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatroom")
@Getter
public class Chatroom {

    @EmbeddedId
    private ChatroomId chatroomId;

    @MapsId("memberId") //ChatroomId.memberId와 중복 삽입을 방지하기 위해 식별자 클래스(ChatroomId)의 ID와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private LocalDateTime createdDate;

}
