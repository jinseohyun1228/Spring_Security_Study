package com.jsh.securitystudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;


import com.jsh.securitystudy.config.oauth.PrincipalOauth2UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
//secured 어노테이션 활성화
public class MySecurityConfig {

    @Bean
    public BCryptPasswordEncoder encoderPWD() {
        return new BCryptPasswordEncoder();
    }

    private final PrincipalOauth2UserService principalOauth2UserService;

    public MySecurityConfig(@Lazy PrincipalOauth2UserService principalOauth2UserService) {
        this.principalOauth2UserService = principalOauth2UserService;
    }

    @Bean
        //빈으로 등록하기
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests((authorizeRequests) -> authorizeRequests
                                .requestMatchers("/user/**").authenticated()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER"))
                .formLogin(formLogin -> formLogin
                        .loginPage("/loginForm") //-> 사용자 정의 로그인 페이지
                        .loginProcessingUrl("/login") //이 주소가 호출 되면 시큐리티가 낚아채서 로그인 ㄱㄱ
                        .defaultSuccessUrl("/")) // -> 로그인 성공 후 이동 페이지
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/loginForm")
                        .defaultSuccessUrl("/")
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(principalOauth2UserService)));

        return http.build();

    }


    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration googleClientRegistration = ClientRegistration.withRegistrationId("google")
                .clientId("760459109498-1dpf66s2nefpk02uvg07479hnakltacs.apps.googleusercontent.com")
                .clientSecret("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")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8000/security/login/oauth2/code/google") //"http://localhost:8000/security/login/oauth2/code/google"
                .scope("profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .clientName("Google")
                .build();

        return new InMemoryClientRegistrationRepository(googleClientRegistration);
    }


}