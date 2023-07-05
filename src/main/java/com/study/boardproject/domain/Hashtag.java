package com.study.boardproject.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true) //callSuper로 auditing필드까지 보이도록함
@Table(indexes = {
        @Index(columnList = "hashtagName", unique = true), // unique 등록
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
@Entity
public class Hashtag extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude // toString 순환참조를 방지해준다.
    @ManyToMany(mappedBy = "hashtags") // 연습으로 사용해봄
    private Set<Article> articles = new LinkedHashSet<>(); // 정렬을 감안해서 LinkedHashSet을 사용함

    @Setter
    @Column(nullable = false)
    private String hashtagName; // 해시태그 이름

    // id, 연관관계를 제외한 생성자 만들기
    private Hashtag(String hashtagName) {
        this.hashtagName = hashtagName;
    }

    // 생성자 factory 메소드를 만들어 준다.
    public static Hashtag of(String hashtagName) {
        return new Hashtag(hashtagName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hashtag that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }


}
