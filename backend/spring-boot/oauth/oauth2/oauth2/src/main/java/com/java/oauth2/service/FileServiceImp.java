package com.java.oauth2.service;

import com.java.oauth2.dto.FileDTO;
import com.java.oauth2.entity.FileInfo;
import com.java.oauth2.dto.FileResDTO;
import com.java.oauth2.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImp implements FileService {

	private final FileInfoRepository fileInfoRepository;

	// application.properties에서 호스팅 도메인 정보를 가져옴
	@Value("${hosting.uri}")
	private String hostingUri;

	//private String baseUrl = "http://d.0neteam.co.kr:9000/file/uri";
	private String baseUrl = hostingUri + "/file/uri";
	private String getRootPath() {return new File("").getAbsolutePath();}
	private String lastPath = "/upload";
	private String getCurrnetDatePath() {return "/".concat(new SimpleDateFormat("yyyyMMdd").format(new Date()));}
	private String getName(MultipartFile file) {return file.getOriginalFilename();}
	private String setName() {return Long.toString(System.nanoTime());}
	private String getContentType(MultipartFile file) {return file.getContentType();}
	private String getExtension(MultipartFile file) {
		String contentType = file.getContentType();
		String name = getName(file);
		String originalFileExtension = "";
		if (!ObjectUtils.isEmpty(contentType)){
			if(contentType.contains("image/jpeg")){originalFileExtension = ".jpg";}
			else if(contentType.contains("image/png")){originalFileExtension = ".png";}
			else if(contentType.contains("image/gif")){originalFileExtension = ".gif";}
			else if(name.lastIndexOf(".") > 0){originalFileExtension = name.substring(name.lastIndexOf("."), name.length());}
		}
		return originalFileExtension;
	}

	@Override
	public FileResDTO upload(MultipartFile file, int userNo) {
		int success = 0;
		FileDTO fileDTO = null;
		if(!file.isEmpty()){
			String orgin = getName(file);
			String name = setName();
			String ext = getExtension(file);
			String mediaType = getContentType(file);
			String attachPath = getRootPath().concat(lastPath)
					.concat(getCurrnetDatePath())
					.concat("/" + userNo);
			System.out.println("attachPath : " + attachPath);
			try {
				File newFile = new File(attachPath.concat("/").concat(name).concat(ext));
				if(!newFile.exists()){newFile.mkdirs();}
				file.transferTo(newFile);
				FileInfo fileInfo = FileInfo.builder()
						.orgin(orgin)
						.name(name)
						.attachPath(attachPath)
						.ext(ext)
						.useYN('Y')
						.regUserNo(userNo)
						.mediaType(mediaType)
						.build();
				fileInfo = fileInfoRepository.save(fileInfo);
				if(fileInfo.getNo() > 0) {
					System.out.println("url = " + baseUrl.concat("/"+fileInfo.getNo()));
					success = 1;
					fileDTO = FileDTO.builder()
							.no(fileInfo.getNo())
							.url(baseUrl.concat("/"+fileInfo.getNo()))
							.build();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return FileResDTO.builder()
				.success(success)
				.file(fileDTO)
				.build();
	}

	@Override
	public ResponseEntity<?> uri(Integer fileNo, int userNo) {
		// 파일 소유권 확인 추가?
		FileInfo fileInfo = fileInfoRepository.findById(fileNo).orElseThrow();

		//요청한 파일의 등록한 유저와 현재 로그인한 유저가 동일한 경우만 볼수 있도록 설정
		if (fileInfo.getRegUserNo() != userNo) {
			return ResponseEntity.notFound().build();
		}

		try {
			String attachPath = fileInfo.getAttachPath();
			String orgin = fileInfo.getOrgin();
			String name = fileInfo.getName();
			String ext = fileInfo.getExt();
			String mediaType = fileInfo.getMediaType();
			File file = new File(attachPath.concat("/").concat(name).concat(ext));

			// 파일명이 한글일 경우 UTF-8로 인코딩
			String encodedFileName = URLEncoder.encode(orgin, StandardCharsets.UTF_8).replace("+", "%20");

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);
			return ResponseEntity.ok()
					.headers(headers)
					.contentLength(file.length())
					.contentType(MediaType.parseMediaType(mediaType))
					.body(new InputStreamResource(new FileInputStream(file)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.notFound().build();
	}

}