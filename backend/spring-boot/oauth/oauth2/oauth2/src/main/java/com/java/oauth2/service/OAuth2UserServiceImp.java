package com.java.oauth2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.oauth2.dto.CustomOAuth2User;
import com.java.oauth2.entity.OAuthClient;
import com.java.oauth2.repository.OAuthClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j  // 로깅을 위한 어노테이션
@Service  // 스프링 서비스 컴포넌트로 등록
@RequiredArgsConstructor  // final 필드에 대한 생성자를 자동 생성하는 Lombok 어노테이션
public class OAuth2UserServiceImp extends DefaultOAuth2UserService {

  private final OAuthClientRepository oauthClientRepository;  // OAuth2 클라이언트 정보를 저장하는 리포지토리

  // 사용자 정보 로딩 메서드
  public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
    // 기본적인 사용자 정보 로딩
    OAuth2User oAuth2User = super.loadUser(request);

    // 클라이언트 이름(예: 네이버, 카카오) 가져오기
    String oAuthClientName = request.getClientRegistration().getClientName();

    try {
      // OAuth2User의 속성 정보 로그로 출력 (디버깅 용)
      log.info("{} : {}", oAuthClientName, new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
    } catch (Exception e) {
      e.printStackTrace();  // 예외 발생 시 스택 트레이스 출력
    }

    // OAuth2User에서 받아올 정보들 선언
    String oauthId = null;
    String name = null;
    String email = null;

    // 네이버 OAuth2 응답 처리
    if(oAuthClientName.equals("naver")) {
      // 네이버에서 반환한 데이터에서 'response' 속성의 맵을 가져옴
      Map<String, String> responseMap = (Map) oAuth2User.getAttributes().get("response");
      oauthId = responseMap.get("id");
      email = responseMap.get("email");
      name = responseMap.get("name");
    }

    // 카카오 OAuth2 응답 처리
    if(oAuthClientName.equals("kakao")) {
      // 카카오에서 반환한 'id'와 'kakao_account' 속성을 처리
      oauthId = oAuth2User.getAttributes().get("id").toString();
      Map<String, Object> kakao_account = (Map) oAuth2User.getAttributes().get("kakao_account");
      if(kakao_account != null) {
        log.info("kakao_account : {}", kakao_account);

        // 이메일 정보가 존재하면 추출
        Object oEmail = kakao_account.get("email");
        if(oEmail != null) {
          email = oEmail.toString();
        }
        log.info("email : {}", email);

        // 프로필 정보에서 이름을 추출
        Map<String, Object> profile = (Map) kakao_account.get("profile");
        if(profile != null) {
          log.info("profile : {}", profile);
          name = profile.get("nickname").toString();
        }
      }
    }

    // 기존 회원인지 확인 후, 신규 회원이면 DB에 추가
    OAuthClient oauthClient = oauthClientRepository.findByOauthId(oauthId);

    // 기존에 해당 oauthId로 회원 정보가 없으면 신규 회원 처리
    if(oauthClient == null) {
      // 신규 회원 정보 생성
      oauthClient = OAuthClient.builder()
              .name(name)
              .email(email)
              .issuer(oAuthClientName)  // 인증 제공자 (네이버, 카카오 등)
              .oauthId(oauthId)
              .useYN('Y')  // 사용 여부
              .build();

      // DB에 저장
      oauthClient = oauthClientRepository.save(oauthClient);

      // 저장 후 ID가 정상적으로 생성되면 추가 작업 가능
      if(oauthClient.getNo() > 0) {
        // 정상 로직 추가 (예: 회원가입 후 추가 작업)
      }
    }

    // 최종적으로 CustomOAuth2User 객체를 생성하여 반환
    return CustomOAuth2User.builder()
            .issuer(oAuthClientName)  // 인증 제공자 이름
            .name(name)  // 사용자 이름
            .id(oauthClient.getNo())  // DB에서 저장된 사용자 ID
            .email(email)
            .build();
  }

}
