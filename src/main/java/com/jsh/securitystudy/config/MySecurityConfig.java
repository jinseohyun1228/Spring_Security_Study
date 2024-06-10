package com.jsh.securitystudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
//secured 어노테이션 활성화
public class MySecurityConfig {

    @Bean
        //빈으로 등록하기
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests((authorizeRequests) -> authorizeRequests
                                .requestMatchers("/user/**").authenticated()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANGER"))
                .formLogin(formLogin -> formLogin
                        .loginPage("/loginForm") //-> 사용자 정의 로그인 페이지
                        .loginProcessingUrl("/login") //이 주소가 호출 되면 시큐리티가 낚아채서 로그인 ㄱㄱ
                        .defaultSuccessUrl("/")) // -> 로그인 성공 후 이동 페이지
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/loginForm")
                        .defaultSuccessUrl("/")
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(null)));

        return http.build();

    }



    @Bean
    public BCryptPasswordEncoder encoderPWD() {
        return new BCryptPasswordEncoder();
    }

}