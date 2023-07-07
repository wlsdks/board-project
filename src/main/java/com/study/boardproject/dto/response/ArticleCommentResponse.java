package com.study.boardproject.dto.response;

import com.study.boardproject.dto.ArticleCommentDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public record ArticleCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Long parentCommentId, // check
        Set<ArticleCommentResponse> childComments
) {

    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname, String userId) {
        return ArticleCommentResponse.of(id, content, createdAt, email, nickname, userId, null);
    }

    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname, String userId, Long parentCommentId) {
        // 정렬된 대댓글을 내보내기 위해 TreeSet을 사용한다.
        Comparator<ArticleCommentResponse> childCommentComparator = Comparator
                .comparing(ArticleCommentResponse::createdAt)
                .thenComparing(ArticleCommentResponse::id);

        return new ArticleCommentResponse(id, content, createdAt, email, nickname, userId, parentCommentId, new TreeSet<>(childCommentComparator));
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return ArticleCommentResponse.of(
                dto.id(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.userAccountDto().userId(),
                dto.parentCommentId()
        );
    }

    // 부모인지 자식인지 판단하는 메서드
    public boolean hasParentComment() {
        // 내가 parentCommentId를 가졌는지 아닌지를 검사하면 된다.
        return parentCommentId != null;
    }

}