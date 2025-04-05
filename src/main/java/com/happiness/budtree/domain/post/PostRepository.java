package com.happiness.budtree.domain.post;

import com.happiness.budtree.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.member = :member ORDER BY p.createDate DESC, p.postId DESC")
    List<Post> findLatestPosts(@Param("member") Member member);




}

