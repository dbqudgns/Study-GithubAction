package com.happiness.budtree.domain.member.DTO.response;

import lombok.Builder;

@Builder
public record MemberCheckRP(String username,
                            Integer success,
                            String message) {
}
