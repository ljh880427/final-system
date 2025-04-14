package com.java.oauth2.config;

import com.java.oauth2.dto.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j  // 로그 출력을 위한 어노테이션
@Component  // 스프링 컴포넌트로 등록하기 위한 어노테이션
@RequiredArgsConstructor  // final 필드에 대해 생성자를 자동으로 생성하는 Lombok 어노테이션
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  // 인증 성공 후 실행되는 메서드
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    // 인증 정보를 SecurityContext에 설정
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 현재 세션을 가져옴
    HttpSession session = request.getSession();

    // 세션에 SPRING_SECURITY_CONTEXT를 설정하여 인증 정보를 저장
    session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

    // 인증된 사용자 정보를 CustomOAuth2User 타입으로 캐스팅
    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

    // 인증된 사용자 정보 로깅
    log.info("oAuth2User : {}", oAuth2User);

    // 응답의 문자 인코딩을 UTF-8로 설정
    response.setCharacterEncoding("UTF-8");

    // 응답의 콘텐츠 타입을 HTML로 설정
    response.setContentType("text/html;charset:UTF-8");

    // 사용자의 정보(JSON 형식) 응답으로 반환할 수 있도록 설정(현재는 주석 처리됨)
    // response.getWriter().append(new Gson().toJson(oAuth2User));

    // 인증 후 리디렉션할 URL로 이동
    response.sendRedirect("/");
  }

}
