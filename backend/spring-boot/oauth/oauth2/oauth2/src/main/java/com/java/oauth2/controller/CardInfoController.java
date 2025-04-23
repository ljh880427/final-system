package com.java.oauth2.controller;

import com.java.oauth2.common.Utils;
import com.java.oauth2.dto.FileDTO;
import com.java.oauth2.dto.FileResDTO;
import com.java.oauth2.entity.CardInfo;
import com.java.oauth2.repository.CardInfoRepository;
import com.java.oauth2.service.CardService;
import com.java.oauth2.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@EnableMethodSecurity(prePostEnabled = true)  // ★ ROLE_ 룰을 정의할때 이거 꼭 필요함
@CrossOrigin(origins = { "http://localhost:5178", "http://l.0neteam.co.kr:5178" }, allowCredentials = "true")
@RequestMapping("/api")
@Slf4j
@Controller
@RequiredArgsConstructor
public class CardInfoController {

    private final CardService cardService;

    //@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')") // 두 가지 권한 중 하나라도 있으면 허용 (OR)
    //@PreAuthorize("hasRole('ADMIN') and hasRole('SUPERUSER')") // 두 가지 권한을 모두 가지고 있어야 허용 (AND)
    @GetMapping("/card/detail")
    public ResponseEntity<?> getCardInfoDetail(@RequestParam("cardNo") String cardNo, Model model, HttpServletRequest request) {
        return cardService.getCardInfoDetail(cardNo, model, request);
    }

    @GetMapping("/card/edit")
    public ResponseEntity<?> getCardInfoEdit(@RequestParam("cardNo") String cardNo, Model model) {
        return cardService.getCardInfoEdit(cardNo, model);
    }

    // 명함입력등록
    @PostMapping("/RegCardInfo")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> RegCardInfo(Model model, @RequestBody CardInfo cardInfo, HttpServletRequest request) {
        // 응답을 저장할 HashMap 생성
        Map<String, Object> response = new HashMap<>();

        try {
            // 사용자 정보를 처리하는 로직 (예: DB 저장)
            System.out.println("cardInfo : " + cardInfo);

            if (cardInfo != null) {
                // 카드 정보가 정상적으로 저장되는지 확인 (가정: cardService.RegCardInfo()는 저장을 하고 성공 시 객체 반환)
                if (cardService.RegCardInfo(cardInfo, request) != null) {
                    // 성공 응답
                    response.put("status", true);
                    response.put("msg", "등록완료");
                } else {
                    // 실패 응답 (예: DB 저장 실패)
                    response.put("status", false);
                    response.put("msg", "등록실패");
                }
            } else {
                // cardInfo가 null인 경우 실패
                log.info("요청한 cardInfo : {}", cardInfo);
                response.put("status", false);
                response.put("msg", "잘못된 요청");
            }

        } catch (Exception e) {
            // 예외가 발생한 경우
            response.put("status", false);
            response.put("msg", "서버 오류 발생: " + e.getMessage());
        }

        // 응답을 ResponseEntity로 감싸서 반환
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 명함 사진 등록
    @ResponseBody
    @PostMapping("/PictureRegCardInfo")
    public ResponseEntity<Map<String, Object>> PictureRegCardInfo (@RequestParam(value = "userPictureFile", required = false) MultipartFile userPictureFile,
                                  @RequestParam(value = "cardPictureFile", required = false) MultipartFile cardPictureFile,
                                  @RequestParam("name") String name,
                                  @RequestParam("tel") String tel,
                                  HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        boolean status = cardService.PictureRegCardInfo(userPictureFile, cardPictureFile, name, tel, request);

        if (status) {
            // 성공 응답
            response.put("status", status);
            response.put("msg", "등록완료");
        } else {
            // 실패 응답 (예: DB 저장 실패)
            response.put("status", status);
            response.put("msg", "등록실패");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 명함 상세 수정 업데이트
    @ResponseBody
    @PostMapping("/EditDetailUpdateCardInfo")
    public ResponseEntity<Map<String, Object>> EditDetailUpdateCardInfo (@RequestParam(value = "userPictureFile", required = false) MultipartFile userPictureFile,
                                                                         @RequestParam(value = "cardPictureFile", required = false) MultipartFile cardPictureFile,
                                                                         @RequestParam(value = "initFilePicture", required = false, defaultValue = "0") String initFilePicture,
                                                                         @RequestParam("cardNo") String cardNo,
                                                                         @RequestParam("company") String company,
                                                                         @RequestParam("position") String position,
                                                                         @RequestParam("name") String name,
                                                                         @RequestParam("adr") String adr,
                                                                         @RequestParam("email") String email,
                                                                         @RequestParam("tel") String tel,
                                                                         @RequestParam("fax") String fax,
                                                                         @RequestParam("memo") String memo,
                                                                         HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        boolean status = cardService.EditDetailUpdateCardInfo(userPictureFile, cardPictureFile, initFilePicture, cardNo, company, position, name, adr, email, tel, fax, memo, request);

        if (status) {
            // 성공 응답
            response.put("status", status);
            response.put("msg", "카드정보 업데이트 완료");
        } else {
            // 실패 응답 (예: DB 저장 실패)
            response.put("status", status);
            response.put("msg", "카드정보 업데이트 실패");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // 명함 삭제
    @ResponseBody
    @PostMapping("/DelCardInfo")
    public ResponseEntity<Map<String, Object>> DelCardInfo (@RequestBody Map<String, Object> payload, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        boolean status = cardService.DelCardInfo(payload, request);

        if (status) {
            // 성공 응답
            response.put("status", status);
            response.put("msg", "삭제완료");
        } else {
            // 실패 응답 (예: DB 저장 실패)
            response.put("status", status);
            response.put("msg", "삭제오류");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
