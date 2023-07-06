package com.study.boardproject.service;

import com.study.boardproject.domain.Hashtag;
import com.study.boardproject.repository.HashtagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@DisplayName("비즈니스 로직 - 해시태그")
@ExtendWith(MockitoExtension.class)
class HashtagServiceTest {

    // sut -> system under test라는 의미 (테스트 대상을 표현)
    @InjectMocks
    private HashtagService sut;

    // sut에서 사용되는 hashtagRepository는 아래의 Mock으로 주입될것이다.
    @Mock
    private HashtagRepository hashtagRepository;


    @DisplayName("본문을 파싱하면, 해시태그 이름들을 중복없이 반환한다.")
    @MethodSource
    @ParameterizedTest(name = "[{index}] \"{0}\" => {1}")
        // 파라미터를 사용하는 테스트
    void givenContent_whenParsing_thenReturnUniqueHashtagNames(String input, Set<String> expected) {
        //given

        //when
        Set<String> actual = sut.parseHashtagNames(input);

        //then -> containsExactlyInAnyOrderElementsOf
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        then(hashtagRepository).shouldHaveNoInteractions(); // 상호작용한게 전혀 없을때 shouldHaveNoInteractions()를 사용한다.
    }

    // 파라미터 테스트의 입력값은 test와 동일한 이름으로 만들어준다.
    static Stream<Arguments> givenContent_whenParsing_thenReturnUniqueHashtagNames() {
        return Stream.of(
                // arguments안에 테스트할 함수에서 사용될 인자를 넣어준다.
                arguments(null, Set.of()), // #java를 발견하면 java라는것을 뽑아낸다.
                arguments("", Set.of()),
                arguments(" ", Set.of()),
                arguments("#", Set.of()),
                arguments("#  ", Set.of()),
                arguments("  #", Set.of()),
                arguments("#java", Set.of("java")),
                arguments("#java_spring", Set.of("java_spring")),
                arguments("#java#spring", Set.of("java", "spring")),
                arguments("ja#va", Set.of("va")),
                arguments("아주 긴 글~~~~~~~~~~~~~~~#java#스프링", Set.of("java", "스프링"))
        );
    }

    @DisplayName("해시태그 이름들을 입력하면, 저장된 해시태그 중 이름에 매칭하는 것들을 중복 없이 반환한다.")
    @Test
    void givenHashtagNames_whenFindingHashtags_thenReturnHashtagSet() {
        //given
        Set<String> hashtagNames = Set.of("java", "spring", "boots");
        given(hashtagRepository.findByHashtagNameIn(hashtagNames)).willReturn(List.of(
                Hashtag.of("java"),
                Hashtag.of("spring")
        ));

        //when
        Set<Hashtag> hashtags = sut.findHashtagsByNames(hashtagNames);

        //then
        assertThat(hashtags).hasSize(2);
        then(hashtagRepository).should().findByHashtagNameIn(hashtagNames);
    }
}