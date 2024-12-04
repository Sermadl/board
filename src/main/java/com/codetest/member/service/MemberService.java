package com.codetest.member.service;

import com.codetest.member.model.dto.request.SignInRequest;
import com.codetest.member.model.dto.request.SignUpRequest;
import com.codetest.member.model.entity.Member;
import com.codetest.member.model.entity.MemberRole;
import com.codetest.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignUpRequest request) {
        if(memberRepository.findByLoginId(request.getLoginId()).isPresent()) {
            throw new RuntimeException("이미 가입된 아이디입니다.");
        }

        if(!request.getPassword().equals(request.getVerifyPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        Member member = Member.builder()
                .name(request.getName())
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRole(MemberRole.USER)
                .build();

        memberRepository.save(member);
    }

    public void signIn(SignInRequest request, HttpServletRequest httpServletRequest) {
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("아이디/비밀번호가 바르지 않습니다.");
        }

        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);  // Session이 없으면 생성

        session.setAttribute("member", member);
        session.setAttribute("role", member.getUserRole().toString());
        session.setMaxInactiveInterval(1800);
    }

    public void verifySignUp(SignUpRequest request, BindingResult result){
        if(verifyLoginId(request.getLoginId())){
            result.addError(new FieldError("request", "loginId", "이미 가입된 아이디입니다."));
        }
        if(verifyPassword(request.getPassword(), request.getVerifyPassword())){
            result.addError(new FieldError("request", "verifyPassword", "비밀번호가 일치하지 않습니다."));
        }
    }

    public boolean verifyLoginId(String loginId){
        return memberRepository.findByLoginId(loginId).isPresent();
    }

    public boolean verifyPassword(String password, String verifyPassword){
        return !password.equals(verifyPassword);
    }

    public void verifySignIn(SignInRequest request, BindingResult result){
        if(memberRepository.findByLoginId(request.getLoginId()).isPresent()){
            if(!passwordEncoder.matches(request.getPassword(), memberRepository.findByLoginId(request.getLoginId()).get().getPassword())) {
                result.addError(new FieldError("request", "password", "비밀번호가 틀렸습니다."));
            }
            return;
        }

        result.addError(new FieldError("request", "loginId", "아이디가 존재하지 않습니다."));
    }

    public void signOut(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if(session != null) {
            session.invalidate();
        } else {
            throw new RuntimeException("Session not found");
        }
    }
}
