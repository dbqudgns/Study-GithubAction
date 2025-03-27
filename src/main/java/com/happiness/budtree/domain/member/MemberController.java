package com.happiness.budtree.domain.member;

import com.happiness.budtree.domain.member.DTO.request.MemberChangeNameRQ;
import com.happiness.budtree.domain.member.DTO.request.MemberCheckRQ;
import com.happiness.budtree.domain.member.DTO.request.MemberLoginRQ;
import com.happiness.budtree.domain.member.DTO.request.MemberRegisterRQ;
import com.happiness.budtree.domain.member.service.LoginService;
import com.happiness.budtree.domain.member.service.LogoutService;
import com.happiness.budtree.domain.member.service.MemberService;
import com.happiness.budtree.domain.member.service.RefreshService;
import com.happiness.budtree.jwt.Custom.CustomMemberDetails;
import com.happiness.budtree.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Tag(name = "유저 api", description = "유저 관련 기능")
public class MemberController {

    private final MemberService memberService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final RefreshService refreshService;

    @PostMapping("/check")
    @Operation(summary = "아이디 중복 체크")
    public ResponseEntity<?> checkID(@RequestBody @Valid MemberCheckRQ memberCheckRQ) {

        String username = memberCheckRQ.username();

        return ResponseEntity.ok(memberService.checkID(username));
    }

    @PostMapping("/register")
    @Operation(summary = "회원가입")
    public ResponseEntity<?> register(@RequestBody @Valid MemberRegisterRQ memberRegisterRQ) {

        memberService.register(memberRegisterRQ);

        return ResponseEntity.ok(ApiResponse.success(200, "회원가입 성공"));
    }

    @GetMapping("/reissue")
    @Operation(summary = "Access 토큰 재발급 및 Refresh 토큰 Rotate")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return refreshService.reissue(request, response);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<?> login(@RequestBody @Valid MemberLoginRQ memberLoginRQ,
                                   HttpServletRequest request, HttpServletResponse response) {
        return loginService.login(memberLoginRQ, request, response);
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return logoutService.logout(request, response);
    }

    @PatchMapping("/change-name")
    @Operation(summary = "닉네임 변경")
    public ResponseEntity<?> changeName(@RequestBody @Valid MemberChangeNameRQ memberChangeNameRQ,
                                        @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        String name = memberChangeNameRQ.name();
        memberService.checkName(name, customMemberDetails);
        return ResponseEntity.ok(ApiResponse.success(200, "닉네임 변경 완료"));
    }

}
