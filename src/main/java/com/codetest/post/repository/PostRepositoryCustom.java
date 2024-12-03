package com.codetest.post.repository;

import com.codetest.post.model.entity.Post;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.codetest.post.model.entity.QPost.post;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Page<Post> findByWhere(Pageable pageable, String title, String writerId) {
        List<Post> content =
                queryFactory
                        .select(post)
                        .from(post)
                        .where(
                                titleHas(title),
                                writerEq(writerId)
                        )
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(getSort(pageable, post))
                        .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(
                        titleHas(title),
                        writerEq(writerId)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression titleHas(String title) {
        return title != null ? post.title.contains(title) : null;
    }

    private BooleanExpression writerEq(String writerId) {
        return writerId != null ? post.member.loginId.eq(writerId) : null;
    }

    public static <T> OrderSpecifier<?>[] getSort(Pageable pageable, EntityPathBase<T> qClass) {
        return pageable.getSort().stream().map(order ->
                        new OrderSpecifier(
                                Order.valueOf(order.getDirection().name()),
                                Expressions.path(Object.class, qClass, order.getProperty())
                        )).toList()
                .toArray(new OrderSpecifier[0]);
    }
}
