package com.happiness.budtree.util;

import com.happiness.budtree.domain.member.Member;
import com.happiness.budtree.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReturnMember {

    private final MemberRepository memberRepository;

    public Member findMemberByUsernameOrTrow(String username) {

        Member member = memberRepository.findByUsername(username);

        if (member == null) {
            throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다.");
        }

        return member;
    }

}
