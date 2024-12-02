package com.codetest.post.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;

@Getter
@Setter
public class FileRequest {
    private String fileId;
    private String originalName;
    private MediaType mediaType;
}
