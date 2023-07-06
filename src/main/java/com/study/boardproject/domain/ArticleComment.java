package com.study.boardproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

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

    @Setter
    @ManyToOne(optional = false) //optinoal = false는 필수값이라는 뜻이다.
    @JoinColumn(name = "articleId")
    private Article article; // 게시글 (ID)

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount; // 유저 정보 (ID)

    @Setter
    @Column(updatable = false) // 부모가 없는 댓글이 있을수도있으니 nullable 해야한다. -> 부모댓글을 세팅하면 바뀔수없도록 해준다.
    private Long parentCommentId; // 부모 댓글 ID

    @ToString.Exclude // 연관관계 ToString 순환참조 방지
    @OrderBy("createdAt ASC") // 정렬규칙을 정해준다.
    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL) // 부모 댓글을 지운다면 자식 댓글도 다 지운다. (cascade 조건을 넣어줌)
    private Set<ArticleComment> childComments = new LinkedHashSet<>(); //순서가 있는 set선언

    @Setter
    @Column(nullable = false, length = 500)
    private String content; // 본문

    // 일반적인 생성자인데 private이니 아래의 factory메소드를 사용하게 될것임
    private ArticleComment(Article article, UserAccount userAccount, Long parentCommentId, String content) {
        this.article = article;
        this.userAccount = userAccount;
        this.parentCommentId = parentCommentId;
        this.content = content;
    }

    // comment 부모관계를 주입하는 메소드
    public void addChildComment(ArticleComment child) {
        // 안전하게 childComments에 getter로 접근한다.
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }

    // ArticleComment entity를 만드는 factory 메소드 선언 -> 이게 생성자가 된다. (코드 변경에 대한 영향을 최소화할수있음)
    public static ArticleComment of(Article article,UserAccount userAccount, String content) {
        return new ArticleComment(article, userAccount, null, content);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
