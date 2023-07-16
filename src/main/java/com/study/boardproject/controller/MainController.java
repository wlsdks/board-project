package com.study.boardproject.controller;

import com.study.boardproject.dto.response.ArticleCommentResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

/**
 * <p>
 * 메인 컨트롤러 테스트 중입니다.
 * <p>
 */
@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
        return "forward:/articles";
    }

    /**
     * 댓글 정보를 열람한다.
     *
     * @param id 댓글 ID
     * @return 댓글 응답
     */
    @ResponseBody // view 엔드포인트가 아니라 data를 내보내도록 한다.
    @GetMapping("/test-rest")
    public ArticleCommentResponse test(Long id) {
        return ArticleCommentResponse.of(
                id,
                "content",
                LocalDateTime.now(),
                "e@email.com",
                "wlsdks",
                "wlsdks"
        );
    }

}
