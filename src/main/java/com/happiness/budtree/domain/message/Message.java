package com.happiness.budtree.domain.message;

import com.happiness.budtree.domain.chatroom.Chatroom;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Getter
public class Message {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "room_id", referencedColumnName = "room_id"),
            // referencedColumnName : chatroom 테이블에서 참조할 속성 => room_id를 참조
            @JoinColumn(name = "member_id", referencedColumnName = "member_id")
            // referencedColumnName : chatroom 테이블에서 참조할 속성 => member_id를 참조
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Chatroom chatroom;

    @Enumerated(EnumType.STRING)
    private SenderType senderType;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdDate;

}
