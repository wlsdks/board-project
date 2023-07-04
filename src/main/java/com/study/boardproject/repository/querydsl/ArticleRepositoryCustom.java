package com.study.boardproject.repository.querydsl;

import java.util.List;

public interface ArticleRepositoryCustom {
    // domain인 entity를 반환받는것이 아니다. -> querydsl의 힘이 필요하다.
    List<String> findAllDistinctHashtags();
}
