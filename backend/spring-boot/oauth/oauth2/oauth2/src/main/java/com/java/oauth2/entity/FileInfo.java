package com.java.oauth2.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fileinfo")
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer no;
	
	@Column(nullable = false, length = 100)
	private String orgin;
	
	@Column(nullable = false, length = 100)
	private String name;
	
	@Column(nullable = false, length = 50)
	private String attachPath;
	
	@Column(nullable = false, length = 10)
	private String ext;
	
	@Column(nullable = false)
	private Character useYN;
	
	@CreationTimestamp
    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
	private LocalDateTime regDate;
	
	@Column(nullable = false)
	private Integer regUserNo;

	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy.MM.dd HH:mm")
	private LocalDateTime modDate;
	private Integer modUserNo;
	
	@Column(nullable = false, length = 255)
	private String mediaType;

}
