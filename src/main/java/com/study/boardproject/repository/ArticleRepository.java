package com.study.boardproject.repository;

import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.study.boardproject.domain.Article;
import com.study.boardproject.domain.QArticle;
import com.study.boardproject.repository.querydsl.ArticleRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource // spring-data-rest 사용을 위해 작성
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        ArticleRepositoryCustom, // 커스텀 리포지토리를 상속받는다.
        QuerydslPredicateExecutor<Article>, // 이 entity의 모든 필드에 대한 기본 검색기능을 추가해준다. (like검색 불가능)
        QuerydslBinderCustomizer<QArticle> // 이걸 추가해야 검색의 세부설정이 가능하다. (like 검색 설정가능)
{

    // 부분검색 가능하게 Containing을 붙여준다. -> query에 {% %} 추가됨
    Page<Article> findByTitleContaining(String title, Pageable pageable);
    Page<Article> findByContentContaining(String content, Pageable pageable);
    Page<Article> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Article> findByUserAccount_NicknameContaining(String nickname, Pageable pageable);
    Page<Article> findByHashtag(String hashtag, Pageable pageable);

    // 삭제 로직
    void deleteByIdAndUserAccount_UserId(Long articleId, String userid);

    // 앞에 default를 달아줘서 메서드를 구현할수 있다.
    // 검색 기능 - 상세검색 구현
    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        // entity filed에서 내가 원하는 것들만 검색할수있도록 설정한다.
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.content, root.hashtags, root.createdAt, root.createdBy);
        // bindings.bind(root.title).first(StringExpression::likeIgnoreCase); // 쿼리가 like '${v}' 로 생성됨
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase); // 쿼리가 like '${v}' 로 생성됨
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase); // 쿼리가 like '%${v}%' 로 생성됨
        bindings.bind(root.hashtags.any().hashtagName).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}