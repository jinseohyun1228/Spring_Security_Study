package com.jsh.securitystudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//@EnableWebSecurity //활성화 -> 스프링 시큐리티 필터가 스프링 필터페인에 등록이 된다.
//public class SecurityConfig extends WebSecurityConfigurerAdapter{
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf.disable();
/*
    http.csrf.disable();이란??

    * csrf : 공격자가 안전한 사용자의 요청을 바꾸는 공격
    * csrf.disable() : 스프링 시큐리티가 csrf에대한 보호를 비활성화하는 것
    ?? 왜 -> 이 사이트가 REST API 서비스니까 (자세한 내용은 다음에.. 총총총)

    ! 이제는 : -> http.csrf(AbstractHttpConfigurer::disable)
 */
//        http.authorizeRequests() -> : HTTP 요청에 대한 인증 요구 사항을 설정하기 위한 메서드
//                .antMatchers("/user/**").authenticated() -> 인증이 된 사용자에게 허용
//                .antMatchers("/manager/**").access("접근제어표현식") -> 접근제어표현식에 따라 접근 허용
//                .antMatchers("/user/**").authenticated("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANGER')")
//                .antMatchers("/admin/**").authenticated("hasRole('ROLE_ADMIN')")
//                .anyRequest().permitAll() -> 이외의 요청은 권한이 없더라도 모두 허용해줌
//                .and()
//                .formLogin() //로그인 방식을 사용하는 것으로 설정
//                .loginPage("/login"); //직접 설정한 로그인 페이지로 로그인페이지를 설정
//    }
//}

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
                        .defaultSuccessUrl("/")); // -> 로그인 성공 후 이동 페이지



        return http.build();


//        http.csrf().disable();
//        http.authorizeRequests()
//                .antMatchers("/user/**").authenticated()
//                .antMatchers("/manager/**").access("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')")
//                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
//                .anyRequest().permitAll();
//
//        return http.build();

    }


    @Bean
    public BCryptPasswordEncoder encoderPWD() {
        return new BCryptPasswordEncoder();
    }

}