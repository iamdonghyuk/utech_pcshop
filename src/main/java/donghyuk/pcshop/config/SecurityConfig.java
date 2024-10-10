package donghyuk.pcshop.config;

import donghyuk.pcshop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// 인증 관련 설정 클래스
@Configuration          // 구성 클래스 등록
@EnableWebSecurity      // 스프링 시큐리트 활성화, 웹 보안 설정 사용
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    MemberService memberService;        // MemberService

    // 로그인/로그아웃 및 인증 관련 설정 함수 Override
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // /, /member/, /item/, /images/는 모두 허용,
        // /admin은 admin만 허용
        // 그외에는 인증된 (로그인한) 사용자만 허용
        http.authorizeRequests()
                .mvcMatchers("/", "/member/**", "/item/**", "/images/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();

        // 인증 예외가 발생했을 때 처리부분 (로그인 페이지로 이동시킨다!)
        http.exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/member/login");
                });

        // 로그인 설정
        http.formLogin()
                .loginPage("/member/login")         // 로그인 페이지
                .defaultSuccessUrl("/")             // 로그인 성공시 URL
                .usernameParameter("email")         // username은 email
                .failureUrl("/member/login/fail")   // 로그인 실패시 URL
                .and()
                .logout()                           // 로그아웃 설정
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout")) // 로그아웃 페이지
                .logoutSuccessUrl("/"); // 로그아웃 성공시 URL
    }

    // css, js, img 폴더들은 인증에서 제외.
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }

    // 암호 인코딩
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
    }

    // 비밀번호 암호화하는 스프링 시큐리티 클래스 사용.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

