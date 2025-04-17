package com.java.oauth2.common;

import com.java.oauth2.entity.OAuthClient;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Utils {

    private final JwtDecoder jwtDecoder;

    private final JWKSet jwkSet;

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public String getUserNo (HttpServletRequest request) {

        String userNo = "";

        //쿠키 값 확인
        Cookie[] cookies = request.getCookies();

        log.info("getUserNo token get start");
        log.info("COOKIES : {}", cookies);
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("COOKIE : {}", cookie);
                if ("access_token".equals(cookie.getName())) {
                    List<JWK> jwks = jwkSet.getKeys();
                    String token = cookie.getValue();
                    System.out.println("get UserNo token = " + token);

                    try{

                        // JwtDecoder를 사용하여 토큰 디코딩
                        Jwt jwt = jwtDecoder.decode(token);
                        // 🔹 디버깅 로그 출력 (토큰 클레임 및 만료 시간)
                        System.out.println("Decoded JWT claims: " + jwt.getClaims());
                        userNo = (String) jwt.getClaims().get("userNo");
                        System.out.println("controller userNo : " + userNo);


                    } catch (JwtException e) {
                        // 토큰 처리 중 오류가 발생한 경우 로그아웃처리
                        return "invaildToken";
                    }

                }
            }
        }

        return userNo;
    }


    public String getRefreshToken (HttpServletRequest request) {

        String refresh_token = "";

        //쿠키 값 확인
        Cookie[] cookies = request.getCookies();

        log.info("refresh token get start");
        log.info("COOKIES : {}", cookies);
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("COOKIE : {}", cookie);
                if ("refresh_token".equals(cookie.getName())) {
                    List<JWK> jwks = jwkSet.getKeys();
                    refresh_token = cookie.getValue();
                    System.out.println("get cookie refresh token = " + refresh_token);

                }
            }
        }

        return refresh_token;
    }


    public boolean login_Authentication(OAuthClient oAuthClient,
                                        HttpServletRequest request) {

        boolean status = false;

        boolean startYN = false;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("로그인전 - Auth 처리 시작.");
            startYN = true;
        }else {
            System.out.println("로그인된상태 - Auth 처리 Pass.");
            status = true;
        }

        if(startYN) {

            try {

                // 1. 권한 정보 구성
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                // 2. 사용자 정보 객체 만들기
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        oAuthClient.getEmail(),
                        oAuthClient.getPwd(),
                        authorities
                );

                // 3. 인증 객체 생성
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                // 4. SecurityContext에 등록
                SecurityContextHolder.getContext().setAuthentication(auth);

                // 세션에 저장 (중요!)
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(auth);
                HttpSession session = request.getSession(true);
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

                status = true;

            }catch(Exception e) {
                e.printStackTrace();
                status = false;
            }

        }

        return status;
    }


    public boolean Update_login_Authentication( HttpServletRequest request, String access_token) {

        try {
            // 기존 인증 정보
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            if (currentAuth == null) {
                System.out.println("❌ 현재 인증 정보가 없습니다. 업데이트 중단");
                return false;
            }

            // 기존 principal, credentials 유지
            Object principal = currentAuth.getPrincipal();
            Object credentials = currentAuth.getCredentials();

            Collection<GrantedAuthority> combinedAuthorities = new ArrayList<>();
            combinedAuthorities.addAll(currentAuth.getAuthorities()); // 기존 세션 권한목록

            Jwt jwt = jwtDecoder.decode(access_token); // jwt scope 권한 추가

            Collection<? extends GrantedAuthority> jwtAuthorities =
                    jwtAuthenticationConverter.convert(jwt).getAuthorities();
            combinedAuthorities.addAll(jwtAuthorities); // ✅ scope 권한 추가

            // 새 권한 목록 설정 (예: SCOPE_read 추가)
            List<GrantedAuthority> newAuthorities = new ArrayList<>(combinedAuthorities);

            // 새 인증 객체 생성
            Authentication updatedAuth = new UsernamePasswordAuthenticationToken(principal, credentials, newAuthorities);

            // SecurityContext 교체
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(updatedAuth);
            SecurityContextHolder.setContext(context);

            // 세션에도 반영
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            System.out.println("✅ 인증 권한이 업데이트됨: " + newAuthorities);

        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
