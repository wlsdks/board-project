package com.study.boardproject.service;

import com.study.boardproject.domain.UserAccount;
import com.study.boardproject.dto.UserAccountDto;
import com.study.boardproject.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor //생성자 주입
@Transactional //db접근
@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    /**
     * repository에서 가져오는 entity정보는 service 외부로는 나가지 않도록 dto를 사용한다.(격리를 시킨다.)
     * 이 메소드에서 마지막에 orElse 예외처리를 해주지 않은것은 이 service 레이어에서 에러처리를 전담하지않기 위함이다.(상위로 전파)
     */
    public Optional<UserAccountDto> searchUser(String username) {
        return userAccountRepository.findById(username)
                .map(UserAccountDto::from);
    }

    public UserAccountDto saveUser(String username, String password, String email, String nickname, String memo) {
        return UserAccountDto.from(
                userAccountRepository.save(UserAccount.of(username, password, email, nickname, memo, username)) //UserAccount.of로 entity를 만들어서 넣어준다.
        );
    }

}
