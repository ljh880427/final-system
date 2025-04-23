package com.java.oauth2.service;

import com.java.oauth2.entity.CardInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CardService {

    public CardInfo RegCardInfo(@RequestBody CardInfo cardInfo, HttpServletRequest request);

    public boolean PictureRegCardInfo(@RequestParam(value = "userPictureFile", required = false) MultipartFile userPictureFile,
                                      @RequestParam(value = "cardPictureFile", required = false) MultipartFile cardPictureFile,
                                      @RequestParam("name") String name,
                                      @RequestParam("tel") String tel,
                                      HttpServletRequest request);

    public boolean EditDetailUpdateCardInfo (@RequestParam(value = "userPictureFile", required = false) MultipartFile userPictureFile,
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
                                                                         HttpServletRequest request);


    public boolean DelCardInfo(@RequestBody Map<String, Object> payload, HttpServletRequest request);


    public ResponseEntity<?> getCardInfoDetail(@RequestParam("cardNo") String cardNo, Model model, HttpServletRequest request);

    public ResponseEntity<?> getCardInfoEdit(@RequestParam("cardNo") String cardNo, Model model);

}
