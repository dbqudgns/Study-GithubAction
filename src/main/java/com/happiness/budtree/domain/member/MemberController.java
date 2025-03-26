package com.happiness.budtree.domain.member;

import com.happiness.budtree.domain.member.DTO.request.MemberLoginDTO;
import com.happiness.budtree.domain.member.DTO.request.MemberRegisterDTO;
import com.happiness.budtree.domain.member.service.LoginService;
import com.happiness.budtree.domain.member.service.LogoutService;
import com.happiness.budtree.domain.member.service.MemberService;
import com.happiness.budtree.domain.member.service.RefreshService;
import com.happiness.budtree.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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


    @PostMapping("/register")
    @Operation(summary = "회원가입")
    public ResponseEntity<?> register(@RequestBody @Valid MemberRegisterDTO memberRegisterDTO) {

        memberService.register(memberRegisterDTO);

        return ResponseEntity.ok(ApiResponse.success(200, "회원가입 성공"));
    }

    @GetMapping("/reissue")
    @Operation(summary = "Access 토큰 재발급 및 Refresh 토큰 Rotate")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return refreshService.reissue(request, response);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<?> login(@RequestBody @Valid MemberLoginDTO memberLoginDTO,
                                   HttpServletRequest request, HttpServletResponse response) {
        return loginService.login(memberLoginDTO, request, response);
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return logoutService.logout(request, response);
    }

}
