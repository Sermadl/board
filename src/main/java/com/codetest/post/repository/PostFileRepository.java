package com.codetest.post.repository;

import com.codetest.post.model.entity.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
    Optional<PostFile> findByPostId(Long postId);
}
