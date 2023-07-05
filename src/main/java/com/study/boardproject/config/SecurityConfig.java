package com.study.boardproject.config;

import com.study.boardproject.dto.UserAccountDto;
import com.study.boardproject.dto.security.BoardPrincipal;
import com.study.boardproject.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 최신 방법의 security 설정하는 filterChain 방식을 적용
 * spring security 6.1.x 기준 설정코드 작성
 */
//@EnableWebSecurity -> 이건 안넣어도 된다.
@Configuration
public class SecurityConfig {

    /**
     * 5.x부터 빈 등록과 filterChain방식을 권장한다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers( // spring security6부터는 mvcMatchers가 아닌 requestMatchers를 사용함
                                HttpMethod.GET,
                                "/",
                                "/articles",
                                "/articles/search-hashtag"
                        ).permitAll() // 위의 경로는 모두 허용한다.
                        .anyRequest().authenticated() // 남은것들은 룰을 적용시킨다.
                )
                .formLogin(Customizer.withDefaults())
                .logout(logout -> logout // 6.xx 버전부터는 로그아웃은 이렇게 처리해야 한다. (Customizer)
                        .logoutUrl("/") // 로그아웃 요청을 처리할 URL 설정
                        .logoutSuccessUrl("/articles") // 로그아웃 성공 후 리다이렉트할 URL 설정
                        .deleteCookies("remove") // 삭제할 쿠키 설정
                        .invalidateHttpSession(false) // HttpSession 무효화 여부 설정
                )
                .build();
    }

    /**
     * 인증과 권한체크를 하는 부분이다.
     * 이부분은 spring security 검사에서 제외하는 부분을 작성한다.
     * 근데 이 방식은 추천하지않는다고 warn이 나온다.
     */
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        //static resource, css, js
//        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }

    /**
     * 실제 인증 데이터를 가져오는 서비스를 구현한다.
     */
    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        return username -> userAccountRepository
                .findById(username) //entity를 받아온다.
                .map(UserAccountDto::from) //entity를 dto로 변환한다.
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. - username: " + username));
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
