package com.happiness.budtree.domain.message;

import com.happiness.budtree.domain.chatroom.Chatroom;
import jakarta.persistence.*;
import lombok.Builder;
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
    @JoinColumn(name = "room_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Chatroom chatroom;

    @Enumerated(EnumType.STRING)
    private SenderType senderType;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Builder
    public Message(Chatroom chatroom, SenderType senderType, String content, LocalDateTime createdDate) {
        this.chatroom = chatroom;
        this.senderType = senderType;
        this.content = content;
        this.createdDate = createdDate;
    }

    public Message() {

    }
}
