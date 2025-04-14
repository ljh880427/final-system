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
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthClient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int no;

  @Column(length = 50)
  private String email;

  @Column(length = 50, nullable = false)
  private String name;

  @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'LOCAL'")
  private String issuer;

  @Column(length = 100)
  private String oauthId;

  private String pwd;

  private int fileNo;

  @Column(name = "useYN", nullable = false, columnDefinition = "char default 'Y'")
  private char useYN;

  @Column(nullable = false)
  @CreationTimestamp
  @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
  private LocalDateTime regDate;

  @UpdateTimestamp
  @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
  private LocalDateTime modDate; // 수정일자

  @Transient // DB 컬럼 생성 X, 메모리에서만 사용
  private String parsRegDate;

  }

