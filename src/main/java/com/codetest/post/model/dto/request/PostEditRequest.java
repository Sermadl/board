package com.codetest.post.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PostEditRequest {
    private String title;
    private String content;
    private List<MultipartFile> files;
}
