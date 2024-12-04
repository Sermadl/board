package com.codetest.global.auth;

import com.codetest.member.model.entity.Member;
import com.codetest.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {
    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();

        if(session == null) {
            response.sendRedirect("/member/sign-in");
            return false;
        } else {
            Member member = (Member) session.getAttribute("member");

            if (member == null) {
                response.sendRedirect("/member/sign-in");
                return false;
            }

            memberRepository.findById(member.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
    }
}
