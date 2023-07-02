package com.study.boardproject.repository;

import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.study.boardproject.domain.ArticleComment;
import com.study.boardproject.domain.QArticle;
import com.study.boardproject.domain.QArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource // spring-data-rest 사용을 위해 작성
public interface ArticleCommentRepository extends
        JpaRepository<ArticleComment, Long>,
        QuerydslPredicateExecutor<ArticleComment>, // 이 entity의 모든 필드에 대한 기본 검색기능을 추가해준다.
        QuerydslBinderCustomizer<QArticleComment> // 이걸 추가해야 검색의 세부설정이 가능하다. (like 검색 설정가능)
{

    List<ArticleComment> findByArticle_Id(Long articleId);

    // 앞에 default를 달아줘서 메서드를 구현할수 있다.
    // 검색 기능 - 상세검색 구현
    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root) {
        // entity filed에서 내가 원하는 것들만 검색할수있도록 설정한다.
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.content, root.createdAt, root.createdBy);
        // bindings.bind(root.title).first(StringExpression::likeIgnoreCase); // 쿼리가 like '${v}' 로 생성됨
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase); // 쿼리가 like '%${v}%' 로 생성됨
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}