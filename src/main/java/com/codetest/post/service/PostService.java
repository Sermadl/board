package com.codetest.post.service;

import com.codetest.member.model.entity.Member;
import com.codetest.member.repository.MemberRepository;
import com.codetest.post.model.dto.request.PostCreateRequest;
import com.codetest.post.model.entity.Post;
import com.codetest.post.model.entity.PostFile;
import com.codetest.post.repository.PostFileRepository;
import com.codetest.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostFileRepository postFileRepository;
    private final Tika tika = new Tika();

    @Value("${filePath}")
    private String FILE_PATH;

    @Transactional
    public void savePost(PostCreateRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .views(0)
                .build();

        try {
            if (request.getFiles() != null) {
                log.info(request.getFiles().get(0).getName());
                saveFiles(request.getFiles(), post);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 오류");
        }

        postRepository.save(post);
    }

    private void saveFiles(List<MultipartFile> files, Post post) throws IOException {
        Path uploadFolder = Paths.get(FILE_PATH);
        if (!uploadFolder.toFile().exists()) {
            uploadFolder.toFile().mkdirs();
        }

        for (MultipartFile file : files) {
            String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + "_" + UUID.randomUUID();
            String mimeType = tika.detect(file.getInputStream());

            if (!checkFileType(mimeType)){
                throw new RuntimeException("허용되지 않는 확장자입니다.");
            }

            PostFile postFile = new PostFile(
                    file.getOriginalFilename(),
                    fileName,
                    mimeType,
                    post
            );

            postFileRepository.save(postFile);

            File saveFile = new File(FILE_PATH, fileName);
            file.transferTo(saveFile);
        }
    }

    private boolean checkFileType(String type) {
        log.info(type);

        return type.equals("image/png") ||
                type.equals("image/jpeg") ||
                type.equals("image/gif") ||
                type.equals("video/mp4") ||
                type.equals("video/x-msvideo") ||
                type.equals("audio/mpeg") ||
                type.equals("audio/wav") ||
                type.equals("application/pdf") ||
                type.equals("application/vnd.ms-powerpoint") ||
                type.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") ||
                type.equals("application/msword") ||
                type.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                type.equals("application/vnd.ms-excel") ||
                type.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                type.equals("text/plain") ||
                type.equals("text/html");
    }

    public void verifyPostRequest(PostCreateRequest request, BindingResult result) {
        if(request.getTitle().isEmpty()){
            result.addError(new FieldError("request", "title", "제목을 입력해주세요"));
        }
        if(request.getContent().isEmpty()){
            result.addError(new FieldError("request", "content", "내용을 입력해주세요"));
        }
    }
}
