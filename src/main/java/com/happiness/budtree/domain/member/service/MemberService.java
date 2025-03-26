package com.happiness.budtree.domain.member.service;

import com.happiness.budtree.domain.member.DTO.request.MemberRegisterDTO;
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
    public void register(MemberRegisterDTO memberRegisterDTO) {

        Member member = Member.builder()
                .name(memberRegisterDTO.name())
                .username(memberRegisterDTO.username())
                .password(bCryptPasswordEncoder.encode(memberRegisterDTO.password()))
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }

}
