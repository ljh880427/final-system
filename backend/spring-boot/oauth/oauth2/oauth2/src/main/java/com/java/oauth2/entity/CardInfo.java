package com.java.oauth2.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "card")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int no;

    @Column(nullable = false)
    private int regUserNo; // 작성자 (다른 테이블과 연관)

    @Column(length = 100)
    private String email;

    @Column(length = 100, nullable = false)
    private String name;

//    @Column(nullable = false, columnDefinition = "TEXT")
    private String memo; // 글내용

    @Column(length = 100)
    private String company;

    @Column(length = 20, nullable = false)
    private String tel;

    @Column(length = 20)
    private String fax;

    private String adr;

    @Column(length = 150) // 직위
    private String position;

    @Column(nullable = true)
    private int fileUserNo;

    @Column(nullable = true)
    private int filePictureNo;

    @Column(name = "useYN", nullable = false, columnDefinition = "char default 'Y'")
    private char useYN;

    @Column(nullable = false)
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime regDate; // 등록일자

    @Column(nullable = true)
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime modDate; // 수정일자

    @Transient // DB 컬럼 생성 X, 메모리에서만 사용
    private String parsRegDate;

}
