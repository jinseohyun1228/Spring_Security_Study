package com.jsh.securitystudy.config.oauth;

import com.jsh.securitystudy.config.auth.PrincipalDetails;
import com.jsh.securitystudy.model.User;
import com.jsh.securitystudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PrincipalOauth2UserService(@Lazy BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /*
     * 사용자가 구글 로그인 버튼을 클릭하고 구글 로그인 창이 떠서 로그인이 완료되면, 코드를 전달받는다.
     * OAuth Client 라이브러리가 코드를 받아준다.
     * 코드를 이용해서 Access Token 을 요청한다.
     *
     *--> 여기까지가 UserRequest
     *
     * 이제부터는 lodeUser()를 호출해서 회원 프로필을 받아야한다. (구글로부터!)
     *
     * 즉, lodeUser()는 구글로 부터 회원 프로필을 받아주는 메서드
     */
    @Override // 구글로부터 받은 UserRequest 데이터에 대한 후처리되는 함수
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //① userRequest 를 통해 OAuth2User를 만들어준다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());
        //이 정보를 사용해 회원 가입을 진행한다.


        // [회원가입 진행]
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getAttribute("sub"); //
        String username = oAuth2User.getAttribute("name") + "_" +  providerId; //
        String email = oAuth2User.getAttribute("email"); //
        String password = bCryptPasswordEncoder.encode("비밀번호");
        String role = "ROLE_USER";

        //이미 존재하는 회원인지 검사
        User userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            //회원가입 진행하기
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity,oAuth2User.getAttributes());
        // 왜 이 코드가 가능한지 생각해보자
        // -> 이 리턴 타입은 무엇을 들고 있는가 User 타입과 oAuth2User을 둘다 가지고 있다.
        /*
        * public PrincipalDetails(User user,Map<String, Object> attrilbutes ) {
        *    this.user = user;
        *   this.attrilbutes = attrilbutes;
        *  }
        */

    }
}
