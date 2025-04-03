package com.happiness.budtree.domain.chatroom;

import com.happiness.budtree.domain.chatroom.DTO.request.ChatroomAllRQ;
// import com.happiness.budtree.domain.chatroom.DTO.request.ChatroomQueryRQ;
import com.happiness.budtree.jwt.Custom.CustomMemberDetails;
import com.happiness.budtree.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.nio.file.AccessDeniedException;


@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
@Tag(name = "채팅방 api", description = "채팅방/GPT 관련 기능")
public class ChatroomController {

    private final ChatroomService chatroomService;

    @GetMapping("/chat/survey/first/{surveyId}")
    @Operation(summary = "자가진단 항목별 점수 정리 후 반환(채팅방 생성됨)")
    public ResponseEntity<?> responseFirstChat(@PathVariable("surveyId") Long surveyId,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails) throws AccessDeniedException {
        return ResponseEntity.ok(ApiResponse.success(chatroomService.responseFirstChat(surveyId, customMemberDetails)));
    }

    @PostMapping("/create")
    @Operation(summary = "첫 대화 시 채팅방 생성(자가진단 때는 필요 없음)")
    public ResponseEntity<?> createChatroom(@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        return ResponseEntity.ok(ApiResponse.success(chatroomService.createChatroom(customMemberDetails)));
    }

//    @PostMapping("/chat/{roomId}")
//    @Operation(summary = "챗봇 요청")
//    public Flux<String> getChatByQuery(@RequestBody @Valid ChatroomQueryRQ chatroomQueryRQ, @PathVariable("roomId") Long roomId,
//                                            @AuthenticationPrincipal CustomMemberDetails customMemberDetails) throws AccessDeniedException {
//        return chatroomService.getChatByQuery(roomId, chatroomQueryRQ.query(), customMemberDetails);
//        //ResponseEntity.ok(ApiResponse.success(chatroomService.getChatByQuery(roomId, chatroomQueryRQ.query(), customMemberDetails)));
//
//    }

    @PostMapping("/all")
    @Operation(summary = "대화 내역 전체 조회")
    public ResponseEntity<?> getAllChatroom(@RequestBody @Valid ChatroomAllRQ chatroomAllRQ,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        return ResponseEntity.ok(ApiResponse.success(chatroomService.chatroomAllRP(chatroomAllRQ, customMemberDetails)));
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "특정 대화 내역 조회")
    public ResponseEntity<?> getChatroomMessages(@PathVariable("roomId") Long roomId,
                                                 @AuthenticationPrincipal CustomMemberDetails customMemberDetails) throws AccessDeniedException {
        return ResponseEntity.ok(ApiResponse.success(chatroomService.chatroomMessages(roomId, customMemberDetails)));
    }

}
