package com.happiness.budtree.domain.post;

import com.happiness.budtree.domain.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@Table(name = "post")
public class Post {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "created_date",nullable = false)
    private LocalDateTime createDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Emotion emotion;

    @Lob
    private String content;

    @Builder
    public Post(String content, Emotion emotion, Member member) {
        this.emotion = emotion;
        this.content = content;
        this.createDate = LocalDateTime.now();
        this.member = member;
    }

    public void updatePost(String content, Emotion emotion) {
        this.content = content;
        this.emotion = emotion;
    }

}
