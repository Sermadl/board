package com.codetest.global.auth;

import com.codetest.member.model.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AppAuthentication {

    public static Long getMemberId(HttpServletRequest request) {
        Member member = (Member) request.getSession().getAttribute("member");
        return member.getId();
    }
}
