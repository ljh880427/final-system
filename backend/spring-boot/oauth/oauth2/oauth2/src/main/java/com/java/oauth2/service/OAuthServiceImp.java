package com.java.oauth2.service;

import com.java.oauth2.common.UserUtils;
import com.java.oauth2.common.Utils;
import com.java.oauth2.dto.CustomOAuth2User;
import com.java.oauth2.dto.FileDTO;
import com.java.oauth2.dto.FileResDTO;
import com.java.oauth2.dto.OauthReqDTO;
import com.java.oauth2.entity.CardInfo;
import com.java.oauth2.entity.OAuthClient;
import com.java.oauth2.repository.OAuthClientRepository;
import com.java.oauth2.repository.CardInfoRepository;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImp implements OAuthService {

  private final OAuthClientRepository oAuthClientRepository;
  private final CardInfoRepository cardInfoRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  private final JwtDecoder jwtDecoder;
  private final JWKSet jwkSet;

  private final FileService fileService;
  private final Utils utils;


  // application.properties에서 호스팅 도메인 정보를 가져옴
  @Value("${hosting.uri}")
  private String hostingUri;

  public ResponseEntity<?> getLoginInfo(@RequestParam(required = false) String searchKeyWord, HttpServletRequest request, Model model) {

    Map<String, Object> result = new HashMap<>();

    List<CardInfo> cardInfos = null;

    System.out.println("hostingUri : " + hostingUri);

    String userNo = utils.getUserNo(request);

    if(userNo != null && !userNo.trim().isEmpty()) {
      int UserNo = Integer.parseInt(userNo);
      OAuthClient oAuthClient = oAuthClientRepository.findByNoAndUseYN(UserNo, 'Y');

      // payload에서 cardNo 값을 추출 (숫자 형태로 변환)
      if(searchKeyWord != null && !searchKeyWord.isEmpty()){
        System.out.println("searchKeyWord : " + searchKeyWord);
        cardInfos = cardInfoRepository.findTop100ByUseYNAndNameContainingOrCompanyContainingOrPositionContainingOrderByNoDesc('Y', searchKeyWord, searchKeyWord, searchKeyWord);
      } else {
        cardInfos = cardInfoRepository.findTop100ByRegUserNoAndUseYNOrderByNoDesc(UserNo, 'Y');
      }

      if (oAuthClient != null) {
        System.out.println("******************** " + oAuthClient);
        //model.addAttribute("email", oAuthClient.getEmail());
        //model.addAttribute("name", oAuthClient.getName());
        result.put("no", oAuthClient.getNo());
        result.put("email", oAuthClient.getEmail());
        result.put("name", oAuthClient.getName());

        //메인 사용자 프로필 사진
        if (oAuthClient.getFileNo() > 0) {
          //model.addAttribute("PhotoNo", hostingUri + "/file/uri/" + oAuthClient.getFileNo());
          result.put("PhotoNo", "/file/uri/" + oAuthClient.getFileNo());
          System.out.println("oAuthClient.getFileNo() : " + oAuthClient.getFileNo());
        }

      }
    }


    HttpSession session = request.getSession();
    CustomOAuth2User social_userinfo = null;
    social_userinfo = UserUtils.getCustomOAuth2User(request);
    log.info("social_userinfo : {}", social_userinfo);

    // 소셜로그인 값이 있는경우
    if (social_userinfo != null) {

      //model.addAttribute("issuer", social_userinfo.getIssuer());
      //model.addAttribute("name", social_userinfo.getName());
      //model.addAttribute("email", social_userinfo.getEmail());
      result.put("no", social_userinfo.getId());
      result.put("issuer", social_userinfo.getIssuer());
      result.put("name", social_userinfo.getName());
      result.put("email", social_userinfo.getEmail());

      OAuthClient social_oAuthClient = oAuthClientRepository.findByNoAndUseYN(social_userinfo.getId(), 'Y');
      if (social_oAuthClient.getFileNo() > 0) {
        result.put("PhotoNo", "/file/uri/" + social_oAuthClient.getFileNo());
      }

      log.info("social result : {}", result);
      //return "main";
    }

    // 토큰 인증 실패시 로그아웃 처리
    if(userNo.equals("invaildToken")) {
      System.out.println("invaildToken");
      //return "redirect:" + hostingUri + "/oauth2/logout";
      result.put("hosturl", hostingUri);
      result.put("status", "logout");
    }

    System.out.println("userNo : " + userNo);

    if(cardInfos != null){
      System.out.println("cardInfos : " + cardInfos);
      //model.addAttribute("cardInfos", cardInfos);
      //model.addAttribute("cardPictureUri", hostingUri + "/file/uri/"); // 명함 프로필+명함사진
      result.put("cardInfos", cardInfos);
    }

    result.put("cardPictureUri", "/file/uri/"); // 명함 프로필+명함사진

    return ResponseEntity.ok(result);
  }

  public OAuthClient userInfo (HttpServletRequest request) {

    //쿠키 값 확인
    Cookie[] cookies = request.getCookies();
    OAuthClient oAuthClient = null;

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("access_token".equals(cookie.getName())) {

          List<JWK> jwks = jwkSet.getKeys();

          String token = cookie.getValue();

          System.out.println("userinfo token = " + token);

          try{

            // JwtDecoder를 사용하여 토큰 디코딩
            Jwt jwt = jwtDecoder.decode(token);

            // 🔹 디버깅 로그 출력 (토큰 클레임 및 만료 시간)
            System.out.println("Decoded JWT claims: " + jwt.getClaims());

            // "sub" 클레임 추출
            String userNo = (String) jwt.getClaims().get("userNo");
            String email = (String) jwt.getClaims().get("sub");
            String name = (String) jwt.getClaims().get("username");

            oAuthClient = OAuthClient.builder()
                    .no(Integer.parseInt(userNo))
                    .email(email)
                    .name(name)
                    .build();

          } catch (JwtException e) {
            // 토큰 처리 중 오류가 발생한 경우 로그아웃처리

          }

        }
      }

    }

    return oAuthClient;
  }

  public ResponseEntity<?> Signup (@RequestBody OauthReqDTO oauthReqDTO) {

    Map<String, Object> result = new HashMap<>();

    boolean status = false;

      try {

        System.out.println("oauthReqDTO = " + oauthReqDTO);

        OAuthClient oAuthClient = OAuthClient.builder()
                .name(oauthReqDTO.getName())
                .email(oauthReqDTO.getEmail())
                .issuer("LOCAL")
                .pwd(oauthReqDTO.getPwd())
                //.profilePictureUrl("http://localhost:9000")
                .useYN('Y')
                .build();

        oAuthClient.setPwd(passwordEncoder.encode(oAuthClient.getPwd())); // 암호화 처리
        System.out.println("OAuthService save ");
        System.out.println("oAuthClient = " + oAuthClient);

        OAuthClient savedClient = oAuthClientRepository.save(oAuthClient);
        if (savedClient == null) {
          System.out.println("저장 실패");
          status = false;

        }
        System.out.println("저장 성공: " + savedClient);
        status = true;
      } catch (Exception e) {
        System.out.println("예외 발생: " + e.getMessage());
        e.printStackTrace();
        status = false;
      }

    result.put("status", status);

    return ResponseEntity.ok(result);
  }

  public ResponseEntity<?> signIn (Model model, @RequestBody OauthReqDTO oauthReqDTO, HttpServletResponse response, HttpServletRequest request, HttpSession session) {

    session.setAttribute("client_secret_" + oauthReqDTO.getEmail(), oauthReqDTO.getPwd());

    Map<String, Object> result = new HashMap<>();

    boolean status = true;
    try {
      System.out.println("oauthReqDTO = " + oauthReqDTO);
      Map<String, String> resultMap = getToken(oauthReqDTO);
      String access_token = resultMap.get("access_token");

      System.out.println("access_token = " + access_token);

      Cookie cookie = new Cookie("access_token", access_token);

      cookie.setHttpOnly(true); // JavaScript에서 접근 불가
      //cookie.setSecure(true); // HTTPS에서만 전송
      cookie.setPath("/"); //
      cookie.setMaxAge(session.getMaxInactiveInterval());
      response.addCookie(cookie);


      Jwt jwt = jwtDecoder.decode(access_token);
      String userNo = (String) jwt.getClaims().get("userNo");

      OAuthClient oAuthClient = oAuthClientRepository.findByNoAndUseYN(Integer.parseInt(userNo), 'Y');

      boolean authlogin_status = utils.login_Authentication(oAuthClient, request); // 사용자 권한 ROLE_ 및 사용자 로그인 처리

      status = authlogin_status;

      /// ///////////////////////////////////////////////////////////////////////
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
        //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자가 로그인되어 있지 않습니다.");
        System.out.println("현재 사용자가 로그인되어 있지 않습니다.");
      }else {
        System.out.println("로그인된 상태입니다.");
      }

      if (authentication instanceof AnonymousAuthenticationToken) {
        System.out.println("익명 사용자입니다.");
      } else {
        String username = authentication.getName(); // 또는 getPrincipal().toString()
        System.out.println("로그인된 사용자: " + username);
      }
      /// /////////////////////////////////////////////////////////////////////////

//      model.addAttribute("cafeList", postRepository.findTop10ByMenuNoBoardNoType(1, Sort.by(Sort.Order.desc("no"))));
//      model.addAttribute("blogList", postRepository.findTop10ByMenuNoBoardNoType(2, Sort.by(Sort.Order.desc("no"))));

    } catch (Exception e) {
      status = false;
      log.info("status : {}", status);
      log.info("Exception occurred: {}", e);

    }

    result.put("status", status);

    return ResponseEntity.ok(result);
  }

  public Map<String, String> getToken(OauthReqDTO oauthReqDTO) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type","client_credentials");
    formData.add("client_id", oauthReqDTO.getEmail());
    formData.add("client_secret", oauthReqDTO.getPwd());
    //formData.add("scope", "openid profile");  // ✅ 스코프 추가
    System.out.println("getToken start");

    return RestClient.create().post()
            .uri(hostingUri + "/oauth2/token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(formData)
            .retrieve()
            .toEntity(Map.class)
            .getBody();
  }

  @Override
  public ResponseEntity<String> getCodeUrl(Map<String, String> body, HttpServletRequest request) {

    String clientId = body.get("email");
    String redirectUri = "https://l.0neteam.co.kr/callback/custom";
    String scope = "read";
    String state = "123";

    System.out.println("getCodeUrl Start");

    String userNo = utils.getUserNo(request);

    System.out.println("getCodeUrl userNo : " + userNo);

    String url = UriComponentsBuilder.fromHttpUrl("/oauth2/authorize") // hostingUri로 바꿔도 됨
            .queryParam("response_type", "code")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("scope", scope)
            .queryParam("state", state)
            .build()
            .toUriString();

    return ResponseEntity.ok(url);
  }

  public ResponseEntity<?> TokenFromAuthCode(String code, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

    System.out.println("TokenFromAuthCode START");

    Map<String, Object> result = new HashMap<>();

    boolean status = true;
    try {
      Map<String, String> resultMap = getTokenFromAuthCode(code, request, session);
      String access_token = resultMap.get("access_token");
      String refresh_token = resultMap.get("refresh_token");

      System.out.println("access_token = " + access_token);
      System.out.println("refresh_token = " + refresh_token);

      Cookie cookie_access_token = new Cookie("access_token", access_token);

      cookie_access_token.setHttpOnly(true); // JavaScript에서 접근 불가
      //cookie.setSecure(true); // HTTPS에서만 전송
      cookie_access_token.setPath("/"); //
      cookie_access_token.setMaxAge(session.getMaxInactiveInterval());
      response.addCookie(cookie_access_token);

      Cookie cookie_refresh_token = new Cookie("refresh_token", refresh_token);

      cookie_refresh_token.setHttpOnly(true); // JavaScript에서 접근 불가
      //cookie.setSecure(true); // HTTPS에서만 전송
      cookie_refresh_token.setPath("/"); //
      cookie_refresh_token.setMaxAge(session.getMaxInactiveInterval());
      response.addCookie(cookie_refresh_token);

    } catch (Exception e) {
      status = false;
      log.info("status : {}", status);
      log.info("Exception occurred: {}", e);

    }

    result.put("status", status);
    return ResponseEntity.ok(result);
  }

  public Map<String, String> getTokenFromAuthCode(String code, HttpServletRequest request, HttpSession session) {

    System.out.println("getTokenFromAuthCode START");

    System.out.println("authCode : " + code);

    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

    String redirectUri = "https://l.0neteam.co.kr/callback/custom";

    String userNo = utils.getUserNo(request);

    if(userNo != null){
      OAuthClient oAuthClient = oAuthClientRepository.findById(Integer.parseInt(userNo)).orElseThrow();

      String client_secret = (String) session.getAttribute("client_secret_" + oAuthClient.getEmail());

      System.out.println("getTokenFromAuthCode - oAuthClient : " + oAuthClient);

      formData.add("grant_type", "authorization_code");
      formData.add("code", code);
      formData.add("client_id", oAuthClient.getEmail());
      formData.add("client_secret", client_secret);
      formData.add("redirect_uri", redirectUri);

    }

    return RestClient.create().post()
            .uri(hostingUri + "/oauth2/token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(formData)
            .retrieve()
            .toEntity(Map.class)
            .getBody();
  }


  public ResponseEntity<?> RefreshToken(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

    System.out.println("RefreshToken START");

    Map<String, Object> result = new HashMap<>();

    boolean status = false;
    try {
      Map<String, String> resultMap = getRefreshAccessToken(request, session);
      String access_token = resultMap.get("access_token");
      String refresh_token = resultMap.get("refresh_token");

      System.out.println("new access_token = " + access_token);
      System.out.println("new refresh_token = " + refresh_token);

      Cookie cookie_access_token = new Cookie("access_token", access_token);

      cookie_access_token.setHttpOnly(true); // JavaScript에서 접근 불가
      //cookie.setSecure(true); // HTTPS에서만 전송
      cookie_access_token.setPath("/"); //
      cookie_access_token.setMaxAge(session.getMaxInactiveInterval());
      response.addCookie(cookie_access_token);

      Cookie cookie_refresh_token = new Cookie("refresh_token", refresh_token);

      cookie_refresh_token.setHttpOnly(true); // JavaScript에서 접근 불가
      //cookie.setSecure(true); // HTTPS에서만 전송
      cookie_refresh_token.setPath("/"); //
      cookie_refresh_token.setMaxAge(session.getMaxInactiveInterval());
      response.addCookie(cookie_refresh_token);

      status = true;

    } catch (Exception e) {
      status = false;
      log.info("status : {}", status);
      log.info("Exception occurred: {}", e);

    }

    result.put("status", status);
    return ResponseEntity.ok(result);
  }


  public Map<String, String> getRefreshAccessToken(HttpServletRequest request, HttpSession session) {

    System.out.println("getRefreshAccessToken START");

    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

    String userNo = utils.getUserNo(request);

    if(userNo != null) {
      OAuthClient oAuthClient = oAuthClientRepository.findById(Integer.parseInt(userNo)).orElseThrow();

      String client_secret = (String) session.getAttribute("client_secret_" + oAuthClient.getEmail());
      String refresh_token = utils.getRefreshToken(request);

      formData.add("grant_type", "refresh_token");
      formData.add("refresh_token", refresh_token);
      formData.add("client_id", oAuthClient.getEmail());
      formData.add("client_secret", client_secret); // 실제 secret 사용
    }

    return RestClient.create().post()
            .uri(hostingUri + "/oauth2/token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(formData)
            .retrieve()
            .toEntity(Map.class)
            .getBody();
  }

  public ResponseEntity<?> logout(Model model, HttpServletResponse response, HttpSession session) {

    Map<String, Object> result = new HashMap<>();

    System.out.println("logout test");

    // access_token 삭제
    ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "")
            .httpOnly(true)
            //.secure(true)  // 프로덕션에서는 활성화
            .path("/")
            .maxAge(0)
            .build();
    //헤더에 추가
    response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());


    // refresh_token 삭제
    ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            //.secure(true)
            .path("/")
            .maxAge(0)
            .build();
    //헤더에 추가
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());


    // JSESSIONID 삭제
    ResponseCookie jsessionIdCookie = ResponseCookie.from("JSESSIONID", "")
            .httpOnly(true)
            //.secure(true)
            .path("/")
            .maxAge(0)
            .build();
    //헤더에 추가
    response.addHeader(HttpHeaders.SET_COOKIE, jsessionIdCookie.toString());


    //소셜 로그인 세션 삭제
    if (session != null) {
      session.removeAttribute("SPRING_SECURITY_CONTEXT");
    }

//    model.addAttribute("cafeList", postRepository.findTop10ByMenuNoBoardNoType(1, Sort.by(Sort.Order.desc("no"))));
//    model.addAttribute("blogList", postRepository.findTop10ByMenuNoBoardNoType(2, Sort.by(Sort.Order.desc("no"))));

    result.put("status", true);

    return ResponseEntity.ok(result);
  }

  public ResponseEntity<?> UserInfoUpdate (@RequestParam(value = "file", required = false) MultipartFile file,
                                 @RequestParam("email") String email,
                                 @RequestParam("name") String name,
                                 @RequestParam("pwd") String pwd,
                                 HttpServletRequest request) {

    // 로그인 (Token에 담겨 있는 사용자 정보로 User 테이블 PK 값 가져오기)

    Map<String, Object> result = new HashMap<>();

    try {
      String userNo = utils.getUserNo(request);
      // 토큰 인증 실패시 로그아웃 처리
      if(userNo.equals("invaildToken")) { // userNo 값이 비어있으면 false 처리
        //return false;
        result.put("status", "logout");
      }

      HttpSession session = request.getSession();
      CustomOAuth2User social_userinfo = null;
      social_userinfo = UserUtils.getCustomOAuth2User(request);
      log.info("social_userinfo : {}", social_userinfo);

      // 소셜로그인 값이 있는경우
      if (social_userinfo != null) {
        userNo = String.valueOf(social_userinfo.getId());
      }

      OAuthClient oAuthClient = oAuthClientRepository.findById(Integer.parseInt(userNo)).orElseThrow();

      if(file != null) {
        FileResDTO fileResDTO = fileService.upload(file, Integer.parseInt(userNo));
        FileDTO fileDTO = fileResDTO.getFile();
        oAuthClient.setFileNo(fileDTO.getNo());
        System.out.println("fileDTO.getNo() : " + fileDTO.getNo());
      }

      oAuthClient.setName(name);
      oAuthClient.setPwd(passwordEncoder.encode(pwd)); // 암호화 처리

      oAuthClientRepository.save(oAuthClient);
    }
    catch ( Exception e ){
      System.out.println("Exception : " + e);
      //return false;
      result.put("status", false);
    }

    result.put("status", true);

    return ResponseEntity.ok(result);
  }


  public ResponseEntity<?> MyPageInfo(HttpServletRequest request, Model model) {

    Map<String, Object> result = new HashMap<>();

    System.out.println("Start MyPageInfo");

    String userNo = utils.getUserNo(request);
    // 토큰 인증 실패시 로그아웃 처리
    if(userNo.equals("invaildToken")) {
      System.out.println("invaildToken");
      //return "redirect:" + hostingUri + "/oauth2/logout";
      result.put("status", "logout");
    }

    HttpSession session = request.getSession();
    CustomOAuth2User social_userinfo = null;
    social_userinfo = UserUtils.getCustomOAuth2User(request);
    log.info("social_userinfo : {}", social_userinfo);

    // 소셜로그인 값이 있는경우
    if (social_userinfo != null) {
      userNo = String.valueOf(social_userinfo.getId());
    }

    OAuthClient oAuthClient = oAuthClientRepository.findById(Integer.parseInt(userNo)).orElseThrow();

    if (oAuthClient.getFileNo() > 0) {
      //model.addAttribute("PhotoNo", hostingUri + "/file/uri/" + oAuthClient.getFileNo());
      result.put("PhotoNo", "/file/uri/" + oAuthClient.getFileNo());
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    String regDateStr = oAuthClient.getRegDate().format((formatter));
    oAuthClient.setParsRegDate(regDateStr);

    //model.addAttribute("email", oAuthClient.getEmail());
    //model.addAttribute("oAuthClient", oAuthClient);
    result.put("email", oAuthClient.getEmail());
    result.put("oAuthClient", oAuthClient);
    result.put("status", true);


    return ResponseEntity.ok(result);
  }

  public ResponseEntity<?> MyPageEdit(HttpServletRequest request, Model model) {

    Map<String, Object> result = new HashMap<>();

    System.out.println("Start MyPageEdit");

    String userNo = utils.getUserNo(request);
    // 토큰 인증 실패시 로그아웃 처리
    if(userNo.equals("invaildToken")) {
      System.out.println("invaildToken");
      //return "redirect:" + hostingUri + "/oauth2/logout";
      result.put("status", "logout");
    }

    HttpSession session = request.getSession();
    CustomOAuth2User social_userinfo = null;
    social_userinfo = UserUtils.getCustomOAuth2User(request);
    log.info("social_userinfo : {}", social_userinfo);

    // 소셜로그인 값이 있는경우
    if (social_userinfo != null) {
      userNo = String.valueOf(social_userinfo.getId());
    }

    OAuthClient oAuthClient = oAuthClientRepository.findById(Integer.parseInt(userNo)).orElseThrow();

    if (oAuthClient.getFileNo() != 0) {
      //model.addAttribute("PhotoNo", hostingUri + "/file/uri/" + oAuthClient.getFileNo());
      result.put("PhotoNo", "/file/uri/" + oAuthClient.getFileNo());

    }
    //model.addAttribute("email", oAuthClient.getEmail());
    //model.addAttribute("oAuthClient", oAuthClient);
    result.put("email", oAuthClient.getEmail());
    result.put("oAuthClient", oAuthClient);

    return ResponseEntity.ok(result);
  }

}
