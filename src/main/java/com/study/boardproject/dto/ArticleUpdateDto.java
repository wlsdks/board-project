package com.study.boardproject.dto;

/**
 * DTO for {@link com.study.boardproject.domain.Article}
 */
public record ArticleUpdateDto(
        String title,
        String content,
        String hashtag
) {

    public static ArticleUpdateDto of(String title, String content, String hashtag) {
        return new ArticleUpdateDto(title, content, hashtag);
    }

}