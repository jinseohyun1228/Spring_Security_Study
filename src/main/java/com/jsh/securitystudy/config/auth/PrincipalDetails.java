package com.jsh.securitystudy.config.auth;

import com.jsh.securitystudy.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/*
시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
로그인 진행이 완료가 되면?? session을 만들어준다.
=> Security ContextHoder

이 Session에 들어갈 수 있는 타입이 정해져있다. 바로바로 ~!~!~!
    => Authentication 타입 객체
        => 이 객체 안에는 user정보를 담기위한 또 타입이 정해져있다..
            => 그게 바로 UserDetails 타입 객체

Security Session ->  Authentication -> UserDetails
 */
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    //여기임
    private User user;
    private Map<String, Object> attrilbutes;


    //일반 로그인
    public PrincipalDetails(User user) {
        this.user = user;
    }

    //OAuth 로그인 사용
    public PrincipalDetails(User user,Map<String, Object> attrilbutes ) {
        this.user = user;
        this.attrilbutes = attrilbutes;
    }
    /*
    * 이때 어떻게 user를 전달받을까?
    */

    //OAuth2User 확장으로 인해 추가한 메서드
    @Override
    public Map<String, Object> getAttributes() {
        return attrilbutes;
    }

    //해당 유저의 권한을 리턴하는 곳!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        user.getRole();
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;

    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        //휴면 계정 사용할 때...
        return true;
    }

    //OAuth2User 확장으로 인해 추가한 메서드
    @Override
    public String getName() {
        return null;
        //중요X
    }
}