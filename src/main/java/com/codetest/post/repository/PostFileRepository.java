package com.codetest.post.repository;

import com.codetest.post.model.entity.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
    List<PostFile> findByPostId(Long postId);
    void deleteByPostId(Long postId);
}
