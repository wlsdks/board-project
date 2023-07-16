package com.study.boardproject.dto.security;

import com.study.boardproject.dto.UserAccountDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 1. Spring Security 인증 전용 클래스 -> UserDetails를 implements(구현)해서 인증정보라는것을 만들어서 사용했다.
 * 2. OAuth2를 위한 인증 전용 클래스
 *
 */
public record BoardPrincipal(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String email,
        String nickname,
        String memo,
        Map<String, Object> oAuth2Attributes // OAuth2 전용 필드
) implements UserDetails, OAuth2User {

    // OAuth2와 관련없는 생성자 factory 메소드 oAuth2Attributes 필드를 사용하지 않는 코드를 위함
    public static BoardPrincipal of(String username, String password, String email, String nickname, String memo) {
        return of(username, password, email, nickname, memo, Map.of()); //비어있는 Map을 하나 넘겨준다.(null값은 null safe에 좋지않음)
    }

    // OAuth2에 대응되는 생성자 factory 메소드
    public static BoardPrincipal of(String username, String password, String email, String nickname, String memo, Map<String, Object> oAuth2Attributes) {

        Set<RoleType> roleTypes = Set.of(RoleType.USER);

        return new BoardPrincipal(
                username,
                password,
                roleTypes.stream()
                        .map(RoleType::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet())
                ,
                email,
                nickname,
                memo,
                oAuth2Attributes // OAuth2에는 이 필드가 추가된다.
        );
    }

    // dto를 받아서 인증정보 데이터타입인 BoardPrincipal로 변환해주는 factory 메소드
    public static BoardPrincipal from(UserAccountDto dto) {
        return BoardPrincipal.of(
                dto.userId(),
                dto.userPassword(),
                dto.email(),
                dto.nickname(),
                dto.memo()
        );
    }

    public UserAccountDto toDto() {
        return UserAccountDto.of(
                username,
                password,
                email,
                nickname,
                memo
        );
    }


    /**
     * Spring Security 기능 사용을 위한 구현
     */
    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return password; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    /**
     * OAuth2 기능 사용을 위한 구현
     * getAttributes() 는 필드에서 만든것을 return 해주면 된다.(인증정보가 oAuth2Attributes 안에 있어야 한다.)
     */
    @Override public Map<String, Object> getAttributes() { return oAuth2Attributes; }
    @Override public String getName() { return username; } // username을 return 해주도록 하면 된다.

    public enum RoleType {
        USER("ROLE_USER");

        @Getter private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }

}
