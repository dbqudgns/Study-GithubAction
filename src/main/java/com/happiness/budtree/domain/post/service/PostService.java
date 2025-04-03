package com.happiness.budtree.domain.post.service;

import com.happiness.budtree.domain.member.Member;
import com.happiness.budtree.domain.post.DTO.request.PostAllRQ;
import com.happiness.budtree.domain.post.DTO.request.PostChangeRQ;
import com.happiness.budtree.domain.post.DTO.request.PostRegisterRQ;
import com.happiness.budtree.domain.post.DTO.response.PostAllRP;
import com.happiness.budtree.domain.post.DTO.response.PostEmotionRP;
import com.happiness.budtree.domain.post.DTO.response.PostMessageRP;
import com.happiness.budtree.domain.post.Post;
import com.happiness.budtree.domain.post.PostRepository;
import com.happiness.budtree.jwt.Custom.CustomMemberDetails;
import com.happiness.budtree.util.ReturnMember;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ReturnMember returnMember;

    @Transactional
    public void createPost(PostRegisterRQ postRegister, CustomMemberDetails customMemberDetails) {

        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());

        Post post = Post.builder()
                .content(postRegister.content())
                .emotion(postRegister.emotion())
                .member(member)
                .build();
        postRepository.save(post);

    }

    @Transactional
    public void updatePost(Long postId, PostChangeRQ changeRQ,CustomMemberDetails customMemberDetails) {

        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());

        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("일기장이 존재하지 않습니다."));

        if(!post.getMember().getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException("해당 일기장에 대한 수정권한이 없습니다.");
        }

        post.updatePost(changeRQ.content(),changeRQ.emotion());
    }


    @Transactional
    public PostMessageRP findByPostId(Long postId, CustomMemberDetails customMemberDetails) {
        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());


        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("일기장을 찾을 수 없습니다."));


        if (!post.getMember().getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException("일기장을 조회 할 권한이 없습니다.");
        }
        return PostMessageRP.builder()
                .postId(postId)
                .content(post.getContent())
                .emotion(post.getEmotion())
                .createdDate(post.getCreateDate())
                .build();
    }

    @Transactional
    public List<PostEmotionRP> findLatestSixEmotions(CustomMemberDetails customMemberDetails) {
        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());
        List<Post> posts = postRepository.findLatestPosts(member)
                .stream()
                .limit(6)
                .collect(Collectors.toList());

        List<PostEmotionRP> res = new ArrayList<>();

        for (Post post : posts) {
          res.add(PostEmotionRP.builder()
                  .postId(post.getPostId())
                  .emotion(post.getEmotion())
                  .build());
        }
        return res;
    }

    @Transactional
    public List<PostAllRP> findAllPosts(PostAllRQ postAllRQ,  CustomMemberDetails customMemberDetails) {
        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());
        List<Post> posts = postRepository.findLatestPosts(member);

        List<Post> filterPost;
        if(postAllRQ.year() == 0 && postAllRQ.month()==0){
            filterPost = posts;

         //월별 조회
        }else if(postAllRQ.year() == 0){
            filterPost = posts.stream()
                    .filter(post->post.getCreateDate().getMonthValue() == postAllRQ.month())
                    .toList();
         //년별 조회
        }else if(postAllRQ.month() == 0){
            filterPost = posts.stream()
                    .filter(post->post.getCreateDate().getYear() == postAllRQ.year())
                    .toList();

            // 년 월로 조회
        }else{
            filterPost = posts.stream()
                    .filter(post -> post.getCreateDate().getYear() == postAllRQ.year() &&
                            post.getCreateDate().getMonthValue() == postAllRQ.month())
                    .toList();
        }
        if(filterPost.isEmpty()){
            throw new IllegalArgumentException("해당 날짜에 조회되는 일기장이 존재하지 않습니다.");
        }
            return filterPost.stream()
                    .map(this::convertToPostAllRP)
                    .toList();
        }

        private PostAllRP convertToPostAllRP(Post post) {
            return PostAllRP.builder()
                    .postId(post.getPostId())
                    .createdDate(post.getCreateDate())
                    .emotion(post.getEmotion())
                    .build();
        }


    @Transactional
    public void deletePost(Long postId,CustomMemberDetails customMemberDetails){
        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("일기장이 존재하지 않습니다."));

        if(!post.getMember().getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException("일기장 삭제 권한이 없습니다.");
        }
        postRepository.delete(post);
    }
}