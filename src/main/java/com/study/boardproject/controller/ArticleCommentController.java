package com.study.boardproject.controller;

import com.study.boardproject.dto.request.ArticleCommentRequest;
import com.study.boardproject.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewArticleComment(ArticleCommentRequest articleCommentRequest) {

        

        // 현재 댓글을 작성중인 페이지의 id값을 가지고 있다. 이걸로 redirect를 해준다.
        return "redirect:/articles" + articleCommentRequest.articleId();
    }

    @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(@PathVariable Long commentId, Long articleId) {

        // 댓글을 지우고 나서 현재 페이지를 그대로 유지하려면 이 게시글페이지의 id가 필요해서 articleId를 받는다.
        return "redirect:/articles" + articleId;
    }
}
