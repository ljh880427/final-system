package com.java.oauth2.common;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Utils {

    private final JwtDecoder jwtDecoder;

    private final JWKSet jwkSet;

    public String getUserNo (HttpServletRequest request) {

        String userNo = "";

        //쿠키 값 확인
        Cookie[] cookies = request.getCookies();

        log.info("test info");
        log.info("COOKIES : {}", cookies);
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("COOKIE : {}", cookie);
                if ("access_token".equals(cookie.getName())) {
                    List<JWK> jwks = jwkSet.getKeys();
                    String token = cookie.getValue();
                    System.out.println("token = " + token);

                    try{

                        // JwtDecoder를 사용하여 토큰 디코딩
                        Jwt jwt = jwtDecoder.decode(token);
                        // 🔹 디버깅 로그 출력 (토큰 클레임 및 만료 시간)
                        System.out.println("Cafe Decoded JWT claims: " + jwt.getClaims());
                        userNo = (String) jwt.getClaims().get("userNo");
                        System.out.println("Cafe controller userNo : " + userNo);


                    } catch (JwtException e) {
                        // 토큰 처리 중 오류가 발생한 경우 로그아웃처리
                        return "invaildToken";
                    }

                }
            }
        }

        return userNo;
    }

}
