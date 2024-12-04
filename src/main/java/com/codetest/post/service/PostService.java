package com.codetest.post.service;

import com.codetest.member.model.entity.Member;
import com.codetest.member.repository.MemberRepository;
import com.codetest.post.model.dto.request.PostCreateRequest;
import com.codetest.post.model.dto.request.PostEditRequest;
import com.codetest.post.model.dto.response.PostDetailResponse;
import com.codetest.post.model.dto.response.PostFileResponse;
import com.codetest.post.model.dto.response.PostPreviewResponse;
import com.codetest.post.model.entity.Post;
import com.codetest.post.model.entity.PostFile;
import com.codetest.post.repository.PostFileRepository;
import com.codetest.post.repository.PostRepository;
import com.codetest.post.repository.PostRepositoryCustom;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostFileRepository postFileRepository;
    private final PostRepositoryCustom postRepositoryCustom;
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
                saveFiles(request.getFiles(), post);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 오류");
        }

        postRepository.save(post);
    }

    @Transactional
    public void editPost(PostEditRequest request, Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if(!memberId.equals(post.getMember().getId())) throw new RuntimeException("권한이 없습니다.");

        post.edit(request.getTitle(), request.getContent());

        try {
            if (request.getFiles() != null) {
                editFiles(request.getFiles(), post);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 오류");
        }
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        if(!post.getMember().getId().equals(memberId)) throw new RuntimeException("권한이 없습니다.");

        postRepository.deleteById(postId);
        postFileRepository.deleteByPostId(postId);
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

    private void editFiles(List<MultipartFile> files, Post post) throws IOException {
        List<PostFile> postFiles = postFileRepository.findByPostId(post.getId());
        postFileRepository.deleteAll(postFiles);

        saveFiles(files, post);
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

    public void verifyPostRequest(PostEditRequest request, BindingResult result) {
        if(request.getTitle().isEmpty()){
            result.addError(new FieldError("request", "title", "제목을 입력해주세요"));
        }
        if(request.getContent().isEmpty()){
            result.addError(new FieldError("request", "content", "내용을 입력해주세요"));
        }
    }

    public Page<PostPreviewResponse> showPostList(Pageable pageable, String title, String writerId) {
        return getPostPreview(postRepositoryCustom.findByWhere(pageable, title, writerId));
    }

    @Transactional
    public PostDetailResponse showPost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        post.increaseViews();

        List<PostFileResponse> fileList = new ArrayList<>();

        List<PostFile> postFileList = postFileRepository.findByPostId(postId);

        if(!postFileList.isEmpty()){
            postFileList.forEach(postFile -> {
                fileList.add(
                        new PostFileResponse(
                                postFile.getId(),
                                postFile.getOriginalFileName()
                        )
                );
            });
        }

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getMember().getLoginId(),
                post.getViews(),
                post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                post.getContent(),
                fileList,
                memberId.equals(post.getMember().getId())
        );
    }

    public Page<PostPreviewResponse> getPostPreview(Page<Post> request) {
        return request.map(currPost -> new PostPreviewResponse(
                currPost.getId(),
                currPost.getTitle(),
                currPost.getMember().getLoginId(),
                currPost.getViews(),
                !postFileRepository.findByPostId(currPost.getId()).isEmpty() ? "있음" : "없음",
                currPost.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        ));
    }

    public Long downloadFile(Long id,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        PostFile postFile = postFileRepository.findById(id)
                .orElseThrow(RuntimeException::new);

        try {
            String path = FILE_PATH + "/" + postFile.getStoredFileName();
            log.info(path);

            String fileName = new String(postFile.getOriginalFileName().getBytes(StandardCharsets.UTF_8), "ISO8859-1");

            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            OutputStream outputStream = response.getOutputStream();

            int length;
            byte[] b = new byte[(int) file.length()];
            while ((length = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, length);
            }

            outputStream.flush();

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return postFile.getPost().getId();
    }

    @Transactional
    public Long deleteFile(Long id, Long memberId) {
        PostFile file = postFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        Long postId = file.getPost().getId();
        if(!memberId.equals(file.getPost().getMember().getId())) throw new RuntimeException("권한이 없습니다.");

        postFileRepository.delete(file);

        return postId;
    }
}
