package com.study.boardproject.dto.request;

import com.study.boardproject.dto.ArticleCommentDto;
import com.study.boardproject.dto.UserAccountDto;

/**
 * DTO for {@link com.study.boardproject.domain.ArticleComment}
 */
public record ArticleCommentRequest(Long articleId, String content) {

    public static ArticleCommentRequest of(Long articleId, String content) {
        return new ArticleCommentRequest(articleId, content);
    }

    public ArticleCommentDto toDto(UserAccountDto userAccountDto) {
        return ArticleCommentDto.of(
                articleId,
                userAccountDto,
                content
        );
    }

}