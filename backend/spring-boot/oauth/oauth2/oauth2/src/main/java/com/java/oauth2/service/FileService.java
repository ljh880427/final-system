package com.java.oauth2.service;

import com.java.oauth2.dto.FileResDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

	public FileResDTO upload(MultipartFile file, int userNo);
	public ResponseEntity<?> uri(Integer fileNo, int userNo);
	
}
