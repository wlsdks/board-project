package com.study.boardproject.service;

import com.study.boardproject.domain.type.SearchType;
import com.study.boardproject.dto.ArticleDto;
import com.study.boardproject.dto.ArticleWithCommentsDto;
import com.study.boardproject.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;


    @Transactional(readOnly = true)
    public List<ArticleDto> searchArticles(SearchType searchType, String searchKeyword) {
        return List.of();
    }

    @Transactional(readOnly = true)
    public ArticleDto searchArticle(long articleId) {
        return null;
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesPaging(SearchType searchType, String searchKeyword) {
        return Page.empty();
    }
    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(Long articleId) {
        return null;
    }

    public void saveArticle(ArticleDto dto) {
    }

    public void updateArticle(ArticleDto dto) {
    }

    public void deleteArticle(long articleId) {
    }
}
