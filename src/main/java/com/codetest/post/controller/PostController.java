package com.codetest.post.controller;

import com.codetest.post.model.dto.request.PostCreateRequest;
import com.codetest.post.model.dto.response.PostPreviewResponse;
import com.codetest.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

        return "post/list";
    }

    @GetMapping("/list")
    public String showPostList(@RequestParam(required = false) String title,
                               @RequestParam(required = false) String writerId,
                               Pageable pageable,
                               Model model) {

        if(writerId != null && writerId.isEmpty()) writerId = null;

        Page<PostPreviewResponse> posts = postService.showPost(pageable, title, writerId);

        model.addAttribute("posts", posts);
        model.addAttribute("title", title);
        model.addAttribute("writerId", writerId);

        return "post/list";
    }
}
