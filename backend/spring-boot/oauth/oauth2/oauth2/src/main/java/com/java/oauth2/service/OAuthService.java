package com.java.oauth2.service;

import com.java.oauth2.dto.OauthReqDTO;
import com.java.oauth2.entity.OAuthClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface OAuthService {

    public ResponseEntity<?> getLoginInfo(@RequestParam(required = false) String searchKeyWord, HttpServletRequest request, Model model);

    public OAuthClient userInfo (HttpServletRequest request);

    public ResponseEntity<?> Signup (@RequestBody OauthReqDTO oauthReqDTO);

    public ResponseEntity<?> signIn (Model model, @RequestBody OauthReqDTO oauthReqDTO, HttpServletResponse response, HttpSession session);

    public Map<String, String> getToken(OauthReqDTO oauthReqDTO);

    public ResponseEntity<?> logout(Model model, HttpServletResponse response);

    public ResponseEntity<?> MyPageInfo(HttpServletRequest request, Model model);
    public ResponseEntity<?> MyPageEdit(HttpServletRequest request, Model model);

    public ResponseEntity<?> UserInfoUpdate (@RequestParam(value = "file", required = false) MultipartFile file,
                                             @RequestParam("email") String email,
                                             @RequestParam("name") String name,
                                             @RequestParam("pwd") String pwd,
                                             HttpServletRequest request);
}
