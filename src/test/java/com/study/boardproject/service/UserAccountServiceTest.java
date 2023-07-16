package com.study.boardproject.service;

import com.study.boardproject.domain.UserAccount;
import com.study.boardproject.dto.UserAccountDto;
import com.study.boardproject.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("비즈니스 로직 - 회원")
@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @InjectMocks
    private UserAccountService sut;

    @Mock
    private UserAccountRepository userAccountRepository;

    @DisplayName("존재하는 회원 ID를 검색하면, 회원 데이터를 Optional로 반환한다.")
    @Test
    void test1() {
        //given
        String username = "uno";
        given(userAccountRepository.findById(username)).willReturn(Optional.of(createUserAccount(username)));

        //when
        Optional<UserAccountDto> result = sut.searchUser(username);

        //then
        assertThat(result).isPresent();
        then(userAccountRepository).should().findById(username);
    }

    @DisplayName("존재하지 않는 회원 ID를 검색하면, 비어있는 Optional을 반환한다.")
    @Test
    void test2() {
        //given
        String username = "wrong-user";
        given(userAccountRepository.findById(username)).willReturn(Optional.empty());

        //when
        Optional<UserAccountDto> result = sut.searchUser(username);

        //then
        assertThat(result).isEmpty();
        then(userAccountRepository).should().findById(username);
    }

    @DisplayName("회원 정보를 입력하면, 새로운 회원 정보를 저장하여 가입시키고 해당 회원 데이터를 리턴한다.")
    @Test
    void test3() {
        //given
        UserAccount userAccount = createUserAccount("uno");
        UserAccount savedUserAccount = createSigningUpUserAccount("uno");
        given(userAccountRepository.save(userAccount)).willReturn(savedUserAccount);

        //when
        UserAccountDto result = sut.saveUser(
                userAccount.getUserId(),
                userAccount.getUserPassword(),
                userAccount.getEmail(),
                userAccount.getNickname(),
                userAccount.getMemo()
        );

        //then
        assertThat(result)
                .hasFieldOrPropertyWithValue("userId", userAccount.getUserId())
                .hasFieldOrPropertyWithValue("userPassword", userAccount.getUserPassword())
                .hasFieldOrPropertyWithValue("email", userAccount.getEmail())
                .hasFieldOrPropertyWithValue("nickname", userAccount.getNickname())
                .hasFieldOrPropertyWithValue("memo", userAccount.getMemo())
                .hasFieldOrPropertyWithValue("createdBy", userAccount.getUserId())   // 검사에서는 얘들도 들어가졌는지 봐야한다.
                .hasFieldOrPropertyWithValue("modifiedBy", userAccount.getUserId()); // 검사에서는 얘들도 들어가졌는지 봐야한다.

        then(userAccountRepository).should().save(userAccount);
    }

    private UserAccount createUserAccount(String username) {
        return createUserAccount(username, null);
    }

    private UserAccount createSigningUpUserAccount(String username) {
        return createUserAccount(username, username);
    }

    private UserAccount createUserAccount(String username, String createdBy) {
        return UserAccount.of(
                username,
                "passwod",
                "email.com",
                "nickname",
                "memo",
                createdBy
        );
    }

}