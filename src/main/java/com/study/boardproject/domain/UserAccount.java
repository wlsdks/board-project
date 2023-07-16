package com.study.boardproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString(callSuper = true) //auditing 필드도 toString 대상에 넣어준다.
@Table(indexes = {
        @Index(columnList = "email", unique = true),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class UserAccount extends AuditingFields {

    // 이 엔티티만 id값을 Long을 안쓰고 String을 사용했다. -> reference를 빠르게 찾기 위함
    // String을 pk로 사용중이다.
    @Id
    @Column(length = 50)
    private String userId;

    @Setter
    @Column(nullable = false)
    private String userPassword;

    @Setter
    @Column(length = 100)
    private String email;

    @Setter
    @Column(length = 100)
    private String nickname;

    @Setter
    private String memo;

    protected UserAccount() {}

    // private 생성자 -> factory method를 만들기 위해 private로 선언한다.
    private UserAccount(String userId, String userPassword, String email, String nickname, String memo, String createdBy) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.email = email;
        this.nickname = nickname;
        this.memo = memo;
        this.createdBy = createdBy;
        this.modifiedBy = createdBy; // 최초에는 생성자와 수정자가 같으므로 둘다 createdBy로 해준다.
    }

    // UserAccount를 만드는 factory 메소드1 - 인증정보가 없을때(최초 회원가입일때)
    public static UserAccount of(String userId, String userPassword, String email, String nickname, String memo, String createdBy) {
        return new UserAccount(userId, userPassword, email, nickname, memo, createdBy);
    }

    // UserAccount를 만드는 factory 메소드2 - 인증정보가 이미 있을때
    public static UserAccount of(String userId, String userPassword, String email, String nickname, String memo) {
        // 이미 createdBy가 있는 상태일것이라 createdBy 자리에 null을 넣는다.
        return new UserAccount(userId, userPassword, email, nickname, memo, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount that)) return false;
        return this.getUserId() != null && this.getUserId().equals(that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId());
    }

}
