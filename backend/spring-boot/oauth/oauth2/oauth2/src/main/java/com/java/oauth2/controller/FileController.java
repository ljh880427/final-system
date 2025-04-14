package com.java.oauth2.controller;

import com.java.oauth2.common.Utils;
import com.java.oauth2.dto.FileResDTO;
import com.java.oauth2.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {
	
	private final FileService fileService;

	private final Utils utils;
	
	@GetMapping
	public String file() {
		return "file";
	}

	@ResponseBody
	@PostMapping("/upload")
	public FileResDTO upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		// 로그인 (Token에 담겨 있는 사용자 정보로 User 테이블 PK 값 가져오기)

		String userNo = utils.getUserNo(request);
		log.info("USER : {}", userNo);
		if("".equals(userNo)) userNo = "1";
		return fileService.upload(file, Integer.parseInt(userNo));
	}
	
	@GetMapping("/uri/{fileNo}")
	public ResponseEntity<?> uri(@PathVariable("fileNo") Integer fileNo, HttpServletRequest request) {
		// 로그인 (Token에 담겨 있는 사용자 정보로 User 테이블 PK 값 가져오기)
		String userNo = utils.getUserNo(request);
		log.info("USER : {}", userNo);
		if("".equals(userNo)) userNo = "1";
		return fileService.uri(fileNo, Integer.parseInt(userNo));
	}
	
}
