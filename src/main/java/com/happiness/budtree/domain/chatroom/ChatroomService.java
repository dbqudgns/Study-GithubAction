package com.happiness.budtree.domain.chatroom;

import com.happiness.budtree.domain.chatroom.DTO.request.ChatroomAllRQ;
import com.happiness.budtree.domain.chatroom.DTO.request.ChatroomPartRQ;
import com.happiness.budtree.domain.chatroom.DTO.response.*;
import com.happiness.budtree.domain.member.Member;
import com.happiness.budtree.domain.message.Message;
import com.happiness.budtree.domain.message.MessageRepository;
import com.happiness.budtree.domain.message.SenderType;
import com.happiness.budtree.domain.survey.Survey;
import com.happiness.budtree.domain.survey.SurveyRepository;
import com.happiness.budtree.jwt.Custom.CustomMemberDetails;
import com.happiness.budtree.util.ReturnMember;
import org.springframework.core.io.Resource;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatroomService {

    private final ReturnMember returnMember;
    private final SurveyRepository surveyRepository;
    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;
    private final ChatClient chatClient;

    @Value("classpath:templates/gpt-by-survey.st")
    private Resource template;

    private static final Map<Integer, String> PART = Map.of(
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

    private static final Map<Integer, String> CHOOSE = Map.of(
            1, "전혀 방해받지 않았다",
            2, "며칠 동안 방해받았다",
            3, "7일 이상 방해받았다",
            4, "거의 매일 방해받았다"
    );

    public ChatroomFirstSurveyRP responseFirstChat(Long surveyId, CustomMemberDetails customMemberDetails) throws AccessDeniedException {

        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());

        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new EntityNotFoundException("해당 자가 진단을 찾을 수 없습니다."));

        if (!Objects.equals(survey.getMember().getUsername(), member.getUsername())) {
            throw new AccessDeniedException("로그인 한 사용자는 해당 자가진단을 조회 할 수 없습니다.");
        }

        Chatroom chatroom = Chatroom.builder()
                .member(member)
                .build();

        chatroomRepository.save(chatroom);

        Map<String, List<String>> scoreMap = organizeResponse(survey);

        return ChatroomFirstSurveyRP.builder()
                .name(member.getName())
                .roomId(chatroom.getRoomId())
                .result(scoreMap)
                .build();
    }

    private Map<String, List<String>> organizeResponse(Survey survey) {

        Map<String, List<String>> scoreMap = new HashMap<>();

        for (int i = 1; i<=9; i++) {
            int score = getSurveyScore(survey, i);
            String scoreString = String.valueOf(score);
            scoreMap.computeIfAbsent(scoreString, k -> new ArrayList<>()).add(PART.get(i));
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

    @Transactional
    public ChatroomIdRP createChatroom(CustomMemberDetails customMemberDetails) {

        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());

        Chatroom newChatroom = Chatroom.builder()
                .member(member)
                .build();
        chatroomRepository.save(newChatroom);

        return ChatroomIdRP.builder()
                .name(member.getName())
                .roomId(newChatroom.getRoomId())
                .build();
    }

    @Transactional
    public ChatroomAnswerRP getChatBySurvey(Long roomId, ChatroomPartRQ chatroomPartRQ, CustomMemberDetails customMemberDetails) throws AccessDeniedException {

        //1. 사용자와 채팅방 검증
        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());

        Chatroom chatroom = chatroomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("해당 채팅방을 찾을 수 없습니다."));

        if (!Objects.equals(chatroom.getMember().getUsername(), member.getUsername())) {
            throw new AccessDeniedException("로그인 한 사용자는 해당 채팅방을 이용할 수 없습니다.");
        }

        //2. 항목 & 선택안 번호를 바탕으로 문자열 뽑기
        String partValue = PART.get(chatroomPartRQ.part());
        String chooseValue = CHOOSE.get(chatroomPartRQ.choose());

        //3. 2번에서 뽑은 항목 & 선택안을 바탕으로 Prompt 구성
        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of("part", partValue, "choose", chooseValue));

        //4. 사용자 메시지 저장
        String query = prompt.getContents();
        Message userMessage = Message.builder()
                .chatroom(chatroom)
                .content(query)
                .senderType(SenderType.MEMBER)
                .createdDate(LocalDateTime.now())
                .build();
        messageRepository.save(userMessage);

        //5. GPT 에게 요청
        String response = chatClient.prompt().user(query).call().content();

        //6. GPT 응답 저장
        Message responseMessage = Message.builder()
                .chatroom(chatroom)
                .content(response)
                .senderType(SenderType.BUDDY)
                .createdDate(LocalDateTime.now())
                .build();
        messageRepository.save(responseMessage);

        return ChatroomAnswerRP.builder()
                .answer(response)
                .build();

    }

    @Transactional
    public ChatroomAnswerRP getChatByQuery(Long roomId, String query, CustomMemberDetails customMemberDetails) throws AccessDeniedException {

       //1. 사용자와 채팅방 검증
        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());

        Chatroom chatroom = chatroomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("해당 채팅방을 찾을 수 없습니다."));

        if (!Objects.equals(chatroom.getMember().getUsername(), member.getUsername())) {
            throw new AccessDeniedException("로그인 한 사용자는 해당 채팅방을 이용할 수 없습니다.");
        }

        //2. 사용자 요청문 저장
        Message userMessage = Message.builder()
                .chatroom(chatroom)
                .content(query)
                .senderType(SenderType.MEMBER)
                .createdDate(LocalDateTime.now())
                .build();
        messageRepository.save(userMessage);

        //3. 최근 4개 대화 가져오기
        List<Message> previousMessages = messageRepository.getMessageByRoomID(chatroom);
        int previousSize = previousMessages.size();
        List<Message> recentMessages = previousMessages.subList(Math.max(previousSize - 4, 0), previousSize);

        //4. Prompt 메시지 구성
        List<org.springframework.ai.chat.messages.Message> promptMessages = new ArrayList<>();
        for (Message chatMessage : recentMessages) {
            if (chatMessage.getSenderType() == SenderType.MEMBER) {
                promptMessages.add(new UserMessage(chatMessage.getContent()));
            }
            else {
                promptMessages.add(new AssistantMessage(chatMessage.getContent()));
            }
        }

        //5. 사용자 요청 메시지 추가 ("user" 역할)
        promptMessages.add(new UserMessage(query));
        Prompt prompt = new Prompt(promptMessages);

        //6. GPT 에게 요청
        String response = chatClient.prompt(prompt).call().content();

        //7. GPT 응답 저장
        Message responseMessage = Message.builder()
                .chatroom(chatroom)
                .content(response)
                .senderType(SenderType.BUDDY)
                .createdDate(LocalDateTime.now())
                .build();

        //9. DB 저장
        messageRepository.save(responseMessage);

        return ChatroomAnswerRP.builder()
                .answer(response)
                .build();
    }


    public List<ChatroomAllRP> chatroomAllRP(ChatroomAllRQ chatroomAllRQ, CustomMemberDetails customMemberDetails) {

        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());

        List<Chatroom> allChatroom = chatroomRepository.getChatroomByMemberID(member);

        List<ChatroomAllRP> chatroomAllRPs = new ArrayList<>();

        //전체 조회 시 수행
        if (chatroomAllRQ.year() == 0 && chatroomAllRQ.month() == 0) {

            for (Chatroom chatroom : allChatroom) {

                ChatroomAllRP chatroomAllRP = ChatroomAllRP.builder()
                        .roomId(chatroom.getRoomId())
                        .createdTime(chatroom.getCreatedDate())
                        .build();

                chatroomAllRPs.add(chatroomAllRP);

            }

            return chatroomAllRPs;

        } //월만 조회 시
        else if (chatroomAllRQ.year() == 0) {

            for (Chatroom chatroom : allChatroom) {

                LocalDateTime createdDate = chatroom.getCreatedDate();

                if (createdDate.getMonthValue() == chatroomAllRQ.month()) {
                    ChatroomAllRP chatroomAllRP = ChatroomAllRP.builder()
                            .roomId(chatroom.getRoomId())
                            .createdTime(chatroom.getCreatedDate())
                            .build();

                    chatroomAllRPs.add(chatroomAllRP);
                }
            }

            return chatroomAllRPs;
        } //년만 조회 시
        else if (chatroomAllRQ.month() == 0) {

            for (Chatroom chatroom : allChatroom) {

                LocalDateTime createdDate = chatroom.getCreatedDate();

                if (createdDate.getYear() == chatroomAllRQ.year()) {
                    ChatroomAllRP chatroomAllRP = ChatroomAllRP.builder()
                            .roomId(chatroom.getRoomId())
                            .createdTime(chatroom.getCreatedDate())
                            .build();

                    chatroomAllRPs.add(chatroomAllRP);
                }
            }

            return chatroomAllRPs;
        }
        else { // 특정 년, 월 조회 시

            for (Chatroom chatroom : allChatroom) {

                LocalDateTime createdDate = chatroom.getCreatedDate();

                //요청 받은 년, 월과 비교
                if (createdDate.getYear() == chatroomAllRQ.year() && createdDate.getMonthValue() == chatroomAllRQ.month()) {

                    ChatroomAllRP chatroomAllRP = ChatroomAllRP.builder()
                            .roomId(chatroom.getRoomId())
                            .createdTime(chatroom.getCreatedDate())
                            .build();

                    chatroomAllRPs.add(chatroomAllRP);
                }
            }

            return chatroomAllRPs;
        }
    }

    public List<ChatroomMessageRP> chatroomMessages(Long roomId, CustomMemberDetails customMemberDetails) throws AccessDeniedException {

        Member member = returnMember.findMemberByUsernameOrTrow(customMemberDetails.getUsername());

        Chatroom room = chatroomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("해당 채팅방을 찾을 수 없습니다."));

        if (!Objects.equals(room.getMember().getUsername(), member.getUsername())) {
            throw new AccessDeniedException("로그인 한 사용자는 해당 채팅방을 조회 할 수 없습니다.");
        }

        List<Message> messageList = messageRepository.getMessageByRoomID(room);

        List<ChatroomMessageRP> chatroomMessageRPs = new ArrayList<>();
        for (Message message : messageList) {

            ChatroomMessageRP chatroomMessageRP = ChatroomMessageRP.builder()
                    .sender(message.getSenderType())
                    .content(message.getContent())
                    .createdDate(message.getCreatedDate())
                    .build();

            chatroomMessageRPs.add(chatroomMessageRP);

        }

        return chatroomMessageRPs;

    }
}
