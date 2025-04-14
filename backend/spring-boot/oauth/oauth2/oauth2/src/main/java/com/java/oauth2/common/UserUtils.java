package com.java.oauth2.common;

import com.java.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public class UserUtils {

    public static CustomOAuth2User getCustomOAuth2User(HttpServletRequest request) {

        HttpSession session = request.getSession();

        // 세션에서 SecurityContextImpl 객체를 가져옵니다.
        // 세션에서 값 가져오기 (예: "SPRING_SECURITY_CONTEXT"라는 이름으로 저장된 값 : ex = SecurityContextImpl [Authentication=OAuth2AuthenticationToken [Principal=CustomOAuth2User(issuer=naver, name=이지현, id=4), Credentials=[PROTECTED], Authenticated=true, Details=WebAuthenticationDetails [RemoteIpAddress=211.235.90.45, SessionId=B8B85BFC1B16C201DD6229D924FEC5EC], Granted Authorities=[]]])
        Object securityContextObject = session.getAttribute("SPRING_SECURITY_CONTEXT");

        // SecurityContextImpl로 변환
        if (securityContextObject instanceof SecurityContext) {
            SecurityContext securityContext = (SecurityContext) securityContextObject;
            Authentication authentication = securityContext.getAuthentication();

            // 인증 정보가 OAuth2AuthenticationToken 타입인지 확인
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
                Object principal = token.getPrincipal();

                // principal이 CustomOAuth2User인지 확인
                if (principal instanceof CustomOAuth2User) {
                    return (CustomOAuth2User) principal;
                }
            }
        }

        return null; // Authentication이 OAuth2AuthenticationToken이 아니거나, CustomOAuth2User가 아니면 null 반환
    }
}
