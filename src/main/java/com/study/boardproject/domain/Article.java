package com.study.boardproject.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true) // 쉽게 출력하도록함
@NoArgsConstructor(access = AccessLevel.PROTECTED) // entity는 항상 protected레벨의 기본 생성자를 만들어줘야 한다.
@Table(indexes = { // index를 걸어준다.
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends AuditingFields{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount; // 유저 정보ID

    // @Setter를 필드에 걸어서 정말 필요한 필드에만 걸어준다. Id같은건 못건들게 한다. nullable은 default는 true이다.
    @Setter
    @Column(nullable = false)
    private String title; // 제목

    @Setter
    @Column(nullable = false, length = 10000)
    private String content; // 본문

    @Setter private String hashtag; // 해시태그

    // List,Set으로 설정가능 (중복허용x로 set사용함)
    @ToString.Exclude // 순환참조를 방지하기위해 toString에서 제외시켜버린다. (중요!!)
    @OrderBy("createdAt DESC") // 정렬기준은 id로 정렬을 추가한다.
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();


    // 실제로 만들때 필요한 값만을 뽑아내서 생성자를 만든다. id, 생성시간, 생성자 등은 만들때 필요없고 자동으로 등록된다.
    public Article(UserAccount userAccount, String title, String content, String hashtag) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    // Article 엔티티를 가진녀석을 외부에서 쉽게 만들수있도록 of() 메서드를 만든다.
    public static Article of(UserAccount userAccount, String title, String content, String hashtag) {
        return new Article(userAccount, title, content, hashtag);
    }

    /**
     * lombok의 equalsAndHashcode를 사용하지않고 cmd+n으로 직접 equals()and hashCode()로 만들어준다.
     * lombok은 모든필드를 변환해서 검사하는데 이렇게 직접만들면 pk인 id값만으로 만들어서 최적화가 가능하다.
     * 이후 return문에 id != null && 를 맨앞에 붙여줘서 id가 부여되지 않았으면(아직 영속화가 안되었으면) 동등성 검사가 의미가 없다고 해주는것이다.
     * 만약 id가 있다면 id가 같은지만 보고 id가 같다면 같은 객체라고 동등성 검사를 하는것이다.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return id != null && Objects.equals(id, article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
