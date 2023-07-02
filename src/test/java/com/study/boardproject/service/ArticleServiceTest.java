package com.study.boardproject.service;

import com.study.boardproject.domain.Article;
import com.study.boardproject.domain.type.SearchType;
import com.study.boardproject.dto.ArticleDto;
import com.study.boardproject.dto.ArticleUpdateDto;
import com.study.boardproject.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;


@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks // mock을 주입받는 클래스는 InjectMocks를 적어준다.
    private ArticleService articleService;

    @Mock // mock으로 사용되는애는 Mock을 적어준다.
    private ArticleRepository articleRepository; //mocking에 사용됨

    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환한다.")
    @Test
    void articleSearchListTest() {
        //given

        //when
        List<ArticleDto> articles = articleService.searchArticles(SearchType.TITLE, "search keyword"); // 제목, 본문, ID, 닉네임, 해시태그

        //then
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void articleSearchTest() {
        //given

        //when
        ArticleDto article = articleService.searchArticle(1L); // 제목, 본문, ID, 닉네임, 해시태그

        //then
        assertThat(article).isNotNull();
    }

    @DisplayName("게시글 조회시 - 페이징 테스트")
    @Test
    void pagingTest() {
        //given

        //when
        Page<ArticleDto> articles = articleService.searchArticlesPaging(SearchType.TITLE, "search keyword"); // 제목, 본문, ID, 닉네임, 해시태그

        //then
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void articleContent_writeSave() {
        // given
        // BDDMockito 사용 - static import , any()는 ArgumentMatchers 사용
        // article class를 넘겨주면 아무거나 article class를 반환한다.
        given(articleRepository.save(any(Article.class))).willReturn(null);

        //when
        articleService.updateArticle(1L, ArticleUpdateDto.of("title", "content", "hashtag"));

        //then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글의 ID와 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void articleContent_writeModify() {
        // given
        // BDDMockito 사용 - static import , any()는 ArgumentMatchers 사용
        // article class를 넘겨주면 아무거나 article class를 반환한다.
        given(articleRepository.save(any(Article.class))).willReturn(null);

        //when
        articleService.saveArticle(ArticleDto.of(LocalDateTime.now(), "jinan", "title", "content", "hashtag"));

        //then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다.")
    @Test
    void articleContent_delete() {
        // given
        willDoNothing().given(articleRepository).delete(any(Article.class));

        //when
        articleService.deleteArticle(1L);

        //then
        then(articleRepository).should().save(any(Article.class));
    }

}