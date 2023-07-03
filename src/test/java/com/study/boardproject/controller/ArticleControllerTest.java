package com.study.boardproject.controller;

import com.study.boardproject.config.SecurityConfig;
import com.study.boardproject.dto.ArticleWithCommentsDto;
import com.study.boardproject.dto.UserAccountDto;
import com.study.boardproject.service.ArticleService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@Import(SecurityConfig.class) // springSecurity로 막아놓은 설정을 import해줘야함
@WebMvcTest(ArticleController.class) //이렇게 테스트할 컨트롤러의 클래스를 넣어주면 최적화된다.
class ArticleControllerTest {

    private final MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    // 테스트 코드에서는 @Autowired 생략불가
    public ArticleControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @DisplayName("[view]-[GET] 게시글 리스트 (게시판) 페이지 - 정상호출")
    @Test
    void board_list_test() throws Exception {
        //given
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());

        //when & then
        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // view니까 text_html characterset옵션이 붙어도 오류안나게 compatible로 테스트함
                .andExpect(view().name("articles/index")) // view의 이름검사 실시
                .andExpect(model().attributeExists("articles")); // model안에 articles라는 이름의 key값을 가지는 데이터가있는지 체크
//                .andExpect(model().attributeExists("searchTypes"));

        // 어떤 mock이 호출되었는지 검증한다.
        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
    }

    @DisplayName("[view]-[GET] 게시글 상세 페이지 - 정상호출 ")
    @Test
    void board_list_test2() throws Exception {
        //given
        Long articleId = 1L;
        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());

        //when & then
        mockMvc.perform(get("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // view니까 text_html
                .andExpect(view().name("articles/detail")) // view의 이름검사 실시
                .andExpect(model().attributeExists("article")) // model안에 articles라는 이름의 key값을 가지는 데이터가있는지 체크
                .andExpect(model().attributeExists("articleComments")); // 게시글 댓글정보가 model에 담겨있는지 체크

        // then여기서 이 mock을 호출해야만 한다는 것을 검증한다.
        then(articleService).should().getArticle(articleId);
    }

    @Disabled("구현 중")
    @DisplayName("[view]-[GET] 게시글 검색 전용 페이지 - 정상호출 ")
    @Test
    void board_list_test3() throws Exception {
        //given

        //when & then
        mockMvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // view니까 text_html
                .andExpect(model().attributeExists("articles/search"));
    }

    @Disabled("구현 중")
    @DisplayName("[view]-[GET] 게시글 해시테그 검색 페이지 - 정상호출 ")
    @Test
    void board_list_test4() throws Exception {
        //given

        //when & then
        mockMvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // view니까 text_html
                .andExpect(model().attributeExists("articles/search-hashtag"));
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "wlsdks",
                LocalDateTime.now(),
                "wlsdks"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(1L,
                "wlsdks",
                "pw",
                "wlsdks@naver.com",
                "wlsdks",
                "memo",
                LocalDateTime.now(),
                "wlsdks",
                LocalDateTime.now(),
                "wlsdks"
        );
    }

}