package com.java.oauth2.config;

import com.java.oauth2.service.OAuth2UserServiceImp;
import com.java.oauth2.service.OAuthClientServiceImp;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@EnableWebSecurity  // 웹 보안을 활성화하는 어노테이션
@Configuration  // 스프링 설정 클래스 선언
@RequiredArgsConstructor  // 생성자를 자동으로 생성하는 Lombok 어노테이션
public class AuthorizationConfig {

    private final OAuthClientServiceImp oAuthClientService;  // OAuth2 클라이언트 서비스

    private final OAuth2UserServiceImp oAuth2UserService;  // OAuth2 사용자 서비스
    private final OAuth2SuccessHandler oAuth2SuccessHandler;  // OAuth2 로그인 성공 후 처리 핸들러

    // application.properties에서 OAuth2 인증 관련 URL을 가져옴
    @Value("${oauth2.authorization-end-point}")
    private String authorization;

    @Value("${oauth2.redirection-end-point}")
    private String redirection;


    @Bean
    @Order(1)
    public SecurityFilterChain clientFilterChain(HttpSecurity http, AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        http.with(authorizationServerConfigurer, (authorizationServer) -> authorizationServer.oidc(Customizer.withDefaults()));
        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher());

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/**")
        );

        http.cors(Customizer.withDefaults());

        http
                .securityMatcher("/signIn", "/signUp", "/oauth2/**") // 이 패턴만 이 체인에서 처리
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/signIn", "/signUp", "/oauth2/**", "/api/**").permitAll() // 로그인 및 인증 관련 경로는 permitAll
                                .anyRequest().authenticated() // 나머지 URL 접근 막기
                )
                .oauth2Login(oauth2 -> {
                    oauth2.authorizationEndpoint(endpoint -> endpoint.baseUri(authorization));
                    oauth2.redirectionEndpoint(endpoint -> endpoint.baseUri(redirection));
                    oauth2.userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService));
                    oauth2.loginPage("/*").permitAll(); // 로그인 페이지는 permitAll
                    oauth2.successHandler(oAuth2SuccessHandler);
                });

        return http.build(); // 설정을 마친 SecurityFilterChain 반환
    }

    // SecurityFilterChain 설정 - HTTP 보안 필터 설정
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        // OAuth2 인증 서버 설정

        System.out.println("test filter");

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/**")
        );

        http.cors(Customizer.withDefaults());

        // HTTP 요청에 대한 권한 설정
        http.authorizeHttpRequests(r -> {
                    // 특정 URL에 대해 모두 접근 허용
                    r.requestMatchers("/").permitAll();
                    r.requestMatchers("/docs/**","/v3/**","/swagger-ui/**","/swagger-ui.html").permitAll();
                    r.requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll();
                    r.requestMatchers(HttpMethod.GET, "/signIn","/signUp","/userinfo","/MyPageInfo","/MyPageEdit","/file/**", "/card/detail/**", "/card/edit/**", "/api/**").permitAll();  // GET 요청 허용
                    r.requestMatchers(HttpMethod.POST, "/addClient", "/signUp", "/signIn", "/UserInfoUpdate","/file/**", "/RegCardInfo/**", "/PictureRegCardInfo/**", "/DelCardInfo/**", "/EditDetailUpdateCardInfo/**", "/api/**").permitAll(); // POST 요청 허용
                    r.anyRequest().authenticated();// 나머지 URL 접근 막기
                })
                // OAuth2 Resource Server 설정 (JWT 사용)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(new JwtAuthenticationConverter()) // JWT 인증 컨버터 설정 (선택사항)
                        )
                );

        return http.build(); // 설정을 마친 SecurityFilterChain 반환
    }


    // CORS 설정 (교차 출처 리소스 공유)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 허용할 출처 설정

        List<String> originUris = List.of(
                "http://127.0.0.1:8000",
                "http://localhost:8000",
                "http://localhost:9000",
                "http://l.0neteam.co.kr:9000"
        );
        originUris.forEach(config::addAllowedOrigin);
        config.addAllowedOriginPattern("*");  // 모든 출처 허용
        config.addAllowedHeader("*");  // 모든 헤더 허용
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));  // 허용할 HTTP 메서드 설정
        config.setAllowCredentials(true);  // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/static/**", config);  // static 경로에 대한 CORS 설정
        source.registerCorsConfiguration("/**", config);  // 나머지 경로에 대한 CORS 설정
        return source;
    }

    // 비밀번호 암호화에 사용될 BCryptPasswordEncoder 빈 정의
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // RegisteredClientRepository 빈 정의 (클라이언트 저장소)
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new RegisteredClientRepository() {
            @Override
            public void save(RegisteredClient registeredClient) {}
            @Override
            public RegisteredClient findById(String id) {
                System.out.println("AuthorizationConfig -  : " + id);
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                return oAuthClientService.findById(id, request);  // ID로 클라이언트 조회
            }
            @Override
            public RegisteredClient findByClientId(String email) {
                System.out.println("AuthorizationConfig - findByClientId : " + email);
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                return oAuthClientService.findByClientId(email, request);  // 이메일로 클라이언트 조회
            }
        };
    }

    // RSA 공개 키와 개인 키를 사용하여 JWT 서명 및 검증 설정
    private final RsaKeyProperties rsaKeys;

    @Bean
    public JWKSet jwkSet() {
        RSAKey.Builder builder = new RSAKey.Builder(rsaKeys.publicKey())
                .keyUse(KeyUse.SIGNATURE)  // 서명 용도로 사용
                .algorithm(JWSAlgorithm.RS256)  // 서명 알고리즘 설정
                .keyID("public-key-id");  // 키 ID 설정
        return new JWKSet(builder.build());  // JWKSet 반환
    }

    // JWT 인코더 설정
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);  // NimbusJwtEncoder 반환
    }

    // JWT 디코더 설정
    @Bean
    public JwtDecoder jwtDecoder() {
        // 공개 키를 사용하여 JWK를 생성
        RSAKey jwk = new RSAKey.Builder(rsaKeys.publicKey())
                .keyID("public-key-id")
                .build();

        // JWKSet 생성
        JWKSet jwkSet = new JWKSet(jwk);

        // JWKSource로 감싸기
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);

        // JWSKeySelector를 사용하여 JWTProcessor 설정
        JWSKeySelector<SecurityContext> jwsKeySelector = new JWSVerificationKeySelector<>(Set.of(JWSAlgorithm.RS256), jwkSource);
        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(jwsKeySelector);

        // JWTProcessor를 사용하여 NimbusJwtDecoder 반환
        return new NimbusJwtDecoder(jwtProcessor);
    }

    // JWT 토큰 사용자 정의 설정
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return (context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                RegisteredClient client = context.getRegisteredClient();
                JwtClaimsSet.Builder builder = context.getClaims();

                builder.issuer("Oauth2_Server");  // 발급자 설정
                //builder.expiresAt(Instant.now().plus(1, ChronoUnit.DAYS));  // 만료 시간 설정
                builder.expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES)); // access_token 30분

                builder.claims((claims) -> {
                    claims.put("scope", client.getScopes());  // 클라이언트의 스코프 정보 추가
                });
                                
                System.out.println("test ClientName : " + client.getClientName());
                System.out.println("test ClientID : " + client.getId());
                
                // 사용자 정보 추출
                

                // 사용자 정의 값 추가
                builder.claim("username", client.getClientName());  // 예시로 사용자 이름 추가
                builder.claim("userNo", client.getId());
            }
        });
    }

}
