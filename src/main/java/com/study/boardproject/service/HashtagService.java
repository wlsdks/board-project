package com.study.boardproject.service;

import com.study.boardproject.domain.Hashtag;
import com.study.boardproject.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Transactional
@Service
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    @Transactional(readOnly = true)
    public Set<Hashtag> findHashtagsByNames(Set<String> hashtagNames) {
        // List를 Set으로 변환시킨다.
        return new HashSet<>(hashtagRepository.findByHashtagNameIn(hashtagNames));
    }

    // 본문 파싱 함수
    public Set<String> parseHashtagNames(String content) {
        // 1. 본문 null 체크
        if (content == null) {
            return Set.of(); //null이면 빈 Set을 반환
        }

        Pattern pattern = Pattern.compile("#[\\w가-힣]+");
        Matcher matcher = pattern.matcher(content.strip()); // strip으로 앞뒤의 공백을 자른다.
        Set<String> result = new HashSet<>();

        while (matcher.find()) {
            result.add(matcher.group().replace("#", ""));
        }

        //불변객체 Set을 반환한다.
        return Set.copyOf(result);
    }

    // 해시태그를 지우는데 게시글이 없는경우 -> 구조상 모든 글에서 해시태그가 없어졌을때 해시태그가 지워져야 한다.
    public void deleteHashtagWithoutArticles(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.getReferenceById(hashtagId);
        // 만약 게시글이 비었다면
        if (hashtag.getArticles().isEmpty()) {
            hashtagRepository.delete(hashtag);
        }
    }

}
