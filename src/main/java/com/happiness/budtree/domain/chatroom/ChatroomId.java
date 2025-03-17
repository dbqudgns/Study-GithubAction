package com.happiness.budtree.domain.chatroom;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

//Chatroom 엔티티 복합 키 설정 : 식별자 클랠스
@Embeddable
public class ChatroomId implements Serializable {

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "member_id")
    private Long memberId;

}
