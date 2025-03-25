package com.happiness.budtree.domain.post;

import com.happiness.budtree.domain.member.Member;
import jakarta.persistence.*;
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
    @Column(nullable = false)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Emotion emotion;

    @Column(columnDefinition = "TEXT")
    private String content;

    public void updatePost(String content, Emotion emotion){
        this.content = content;
        this.emotion = emotion;
    }
}
