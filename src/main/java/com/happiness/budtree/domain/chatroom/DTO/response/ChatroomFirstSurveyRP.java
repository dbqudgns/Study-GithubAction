package com.happiness.budtree.domain.chatroom.DTO.response;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ChatroomFirstSurveyRP(
        String name,
        Long roomId,
        Map<String, List<String>> result

) {


}
