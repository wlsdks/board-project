package com.study.boardproject.dto.request;

import com.study.boardproject.dto.ArticleCommentDto;
import com.study.boardproject.dto.UserAccountDto;

/**
 * DTO for {@link com.study.boardproject.domain.ArticleComment}
 */
public record ArticleCommentRequest(
        Long articleId,
        Long parentCommentId, // check
        String content
) {

    public static ArticleCommentRequest of(Long articleId, String content) {
        return ArticleCommentRequest.of(articleId, null, content);
    }

    public static ArticleCommentRequest of(Long articleId, Long parentCommentId, String content) {
        return new ArticleCommentRequest(articleId, parentCommentId, content);
    }

    public ArticleCommentDto toDto(UserAccountDto userAccountDto) {
        return ArticleCommentDto.of(
                articleId,
                userAccountDto,
                parentCommentId,
                content
        );
    }

}