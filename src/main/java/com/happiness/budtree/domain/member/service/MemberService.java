package com.happiness.budtree.domain.member.service;

import com.happiness.budtree.domain.member.DTO.request.MemberRegisterRQ;
import com.happiness.budtree.domain.member.DTO.response.MemberCheckRP;
import com.happiness.budtree.domain.member.Member;
import com.happiness.budtree.domain.member.MemberRepository;
import com.happiness.budtree.domain.member.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public MemberCheckRP checkID(String username) {

        if (memberRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        return MemberCheckRP.builder()
                .username(username)
                .success(1)
                .message("사용 가능한 닉네임 입니다.")
                .build();
    }

    @Transactional
    public void register(MemberRegisterRQ memberRegisterRQ) {

        Member member = Member.builder()
                .name(memberRegisterRQ.name())
                .username(memberRegisterRQ.username())
                .password(bCryptPasswordEncoder.encode(memberRegisterRQ.password()))
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }

}
