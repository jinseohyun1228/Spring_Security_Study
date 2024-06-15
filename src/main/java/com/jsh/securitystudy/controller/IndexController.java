package com.jsh.securitystudy.controller;

import com.jsh.securitystudy.config.auth.PrincipalDetails;
import com.jsh.securitystudy.model.User;
import com.jsh.securitystudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    //일반로그인
    @GetMapping("/test/login")
    public @ResponseBody String testLogin (Authentication authentication , @AuthenticationPrincipal PrincipalDetails userDetails) {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        //authentication.getPrincipal()의 반환값은 Object, 따라서 (PrincipalDetails)으로 다운캐스팅!!
        System.out.println("authentication.getPrincipal() = " + authentication.getPrincipal());


        //getUser을 찾는 두 가지 방법 ^__^
        System.out.println("principalDetails.getUser() = " + principalDetails.getUser());
        /*
        * 그런데 일반 로그인이 아니라, 구글 Oauth 로그인을 한 경우, 이 부분이 오류가 난다.
        * 구글 로그인 시에 @AuthenticationPrincipal 어노테이션으로 받는 타입이, PrincipalDetails이 아닌, OAuth2User타입이기 때문이다.
        */


        //@AuthenticationPrincipal은 어떤 타입을 가지냐?? -> 바로 PrincipalDetails!! 따라서 바로 PrincipalDetails 타입으로 받을 수 있다.
        // 변경전 @AuthenticationPrincipal UserDetails userDetails
        // 변경후 @AuthenticationPrincipal PrincipalDetails userDetails
        System.out.println("userDetails.getUser() = " + userDetails.getUser());
        return "세션정보 확인하기";
    }

    /*
    Method Argument Resolver- AuthenticationPrincipalArgumentResolver:
        스프링 MVC에서 메서드 파라미터에 @AuthenticationPrincipal 어노테이션이 붙어 있으면, 이 리졸버가 호출되어 현재 인증된 사용자의 정보를 주입합니다.
    */

    @GetMapping("/test/oauth/login")
    /*
    구글 로그인을 하면 주입되는 객체가 OAuth2User타입 객체
     */
    public @ResponseBody String testOAuthLogin(Authentication authentication , @AuthenticationPrincipal OAuth2User oAuth) { //의존성 주입 DI
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // 구글 로그인해서 (OAuth2User)로 다운캐스팅~!
        System.out.println("==================================== ");
        System.out.println("authentication.getPrincipal() = " + authentication.getPrincipal());
        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());
        System.out.println("oAuth.getAttributes() = " + oAuth.getAttributes());

        System.out.println("==================================== ");
        System.out.println("==================================== ");
        System.out.println("oAuth2User.getAttribute(\"sub\") = " + oAuth2User.getAttribute("sub"));
        System.out.println("oAuth2User.getAttribute(\"name\") = " + oAuth2User.getAttribute("name"));
        System.out.println("==================================== ");
        System.out.println("==================================== ");
        return "OAuth 세션정보 확인하기";
        /*
        * oAuth.getAttributes()의 타입  Map<String, Object> =>
         */
    }

    /*
    * 스프링 시큐리티
    * 세션에 들어갈 수 있는 타입은 ?? <- 바로 Authentication 객체ㅠㅗㅓ
    * 요 안에 넣을 수 있고 필요할 떄마다 컨트롤러에서 DI를 통해 가져올 수 있다.
    * Authentication 에 들어갈 수 있는 타입은? UserDetails혹은 OAuth2USer 타입 (이 두개가 들어갔다는 것은 로그인이 완료 된것
    *
    * 그런데 필요할때마다 꺼내쓰는 건 좋은데 불편함이 있는겨~!~!~!
    *그래서 해결해야하는 겨
    * OAuth2USer USerDetails implements 해서 PrincipalDetails을 만들자. 타입어쩌구저쩌구
    *
    * 겱국 로그인 타입에 따라서 Authentication에 들어가는 타입이 다르다는 것을 기억하자~!
    */

    /*
    * 회원가입을 위해서느 User 오브젝트
    * 이 User 오브젝트와 UserDetails를 연결시켜주는 PrincipalDetails
    */




    @GetMapping({"/",""})
    public @ResponseBody String index() {
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return "principalDetails.getUser() = " + principalDetails.getUser();
    }


    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public @ResponseBody String join(User user) {
        System.out.println("user = " + user);
        user.setRole("USER");
        String rawPassword = user.getPassword();
        String encPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encPassword); //이거 너무 안전하지 않은 거 아녀?
        userRepository.save(user);
        return "redirect:loginForm"; //이게 뭐여
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }


    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_MANAGER')")
    //여러개 가능
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "개인정보";
    }
}
