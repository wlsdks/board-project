package com.study.boardproject.repository;

import com.study.boardproject.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource // spring-data-rest 사용을 위해 작성
public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
}