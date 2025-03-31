package com.happiness.budtree.domain.chatroom.DTO.response;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ChatroomFirstSurveyRP(

        String name,
        Map<String, List<String>> result

) {


}
