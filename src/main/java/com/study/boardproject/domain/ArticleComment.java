package com.study.boardproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ArticleComment extends AuditingFields{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //optinoal = false는 필수값이라는 뜻이다.
    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "article_id")
    private Article article; // 게시글 (ID)

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount; // 유저 정보 (ID)

    @Setter
    @Column(nullable = false, length = 500)
    private String content; // 본문

    private ArticleComment(Article article, UserAccount userAccount, String content) {
        this.article = article;
        this.userAccount = userAccount;
        this.content = content;
    }

    public static ArticleComment of(Article article,UserAccount userAccount, String content) {
        return new ArticleComment(article, userAccount, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
