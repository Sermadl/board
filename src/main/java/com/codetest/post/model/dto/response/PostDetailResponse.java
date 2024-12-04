package com.codetest.post.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostDetailResponse {
    private Long id;
    private String title;
    private String writerId;
    private int views;
    private String createdAt;
    private String content;
    private List<PostFileResponse> files;
    private boolean editable;
}
