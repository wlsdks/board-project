package com.study.boardproject.config;

import com.study.boardproject.domain.UserAccount;
import com.study.boardproject.repository.UserAccountRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * 테스트에 필요한 security관련 설정을 여기서 전부 처리하기위해 만들었다.
 */
@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean
    private UserAccountRepository userAccountRepository;

    // 스프링 테스트를 할때만 이게 동작한다. 각 테스트 메소드가 동작하기 전에 이 인증정보를 넣어준다.
    @BeforeTestMethod
    public void securitySetUp() {
        given(userAccountRepository.findById(anyString())).willReturn(Optional.of(UserAccount.of(
                "uno",
                "asdf1234",
                "uno@mail.com",
                "Uno",
                "wlsdks memo"
        )));
    }
}
