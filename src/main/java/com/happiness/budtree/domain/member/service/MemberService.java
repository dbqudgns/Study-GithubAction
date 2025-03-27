package com.happiness.budtree.domain.member.service;

import com.happiness.budtree.domain.member.DTO.request.MemberRegisterRQ;
import com.happiness.budtree.domain.member.DTO.response.MemberCheckRP;
import com.happiness.budtree.domain.member.Member;
import com.happiness.budtree.domain.member.MemberRepository;
import com.happiness.budtree.domain.member.Role;
import com.happiness.budtree.jwt.Custom.CustomMemberDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
            throw new IllegalArgumentException("이미 사용중인 아이디 입니다.");
        }

        return MemberCheckRP.builder()
                .username(username)
                .success(1)
                .message("사용 가능한 아이디 입니다.")
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

    @Transactional
    public void checkName(String name, CustomMemberDetails customMemberDetails) {

        Member member = findMemberByUsernameOrTrow(customMemberDetails.getUsername());

        member.updateName(name);

    }

    public Member findMemberByUsernameOrTrow(String username) {

        Member member = memberRepository.findByUsername(username);

        if (member == null) {
            throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다.");
        }

        return member;
    }

}
