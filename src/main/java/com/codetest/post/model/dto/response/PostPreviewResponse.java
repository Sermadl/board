package com.codetest.post.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostPreviewResponse {
    private Long id;
    private String title;
    private String writerId;
    private int views;
    private String file;
    private String createdAt;
}
