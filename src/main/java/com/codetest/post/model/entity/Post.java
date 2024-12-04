package com.codetest.post.model.entity;

import com.codetest.global.util.BaseEntity;
import com.codetest.member.model.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private int views;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void increaseViews() {
        this.views++;
    }

    public void edit(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
