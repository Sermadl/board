package com.codetest.post.controller;

import com.codetest.post.model.dto.request.PostCreateRequest;
import com.codetest.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.codetest.global.auth.AppAuthentication.getMemberId;

@Controller
@Slf4j
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public String showSavePost(@ModelAttribute("request") PostCreateRequest request,
                               Model model) {
        model.addAttribute("request", new PostCreateRequest());
        return "/post/create-post";
    }

    @PostMapping
    public String savePost(@ModelAttribute("request") @Validated PostCreateRequest request,
                               BindingResult result,
                               Model model,
                               HttpServletRequest httpServletRequest) {

        postService.verifyPostRequest(request, result);

        if(result.hasErrors()){
            result.getAllErrors().forEach(error ->
                    log.error(error.getDefaultMessage())
            );
            model.addAttribute("request", request);
            return "post/create-post";
        }

        postService.savePost(request, getMemberId(httpServletRequest));

        return "post/create-post";
    }

    @GetMapping("/list")
    public String showPostList(Model model) {
        return "post/list";
    }
}
