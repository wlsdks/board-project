package com.study.boardproject.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * data-rest이니 mocking이 힘들어서 그냥 이렇게 직접 db에 접근해서 사용한다.(인테그레이션 테스트 진행)
 */
@DisplayName("Data Rest - api 테스트")
@Transactional //db에 접근하니 transactional을 적어줘야 자동으로 테스트 후 rollback을 한다.
@AutoConfigureMockMvc // Mock을 사용하려면 추가해줘야함
@SpringBootTest
public class DataRestTest {

    private final MockMvc mockMvc;

    public DataRestTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @DisplayName("[api] 게시글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticles_thenReturnsArticlesJsonResponse() throws Exception {
        //given

        //when&then
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk()) // status가 200인지 체크
                //우리는 hal을 사용하다보니 그냥 json이 아니라 이렇게 사용해야 테스트가 진행된다.
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 게시글 단건 조회")
    @Test
    void givenNothing_whenRequestingArticle_thenReturnsArticleJsonResponse() throws Exception {
        //given

        //when&then
        mockMvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk()) // status가 200인지 체크
                //우리는 hal을 사용하다보니 그냥 json이 아니라 이렇게 사용해야 테스트가 진행된다.
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 게시글 -> 댓글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticleCommentsFromArticle_thenReturnsArticleJsonResponse() throws Exception {
        //given

        //when&then
        mockMvc.perform(get("/api/articles/1/articleComments"))
                .andExpect(status().isOk()) // status가 200인지 체크
                //우리는 hal을 사용하다보니 그냥 json이 아니라 이렇게 사용해야 테스트가 진행된다.
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 댓글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticleComments_thenReturnsArticleCommentsJsonResponse() throws Exception {
        //given

        //when&then
        mockMvc.perform(get("/api/articleComments"))
                .andExpect(status().isOk()) // status가 200인지 체크
                //우리는 hal을 사용하다보니 그냥 json이 아니라 이렇게 사용해야 테스트가 진행된다.
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] 댓글 단건 조회")
    @Test
    void givenNothing_whenRequestingArticleComment_thenReturnsArticleCommentJsonResponse() throws Exception {
        //given

        //when&then
        mockMvc.perform(get("/api/articleComments/1"))
                .andExpect(status().isOk()) // status가 200인지 체크
                //우리는 hal을 사용하다보니 그냥 json이 아니라 이렇게 사용해야 테스트가 진행된다.
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }


}
