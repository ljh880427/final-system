package com.java.oauth2.service;

import com.java.oauth2.entity.OAuthClient;
import com.java.oauth2.repository.OAuthClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthClientServiceImp implements OAuthClientService {

  private final OAuthClientRepository oAuthClientRepository;
  private String msg = "Client not Found Exception: ";

  public RegisteredClient findById(String Id) {
    System.out.println("findById = " + Id);
    OAuthClient oAuthClient = oAuthClientRepository.findById(Integer.valueOf(Id))
            .orElseThrow(() -> new IllegalArgumentException(msg + Id));
    return loadClientByResult(oAuthClient);
  }

  public RegisteredClient findByClientId(String Id) {
    System.out.println("findByClientId = " + Id);
    OAuthClient oAuthClient = oAuthClientRepository.findByEmailAndUseYN(Id, 'Y')
            .orElseThrow(() -> new IllegalArgumentException(msg + Id));
    return loadClientByResult(oAuthClient);
  }

  public RegisteredClient loadClientByResult(OAuthClient oAuthClient) {

    return RegisteredClient
        .withId(String.valueOf(oAuthClient.getNo()))
        .clientName(oAuthClient.getName())
        .clientId(oAuthClient.getEmail())
        .clientSecret(oAuthClient.getPwd())
//            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
////        .redirectUri(oAuthClient.getRedirectUri())
////        .postLogoutRedirectUri(oAuthClient.getPostLogoutRedirectUri())
//                .scope(OidcScopes.OPENID)
//                .scope(OidcScopes.PROFILE)
        .scope("read")
        .scope("write")

        .clientSettings(ClientSettings.builder().build())
        .build();
  }

}
