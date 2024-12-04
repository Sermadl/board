package com.codetest.post.controller;

import com.codetest.global.auth.AppAuthentication;
import com.codetest.post.model.dto.request.PostCreateRequest;
import com.codetest.post.model.dto.request.PostEditRequest;
import com.codetest.post.model.dto.response.PostDetailResponse;
import com.codetest.post.model.dto.response.PostPreviewResponse;
import com.codetest.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    /**
     * 게시글 작성
     * @param request
     * @param result
     * @param model
     * @param httpServletRequest
     * @return redirect:/post/list
     */
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

        model.addAttribute("currentPage", httpServletRequest.getRequestURI());
        postService.savePost(request, getMemberId(httpServletRequest));

        return "redirect:/post/list";
    }

    /**
     * 게시글 수정
     * @param request
     * @param result
     * @param postId
     * @param model
     * @param httpServletRequest
     * @return redirect:/post/{postId}
     */
    @PostMapping("/edit/{postId}")
    public String editPost(@ModelAttribute("request") @Validated PostEditRequest request,
                           BindingResult result,
                           @PathVariable Long postId,
                           Model model,
                           HttpServletRequest httpServletRequest) {
        model.addAttribute("currentPage", "/post/{postId}");

        postService.verifyPostRequest(request, result);

        if(result.hasErrors()){
            result.getAllErrors().forEach(error ->
                    log.error(error.getDefaultMessage())
            );
            model.addAttribute("request", request);

            PostDetailResponse response = postService.showPost(postId, getMemberId(httpServletRequest));
            model.addAttribute("post", response);

            return "post/edit-post";
        }

        postService.editPost(request, postId, getMemberId(httpServletRequest));

        return "redirect:/post/" + postId;
    }

    /**
     * 게시글 삭제
     * @param postId
     * @param httpServletRequest
     * @return redirect:/post/list
     */
    @GetMapping("/delete/{postId}")
    public String deletePost(@PathVariable Long postId,
                             HttpServletRequest httpServletRequest) {

        postService.deletePost(postId, getMemberId(httpServletRequest));

        return "redirect:/post/list";
    }

    @GetMapping("/file/delete/{id}")
    public String deleteFile(@PathVariable Long id,
                             HttpServletRequest request) {

        Long postId = postService.deleteFile(id, getMemberId(request));

        return "redirect:/post/edit/" + postId;
    }

    @GetMapping
    public String showSavePost(HttpServletRequest httpServletRequest,
                               @ModelAttribute("request") PostCreateRequest request,
                               Model model) {
        model.addAttribute("request", new PostCreateRequest());
        model.addAttribute("currentPage", httpServletRequest.getRequestURI());
        return "/post/create-post";
    }

    @GetMapping("/list")
    public String showPostList(HttpServletRequest request,
                               @RequestParam(required = false) String title,
                               @RequestParam(required = false) String writerId,
                               @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                               Model model) {

        if(writerId != null && writerId.isEmpty()) writerId = null;

        Page<PostPreviewResponse> posts = postService.showPostList(pageable, title, writerId);

        model.addAttribute("posts", posts);
        model.addAttribute("title", title);
        model.addAttribute("writerId", writerId);
        model.addAttribute("currentPage", request.getRequestURI());

        return "post/list";
    }

    @GetMapping("/{postId}")
    public String showPost(@PathVariable Long postId, Model model, HttpServletRequest request) {
        model.addAttribute("currentPage", "/post/{postId}");

        PostDetailResponse response = postService.showPost(postId, getMemberId(request));

        model.addAttribute("post", response);

        return "post/detail";
    }

    @GetMapping("/file-download/{id}")
    public String downloadFile(@PathVariable Long id,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        Long postId = postService.downloadFile(id, request, response);

        return "redirect:/post/" + postId;
    }

    @GetMapping("/edit/{postId}")
    public String showEditPost(@PathVariable Long postId,
                               Model model,
                               HttpServletRequest request) {
        model.addAttribute("request", new PostEditRequest());
        model.addAttribute("currentPage", "/post/{postId}");

        PostDetailResponse response = postService.showPost(postId, getMemberId(request));

        model.addAttribute("post", response);

        return "post/edit-post";
    }

}
