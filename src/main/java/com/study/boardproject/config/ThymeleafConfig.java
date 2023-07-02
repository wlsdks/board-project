package com.study.boardproject.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

/**
 * thymeleaf 템플릿 전용 설정 추가
 */
@Configuration
public class ThymeleafConfig {

    @Bean
    public SpringResourceTemplateResolver thymeleafTemplateResolver(
            SpringResourceTemplateResolver defaultTemplateResolver,
            Thymeleaf3Properties thymeleaf3Properties
    ) {
        // 원래있던 decoupled 로직에 설정을 추가해준 코드다.
        defaultTemplateResolver.setUseDecoupledLogic(thymeleaf3Properties.isDecoupledLogic());

        return defaultTemplateResolver;
    }

    @RequiredArgsConstructor
    @Getter
//    @ConstructorBinding // 3.x.x 버전부터는 필드에 적용시킨 이 어노테이션은 불필요하다고 한다.
    @ConfigurationProperties("spring.thymeleaf3") // thymeleaf3 로 yml 파일에서 설정을 시작한다는 의미이다.
    public static class Thymeleaf3Properties {
        /**
         * Use Thymeleaf 3 Decoupled Logic
         */
        private final boolean decoupledLogic;
    }

}
