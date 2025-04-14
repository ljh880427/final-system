package com.java.oauth2.dto;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResDTO {

	private int success;
	private FileDTO file;
		
}
