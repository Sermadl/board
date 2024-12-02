package com.codetest.post.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PostCreateRequest {
    private String title;
    @NotNull(message = "내용을 입력해주세요")
    private String content;
    private List<MultipartFile> files;
}
