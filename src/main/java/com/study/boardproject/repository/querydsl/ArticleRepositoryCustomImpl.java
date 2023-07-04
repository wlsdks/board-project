package com.study.boardproject.repository.querydsl;

import com.study.boardproject.domain.Article;
import com.study.boardproject.domain.QArticle;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {

    public ArticleRepositoryCustomImpl() {
        super(Article.class); // 이렇게 바꿔도 된다.
    }

    @Override
    public List<String> findAllDistinctHashtags() {
        QArticle article = QArticle.article;

        // QuerydslRepositorySupport를 쓸때는 from부터 시작한다.
        return from(article)
                .distinct() // 중복제거
                .select(article.hashtag)
                .where(article.hashtag.isNotNull())
                .fetch();

    }

}
