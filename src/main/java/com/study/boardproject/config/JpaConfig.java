package com.study.boardproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing // auditing기능 추가(활성화 시킴)
@Configuration
public class JpaConfig {

    //auditing 할때 누가 추가, 수정했는지 사람의 이름을 설정해준다.
    @Bean
    public AuditorAware<String> auditorAware() {
        // TODO: 스프링 시큐리티로 인증 기능을 붙일때 수정해야 한다.
        return () -> Optional.of("jinan");
    }

}
