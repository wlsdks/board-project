package com.study.boardproject.config;

import com.study.boardproject.dto.security.BoardPrincipal;
import com.study.boardproject.dto.security.KakaoOAuth2Response;
import com.study.boardproject.service.UserAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * 최신 방법의 security 설정하는 filterChain 방식을 적용
 * spring security 6.1.x 기준 설정코드 작성
 */
//@EnableWebSecurity -> 이건 안넣어도 된다.
@Configuration
public class SecurityConfig {

    /**
     * Spring Security 등록코드 작성
     * 5.x부터 빈 등록과 filterChain방식을 권장한다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) throws Exception {

        /**
         * 람다식을 도입한 이유로는 그냥 체이닝 방식보다 indent의 일관된 규칙을 적용할수가 있다.(IDE가 자동으로 처리해 준다.)
         * 참고로 spring security6부터는 mvcMatchers가 아닌 requestMatchers를 사용한다.
         * PathRequest.toStaticResources().atCommonLocations() 이것으로 흔히 사용되는 공통 경로(img, css, js, favicon, icon)는 다 허용해준다.
         */
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/",
                                "/articles",
                                "/articles/search-hashtag"
                        ).permitAll()                           // 위의 경로는 모두 허용한다는 의미이다.
                        .anyRequest().authenticated()           // 남은것들은 룰을 적용시킨다.
                )
                .formLogin(withDefaults())
                .logout(logout -> logout                        // 5.xx 버전부터는 로그아웃은 이렇게 처리해야 한다. (Customizer.withDefaults() 사용)
//                        .logoutUrl("/")                       // 로그아웃 요청을 처리할 URL 설정
                                .logoutSuccessUrl("/")          // 로그아웃 성공 후 리다이렉트할 URL 설정
//                        .deleteCookies("remove")              // 삭제할 쿠키 설정
//                        .invalidateHttpSession(false)         // HttpSession 무효화 여부 설정
                )
                .oauth2Login(oAuth -> oAuth                     // OAuth2 로그인 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                )
                .build();
    }

    /**
     * 이녀석은 db를 기반으로 인증정보를 불러와서 user정보를 return하는 역할을 한다.
     * OAuth2에도 이런역할 하는게 필요한데 이걸 사용할수는 없어서 따로 만들어서 @Bean으로 등록해준다.
     */
    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService) {
        return username -> userAccountService
                .searchUser(username)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username: " + username));
    }

    /**
     * <p>
     * OAuth 2.0 기술을 사용한 사용자 인증을 위한 메소드다. OAuth2UserService -> @FunctionalInterface 라서 람다식으로 작성이 가능하다.
     * 여기가 매우 중요하다! 핵심 기능이다. 이 코드는 위의 userDetailsService와 같은 역할을 하는 녀석이다.
     *
     * <p>
     * TODO: 카카오 도메인에 결합되어 있는 코드. 확장을 고려하면 별도의 인증 처리 서비스 클래스로 분리하는것이 좋겠지만 현재 다른 OAuth 인증이 없으니 우선은 이대로 사용한다.
     *
     * @param userAccountService  게시판 서비스의 사용자 계정을 다루는 서비스 로직
     * @param passwordEncoder  패스워드 암호화 도구
     * @return {@link OAuth2UserService} OAuth2 인증 사용자 정보를 읽어들이고 처리하는 서비스 인스턴스를 반환한다.
     */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ) {
        // ioc 컨테이너로 빈으로 주입하면 좋지만 여기서는 new로 생성하는게 더 편리하다. DefaultOAuth2UserService는 Bean으로 등록되어있지 않다.
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        // 입력은 userRequest로 받는다.
        return userRequest -> {

            // 1. 람다식의 입력을 넣어주고 user정보를 가져온다.
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            // 2. oAuth2User로부터 데이터를 Map<>으로 받아서 내가만든 factory메소드에 값을 넣어줘서 response 데이터를 만든다.
            KakaoOAuth2Response kakaoResponse = KakaoOAuth2Response.from(oAuth2User.getAttributes());

            // 3. 이것은 고유값이다. registrationId를 뽑아낸다. yml파일의 client:registration:kakao 에서 [kakao]이다.-> [provider의 id]
            String registrationId = userRequest.getClientRegistration().getRegistrationId();

            // 4. 우리가 만든 response에서 Id를 가져온다.
            String providerId = String.valueOf(kakaoResponse.id());

            // 5. 최종적으로 위에서 만든 값을 가지고 [kakao_{고유값}] 이렇게 username을 만들어줘서 user를 검색하고 없으면 회원가입을 시켜준다.
            String username = registrationId + "_" + providerId;

            // 6. 비밀번호도 만들어준다. -> 카카오 로그인에는 필요없지만 필드자체를 not null로 설계해버려서 가짜를 만들어준다. bcrypt로 인코딩해서 넣는다.
//            String dummyPassword = passwordEncoder.encode("{bcrypt}dummy");
            String dummyPassword = passwordEncoder.encode("{bcrypt}" + UUID.randomUUID());

            // 7. return으로 db에 유저가 있다면 ok , db에 유저가 없다면 가입을 시킨다.
            return userAccountService.searchUser(username)
                    .map(BoardPrincipal::from)             // 이미 kakao 인증정보가 있을경우 동작
                    .orElseGet(() ->                       // 카카오 인증정보가 없을경우엔 이게 동작
                            BoardPrincipal.from(
                                    userAccountService.saveUser(
                                            username,
                                            dummyPassword,
                                            kakaoResponse.email(),
                                            kakaoResponse.nickname(),
                                            null
                                    )
                            )
                    );
        };
    }

    /**
     * password 인코더 구현
     * spring security 암호화 모듈을 사용한다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}
