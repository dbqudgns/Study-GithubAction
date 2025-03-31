package com.happiness.budtree.domain.survey;

import com.happiness.budtree.domain.member.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "survey")
public class Survey {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(nullable = false)
    private Long surveyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private int part1;

    @Column(nullable = false)
    private int part2;

    @Column(nullable = false)
    private int part3;

    @Column(nullable = false)
    private int part4;

    @Column(nullable = false)
    private int part5;

    @Column(nullable = false)
    private int part6;

    @Column(nullable = false)
    private int part7;

    @Column(nullable = false)
    private int part8;

    @Column(nullable = false)
    private int part9;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Level level;

    @Column(nullable = false)
    private LocalDateTime createdDate;

}
