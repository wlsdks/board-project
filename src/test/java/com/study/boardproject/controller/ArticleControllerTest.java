package com.study.boardproject.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@WebMvcTest
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
                .andExpect(content().contentType(MediaType.TEXT_HTML)) // view니까 text_html
                .andExpect(model().attributeExists("articles")); // model안에 articles라는 이름의 key값을 가지는 데이터가있는지 체크
    }

    @DisplayName("[view]-[GET] 게시글 상세 페이지 - 정상호출 ")
    @Test
    void board_list_test2() throws Exception {
        //given

        //when & then
        mockMvc.perform(get("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML)) // view니까 text_html
                .andExpect(model().attributeExists("article")); // model안에 articles라는 이름의 key값을 가지는 데이터가있는지 체크
    }

    @DisplayName("[view]-[GET] 게시글 검색 전용 페이지 - 정상호출 ")
    @Test
    void board_list_test3() throws Exception {
        //given

        //when & then
        mockMvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML)); // view니까 text_html
    }

    @DisplayName("[view]-[GET] 게시글 해시테그 검색 페이지 - 정상호출 ")
    @Test
    void board_list_test4() throws Exception {
        //given

        //when & then
        mockMvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML)); // view니까 text_html
    }

}