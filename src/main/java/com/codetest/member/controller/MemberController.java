package com.codetest.member.controller;

import com.codetest.member.model.dto.request.SignInRequest;
import com.codetest.member.model.dto.request.SignUpRequest;
import com.codetest.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/sign-up")
    public String showSignUp(@ModelAttribute("request") SignUpRequest request, Model model) {
        model.addAttribute("request", new SignUpRequest());
        return "member/sign-up-form";
    }

    @PostMapping("/sign-up")
    public String signUp(@ModelAttribute("request") @Validated SignUpRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        memberService.verifySignUp(request, result);

        if(result.hasErrors()){
            result.getAllErrors().forEach(error ->
                    log.error(error.getDefaultMessage())
            );
            model.addAttribute("request", request);
            return "member/sign-up-form";
        }

        memberService.signUp(request);
        redirectAttributes.addFlashAttribute("msg","가입되었습니다!");
        return "redirect:/";
    }

    @GetMapping("/sign-in")
    public String showSignIn(@ModelAttribute("request")SignInRequest request, Model model) {
        model.addAttribute("request", new SignInRequest());
        return "member/sign-in-form";
    }

    @GetMapping("/sign-out")
    public String signOut(HttpServletRequest request) {
        memberService.signOut(request);
        return "redirect:/";
    }

    @PostMapping("/sign-in")
    public String signIn(@ModelAttribute("request") @Validated SignInRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest httpServletRequest) {

        memberService.verifySignIn(request, result);

        if(result.hasErrors()){
            result.getAllErrors().forEach(error ->
                    log.error(error.getDefaultMessage())
            );
            model.addAttribute("request", request);
            return "member/sign-in-form";
        }

        memberService.signIn(request, httpServletRequest);
        redirectAttributes.addFlashAttribute("msg","로그인되었습니다!");
        return "redirect:/";
    }
}
