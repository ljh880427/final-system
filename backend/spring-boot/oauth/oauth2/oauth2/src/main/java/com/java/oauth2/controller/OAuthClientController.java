package com.java.oauth2.controller;

import com.java.oauth2.dto.OauthReqDTO;
import com.java.oauth2.dto.CustomOAuth2User;
import com.java.oauth2.entity.CardInfo;
import com.java.oauth2.entity.OAuthClient;
import com.java.oauth2.service.CardServiceImp;
import com.java.oauth2.service.OAuthServiceImp;
import com.nimbusds.jose.jwk.JWKSet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = { "http://localhost:5178", "http://l.0neteam.co.kr:5178" }, allowCredentials = "true")
@RequestMapping("/api")
@Slf4j
@Controller
@RequiredArgsConstructor
public class OAuthClientController {

  private final OAuthServiceImp oAuthService;

  private final JwtDecoder jwtDecoder;
  private final JWKSet jwkSet;

  @GetMapping("")
  public ResponseEntity<?> home(@RequestParam(required = false) String searchKeyWord, @AuthenticationPrincipal CustomOAuth2User oAuth2User, Model model, HttpServletRequest request, HttpServletResponse response, @RequestHeader(value = "Authorization", defaultValue = "") String authorizationHeader) {
    return oAuthService.getLoginInfo(searchKeyWord, request, model);
  }

  @ResponseBody
  @GetMapping("/userinfo")
  public OAuthClient userinfo (HttpServletRequest request) {
	return oAuthService.userInfo(request);
  }

  @GetMapping("/signIn")
  public String signIn() {
    return "signIn";
  }

  @GetMapping("/signUp")
  public String signUp() {
    return "signUp";
  }

  @GetMapping("/MyPageInfo")
  public ResponseEntity<?> MyPageInfo(HttpServletRequest request, Model model) {
    return oAuthService.MyPageInfo(request,model);
  }

  @GetMapping("/MyPageEdit")
  public ResponseEntity<?> MyPageEdit(HttpServletRequest request, Model model) {
    return oAuthService.MyPageEdit(request,model);
  }

  @ResponseBody
  @PostMapping("/signUp")
  public ResponseEntity<?> signUp(@RequestBody OauthReqDTO oauthReqDTO) {
    return oAuthService.Signup(oauthReqDTO);
  }

  @ResponseBody
  @PostMapping("/signIn")
  public ResponseEntity<?> signIn(Model model, @RequestBody OauthReqDTO oauthReqDTO, HttpServletResponse response, HttpServletRequest request, HttpSession session) {
    return oAuthService.signIn(model, oauthReqDTO, response, request, session);
  }

  @PostMapping("/getCodeUrl")
  public ResponseEntity<String> getCodeUrl(@RequestBody Map<String, String> body, HttpServletRequest request) {
    return oAuthService.getCodeUrl(body, request);
  }

  @PostMapping("/TokenFromAuthCode")
  public ResponseEntity<?> TokenFromAuthCode(
          @RequestBody Map<String, String> body,
          HttpServletRequest request,
          HttpServletResponse response,
          HttpSession session) {

    String code = body.get("code");

    System.out.println("Controller TokenFromAuthCode Code : " + code);

    return oAuthService.TokenFromAuthCode(code, request, response, session);
  }

  @GetMapping("/RefreshToken")
  public ResponseEntity<?> RefreshToken(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
    return oAuthService.RefreshToken(request,response, session);
  }


  @GetMapping("/oauth2/logout")
  public ResponseEntity<?> logout(Model model, HttpServletResponse response, HttpSession session) {
    return oAuthService.logout(model,response,session);
  }

  @ResponseBody
  @PostMapping("/UserInfoUpdate")
  public ResponseEntity<?> UserInfoUpdate(@RequestParam(value = "file", required = false) MultipartFile file,
                                   @RequestParam("email") String email,
                                   @RequestParam("name") String name,
                                   @RequestParam("pwd") String pwd,
                                   HttpServletRequest request) {
    System.out.println("UserInfoUpdate start");
    
    return oAuthService.UserInfoUpdate(file,email,name,pwd,request);
  }



}
