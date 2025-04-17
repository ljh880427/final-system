package com.java.oauth2.service;

import com.java.oauth2.entity.OAuthClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

public interface OAuthClientService {

    public RegisteredClient findById(String Id, HttpServletRequest request);

    public RegisteredClient findByClientId(String Id, HttpServletRequest request);

    public RegisteredClient loadClientByResult(OAuthClient oAuthClient, HttpServletRequest request);
}
