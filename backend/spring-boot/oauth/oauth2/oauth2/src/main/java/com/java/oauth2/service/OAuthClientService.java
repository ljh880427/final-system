package com.java.oauth2.service;

import com.java.oauth2.entity.OAuthClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

public interface OAuthClientService {

    public RegisteredClient findById(String Id);

    public RegisteredClient findByClientId(String Id);

    public RegisteredClient loadClientByResult(OAuthClient oAuthClient);
}
