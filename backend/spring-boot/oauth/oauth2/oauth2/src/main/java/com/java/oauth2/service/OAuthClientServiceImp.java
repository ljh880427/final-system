package com.java.oauth2.service;

import com.java.oauth2.entity.OAuthClient;
import com.java.oauth2.repository.OAuthClientRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OAuthClientServiceImp implements OAuthClientService {

  private final OAuthClientRepository oAuthClientRepository;
  private String msg = "Client not Found Exception: ";

  public RegisteredClient findById(String Id, HttpServletRequest request) {
    System.out.println("findById = " + Id);
    OAuthClient oAuthClient = oAuthClientRepository.findById(Integer.valueOf(Id))
            .orElseThrow(() -> new IllegalArgumentException(msg + Id));
    return loadClientByResult(oAuthClient, request);
  }

  public RegisteredClient findByClientId(String Id, HttpServletRequest request) {
    System.out.println("findByClientId = " + Id);
    OAuthClient oAuthClient = oAuthClientRepository.findByEmailAndUseYN(Id, 'Y')
            .orElseThrow(() -> new IllegalArgumentException(msg + Id));
    return loadClientByResult(oAuthClient, request);
  }

  public RegisteredClient loadClientByResult(OAuthClient oAuthClient, HttpServletRequest request) {

    System.out.println("oAuthClient - loadClientByResult : " + oAuthClient);

    // 머신 투 머신 등록
    return RegisteredClient
            .withId(String.valueOf(oAuthClient.getNo()))
            .clientName(oAuthClient.getName())
            .clientId(oAuthClient.getEmail())
            .clientSecret(oAuthClient.getPwd())
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)

            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)

            .redirectUri("https://l.0neteam.co.kr/callback/custom") // ✅ redirect URI 필

            .scope("read")
            //.scope("write")

            // 클라이언트 애플리케이션이 사용자에게 "권한 요청"을 할 때, 사용자 동의(consent)를 요구할지 여부를 설정 (true: 동의 화면을 띄움, false: 동의패스)
            .clientSettings(ClientSettings.builder()
                    .requireAuthorizationConsent(false)
                    .build())

            // 리플레쉬 토큰 셋팅 설정
            .tokenSettings(TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofMinutes(30))
                    .refreshTokenTimeToLive(Duration.ofDays(7))
                    .reuseRefreshTokens(true) // or false
                    .build())

            .build();
  }

}
