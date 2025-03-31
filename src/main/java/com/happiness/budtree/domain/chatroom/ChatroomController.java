package com.happiness.budtree.domain.chatroom;

import com.happiness.budtree.jwt.Custom.CustomMemberDetails;
import com.happiness.budtree.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
@Tag(name = "채팅방 api", description = "채팅방/GPT 관련 기능")
public class ChatroomController {

    private final ChatroomService chatroomService;

    @GetMapping("/first-chat-survey/{surveyId}")
    @Operation(summary = "자가진단 항목별 점수 정리 후 반환")
    public ResponseEntity<?> responseFirstChat(@PathVariable("surveyId") Long surveyId,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails) throws AccessDeniedException {
        return ResponseEntity.ok(ApiResponse.success(chatroomService.responseFirstChat(surveyId, customMemberDetails)));
    }


}
