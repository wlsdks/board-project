package com.study.boardproject.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


/**
 * auditing 필드를 한곳에 모아놓는다.
 * auditing을 위해서 @EntityListeners 어노테이션을 추가해야 한다.(최상위인 여기에 선언해주면 다 적용된다.)
 * 이 field 를 적용시킬 entity 클래스에서 이 클래스를 상속받으면 적용된다.
 * OAuth2를 통해 접근하기 위해 private 를 protected로 열어주었다.
 */
@ToString
@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AuditingFields {

    // datetime의 format을 설정
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false) // 최초 한번만 설정되니 업데이트가 불가능하도록 설정해준다.
    protected LocalDateTime createdAt; //생성일시

    @CreatedBy
    @Column(nullable = false, length = 100)
    protected String createdBy; // 생성자

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = false)
    protected LocalDateTime modifiedAt; // 수정일시

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    protected String modifiedBy; //수정자

}
