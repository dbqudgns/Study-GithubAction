package com.happiness.budtree.domain.chatroom;

import com.happiness.budtree.domain.chatroom.DTO.response.ChatroomFirstSurveyRP;
import com.happiness.budtree.domain.member.Member;
import com.happiness.budtree.domain.survey.Survey;
import com.happiness.budtree.domain.survey.SurveyRepository;
import com.happiness.budtree.jwt.Custom.CustomMemberDetails;
import com.happiness.budtree.util.ReturnMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatroomService {

    private final ReturnMember returnMember;
    private final SurveyRepository surveyRepository;
    private final ChatroomRepository chatroomRepository;

    private static final Map<Integer, String> QUESTION = Map.of(
            1, "일 또는 여가 활동을 하는데 흥미나 즐거움을 느끼지 못함",
            2, "기분이 가라앉거나, 우울하거나, 희망이 없음",
            3, "잠이 들거나 계속 잠을 자는 것이 어려움 또는 잠을 너무 많이 잠",
            4, "피곤하다고 느끼거나 기운이 거의 없음",
            5, "입맛이 없거나 과식을 함",
            6, "자신을 부정적으로 봄 혹은 자신이 실패자라고 느끼거나 자신 또는 가족을 실망시킴",
            7, "신문을 읽거나 텔레비전 보는 것과 같은 일에 집중하는 것이 어려움",
            8, "움직임이나 말이 평소보다 눈에 띄게 느려지거나 과하게 빨라짐",
            9, "자신이 죽는 것이 더 낫다고 생각하거나 어떤식으로든 자신을 해칠것이라고 생각함"
    );

    public ChatroomFirstSurveyRP responseFirstChat(Long surveyId, CustomMemberDetails customMemberDetails) throws AccessDeniedException {

        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());

        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new EntityNotFoundException("해당 자가 진단을 찾을 수 없습니다."));

        if (!Objects.equals(survey.getMember().getUsername(), member.getUsername())) {
            throw new AccessDeniedException("로그인 한 사용자는 해당 자가진단을 이용할 수 없습니다.");
        }

        Chatroom chatroom = Chatroom.builder()
                .member(member)
                .build();

        chatroomRepository.save(chatroom);

        Map<String, List<String>> scoreMap = organizeResponse(survey);

        return ChatroomFirstSurveyRP.builder()
                .name(member.getName())
                .result(scoreMap)
                .build();
    }

    private Map<String, List<String>> organizeResponse(Survey survey) {

        Map<String, List<String>> scoreMap = new HashMap<>();

        for (int i = 1; i<=9; i++) {
            int score = getSurveyScore(survey, i);
            String scoreString = String.valueOf(score);
            scoreMap.computeIfAbsent(scoreString, k -> new ArrayList<>()).add(QUESTION.get(i));
        }
        return scoreMap;
    }

    private int getSurveyScore(Survey survey, int part) {
        return switch (part) {
            case 1 -> survey.getPart1();
            case 2 -> survey.getPart2();
            case 3 -> survey.getPart3();
            case 4 -> survey.getPart4();
            case 5 -> survey.getPart5();
            case 6 -> survey.getPart6();
            case 7 -> survey.getPart7();
            case 8 -> survey.getPart8();
            case 9 -> survey.getPart9();
            default -> throw new IllegalArgumentException("잘못된 항목 번호입니다.");
        };
    }
}
