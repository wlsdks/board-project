package com.study.boardproject.dto.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 이 필드들은 Kakao developer 문서에 적혀있는 Response를 그대로 가져와서 만들어준 것이다.
 * inner class의 주의점: 만약 이 inner class의 record들이 여기서만 쓰이는게 아니라 바깥의 다른 class에서 참조가 될것이라면
 * 이렇게 inner 형태로 record를 계속 작성해서 만드는게 아니라 별도의 record class파일로 빼내서 만드는것이 좋다.
 */
@SuppressWarnings("unchecked") // TODO: Map -> Object 변환 로직이 있어 제네릭 타입 캐스팅 문제를 무시한다. 더 좋은 방법이 있다면 고려할 수 있음.
public record KakaoOAuth2Response(
        Long id,
        LocalDateTime connectedAt,
        Map<String, Object> properties,
        KakaoAccount kakaoAccount
) {
    /**
     * KakaoOAuth2Response rocord 내부에 Kakoo계정에 대한 KakaoAccount record를 선언했다.
     */
    public record KakaoAccount(
            Boolean profileNicknameNeedsAgreement,
            Profile profile,
            Boolean hasEmail,
            Boolean emailNeedsAgreement,
            Boolean isEmailValid,
            Boolean isEmailVerified,
            String email
    ) {
        /**
         * KakaoAccount record 내부에 Profile record를 선언했다.
         */
        public record Profile(String nickname) {
            public static Profile from(Map<String, Object> attributes) {
                return new Profile(String.valueOf(attributes.get("nickname")));
            }
        }

        /**
         * attributes를 받아서 KakaoAccount를 만들어주는 factory 메소드
         * String.valueOf()는 좀 더 섬세한 처리가 되어있다. -> null safe한 로직이 추가되어 있다.
         */
        public static KakaoAccount from(Map<String, Object> attributes) {
            return new KakaoAccount(
                    Boolean.valueOf(String.valueOf(attributes.get("profile_nickname_needs_agreement"))),
                    Profile.from((Map<String, Object>) attributes.get("profile")),
                    Boolean.valueOf(String.valueOf(attributes.get("has_email"))),
                    Boolean.valueOf(String.valueOf(attributes.get("email_needs_agreement"))),
                    Boolean.valueOf(String.valueOf(attributes.get("is_email_valid"))),
                    Boolean.valueOf(String.valueOf(attributes.get("is_email_verified"))),
                    String.valueOf(attributes.get("email"))
            );
        }

        public String nickname() { return this.profile().nickname(); }
    }

    // attributes를 받아서 Response로 만들어주는 factory 메소드를 정의한다.
    public static KakaoOAuth2Response from(Map<String, Object> attributes) {
        // attributes에서 값을 꺼내 KakaoOAuth2Response의 생성자를 통해 값을 세팅하여 KakaoOAuth2Response 인스턴스 객체를 생성해준다.
        return new KakaoOAuth2Response(
                Long.valueOf(String.valueOf(attributes.get("id"))),                      // id 세팅
                LocalDateTime.parse(                                                     // 연결시간 세팅
                        String.valueOf(attributes.get("connected_at")),
                        DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault())   // ISO_INSTANT로 변환을 해줘야 한다.
                ),
                (Map<String, Object>) attributes.get("properties"),                      // properties 세팅
                KakaoAccount.from((Map<String, Object>) attributes.get("kakao_account")) // KakaoAccount 세팅.
        );
    }

    // 잘 사용될것같은것을 getter로 만들어준 것이다.
    public String email() { return this.kakaoAccount().email(); }

    /**
     * properties에도 nickname이 존재하지만 그것 말고 KakaoAccount()내부의 nickname을 사용해야 한다.
     * properties.nickname -> deprecated 되었다.
     */
    public String nickname() { return this.kakaoAccount().nickname(); }

}
