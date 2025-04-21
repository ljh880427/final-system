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
import java.util.Arrays;
import java.util.List;

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

    // 예: OAuthClient에 getScopes()라는 메서드가 있고, List<String> 타입을 반환한다고 가정
    List<String> scopes = Arrays.asList("read", "write", "delete"); // 예: ["read", "write", "delete"]  oAuthClient.getScopes();

    RegisteredClient.Builder builder = RegisteredClient
            .withId(String.valueOf(oAuthClient.getNo()))
            .clientName(oAuthClient.getName())
            .clientId(oAuthClient.getEmail())
            .clientSecret(oAuthClient.getPwd())
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)

            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)

            .redirectUri("https://l.0neteam.co.kr/callback/custom")

            .clientSettings(ClientSettings.builder()
                    .requireAuthorizationConsent(false)
                    .build())

  //        .scope("read")    // 수동 scope 입력시
  //        .scope("write")

            .tokenSettings(TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofMinutes(30))
                    .refreshTokenTimeToLive(Duration.ofDays(7))
                    //.refreshTokenTimeToLive(Duration.ofMinutes(5))
                    .reuseRefreshTokens(true)
                    .build());

    // 리스트로 받은 스코프들 추가
    if (scopes != null) {
      for (String scope : scopes) {
        builder.scope(scope);
      }
    }

    return builder.build();
  }

}
