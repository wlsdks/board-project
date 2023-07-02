package com.study.boardproject.dto;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.study.boardproject.domain.Article}
 */
public record ArticleDto(
        LocalDateTime createdAt,
        String createdBy,
        String title,
        String content,
        String hashtag
) {

    public static ArticleDto of(LocalDateTime createdAt, String createdBy, String title, String content, String hashtag) {
        return new ArticleDto(createdAt, createdBy, title, content, hashtag);
    }

}