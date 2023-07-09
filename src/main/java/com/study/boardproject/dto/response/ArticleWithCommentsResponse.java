package com.study.boardproject.dto.response;

import com.study.boardproject.dto.ArticleCommentDto;
import com.study.boardproject.dto.ArticleWithCommentsDto;
import com.study.boardproject.dto.HashtagDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        Set<String> hashtags,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Set<ArticleCommentResponse> articleCommentsResponse
) {

    public static ArticleWithCommentsResponse of(Long id, String title, String content, Set<String> hashtags, LocalDateTime createdAt, String email, String nickname, String userId, Set<ArticleCommentResponse> articleCommentResponses) {
        return new ArticleWithCommentsResponse(id, title, content, hashtags, createdAt, email, nickname, userId, articleCommentResponses);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentsResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtagDtos().stream()
                        .map(HashtagDto::hashtagName)
                        .collect(Collectors.toUnmodifiableSet())
                ,
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.userAccountDto().userId(),
                dto.articleCommentDtos().stream()
                        .map(ArticleCommentResponse::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }

    /**
     * 댓글, 대댓글 세팅하는 메소드
     */
    private static Set<ArticleCommentResponse> organizeChildComments(Set<ArticleCommentDto> dtos) {
        // Set<ArticleCommentDto>를 Map<Long, ArticleCommentResponse>으로 변환하는 작업 실시
        Map<Long, ArticleCommentResponse> map = dtos.stream()
                .map(ArticleCommentResponse::from)
                .collect(Collectors.toMap(ArticleCommentResponse::id, Function.identity()));

        /**
         * 1. 부모댓글이 있는것만 filtering해서 꺼낸다음 사용한다.
         * 2. forEach 안의 comment는 filtering으로 꺼내온 자식댓글이다. 이 자식댓글을 반복문을 돌려 작업을 실시한다.
         * 3. comment.parentCommentId()로 부모 댓글id를 에서 꺼내서 가져온 다음 map.get()안에 id를 넣어서 ArticleCommentResponse를 받아온다.
         * 4. 이제 꺼내온 ArticleCommentResponse 객체 안에 childComments 필드인 Set에 필터링해서 꺼낸 자식 댓글을 넣어준다.
         */
        map.values().stream()
                .filter(ArticleCommentResponse::hasParentComment) //
                .forEach(comment -> {
                    ArticleCommentResponse parentComment = map.get(comment.parentCommentId());
                    parentComment.childComments().add(comment);
                });


        /**
         * 1. 한번 더 values 스트림을 돈다.
         * 2. 위의 stream과 반대로 여기서는 댓글이 부모 댓글을 가지고 있지 않은지 체크한다.(최상위 댓글인지 체크한다.)
         * 3. 체크를 하고 이 map의 내용을 Collection형태로 만들면서 TreeSet을 사용하여 댓글과 대댓글을 각각 정렬을 해준다. (대댓글 정렬, 댓글 정렬이 각각 따로 적용되어야 한다.)
         *    [순서가 보장되어야 하니 TreeSet을 사용한다.]
         * 4. Comparator.comparing(ArticleCommentResponse::createdAt)으로 생성시간을 가져온다.
         * 5. createdAt은 내림차순 정렬이니 제일 먼저 써주고 다음에 .reversed()로 오름차순으로 바꿔준다.
         * 6. 이후 .thenComparingLong(ArticleCommentResponse::id) 이 코드를 통해 혹시 동일한 시간이 존재할지도 모르니 그때를 대비해서 id를 기준으로 하는 오름차순도 넣어준다.
         */
        return map.values().stream()
                .filter(comment -> !comment.hasParentComment())
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator
                                .comparing(ArticleCommentResponse::createdAt)
                                .reversed() // 오름차순으로 바꿔준다.
                                .thenComparingLong(ArticleCommentResponse::id)

                        ) // 순서가 보장되어야 하니 TreeSet을 사용한다.
                    )); // 대댓글 정렬 따로 댓글 정렬이 따로 적용되어야 한다.

    }

}