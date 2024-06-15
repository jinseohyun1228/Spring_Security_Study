package com.jsh.securitystudy.config.auth;

import com.jsh.securitystudy.config.auth.PrincipalDetails;
import com.jsh.securitystudy.model.User;
import com.jsh.securitystudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
시큐리티 설정에서 loginProcessingUrl("/login");
-> login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어 있는 loadUserByUsername 실행~!!!
*/
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    //⭐PrincipalDetails를 반환해준다!!
    /*
    시큐리티 session = Authentication = UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username);
        System.out.println("username = " + username);
        System.out.println("userEntity = " + userEntity);

        if (userEntity != null) { //해당 유저가 존재할 때, 즉 로그인이 가능할 때
            return new PrincipalDetails(userEntity);
        }
        else {
            return null;
        }
        /*
        무엇을 return 해주죠? ??
        => PrincipalDetails를요!! 이것은 곧??? UserDetails

        이 메서드는 어디로 리턴할까??
        바로 Authentication의 내부로 리턴되어서 들어간다.

        Authentication이건 session으로 또 가고!!

         */

    }
}
