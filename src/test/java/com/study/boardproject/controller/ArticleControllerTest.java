package com.study.boardproject.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@WebMvcTest(ArticleController.class) //이렇게 테스트할 컨트롤러의 클래스를 넣어주면 최적화된다.
class ArticleControllerTest {

    private final MockMvc mockMvc;

    // 테스트 코드에서는 @Autowired 생략불가
    public ArticleControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @DisplayName("[view]-[GET] 게시글 리스트 (게시판) 페이지 - 정상호출")
    @Test
    void board_list_test() throws Exception {
        //given

        //when & then
        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // view니까 text_html characterset옵션이 붙어도 오류안나게 compatible로 테스트함
                .andExpect(view().name("articles/index")) // view의 이름검사 실시
                .andExpect(model().attributeExists("articles")); // model안에 articles라는 이름의 key값을 가지는 데이터가있는지 체크
    }

    @Disabled("구현 중")
    @DisplayName("[view]-[GET] 게시글 상세 페이지 - 정상호출 ")
    @Test
    void board_list_test2() throws Exception {
        //given

        //when & then
        mockMvc.perform(get("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // view니까 text_html
                .andExpect(view().name("articles/detail")) // view의 이름검사 실시
                .andExpect(model().attributeExists("article")) // model안에 articles라는 이름의 key값을 가지는 데이터가있는지 체크
                .andExpect(model().attributeExists("articleComments")); // 게시글 댓글정보가 model에 담겨있는지 체크
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

}