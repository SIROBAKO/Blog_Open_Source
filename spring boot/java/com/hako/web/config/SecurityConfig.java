package com.hako.web.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.username}")
    private String username;

    @Value("${security.password}")
    private String password;

    @Value("${test.username}")
    private String test_username;

    @Value("${test.password}")
    private String test_password;
    
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
//            .antMatchers("/admin/**").authenticated()
                .antMatchers("/admin/edit").authenticated()
                .antMatchers("/admin/list").authenticated()
                .antMatchers("/admin/update").authenticated()
                .antMatchers("/admin/uploadSummernoteImageFile").authenticated()
                .antMatchers("/admin/deleteSummernoteImageFile").authenticated()
                .antMatchers("/admin/reg").hasRole("ADMIN") 
                .antMatchers("/admin/del_update").hasRole("ADMIN") 
                .antMatchers("/admin/updatePost").hasRole("ADMIN") 
                .antMatchers("/admin/appendSitemap").hasRole("ADMIN") 
                .anyRequest().permitAll() // 나머지 페이지에 대해 인증 없이 접근 가능
            .and()
            .formLogin()
                .defaultSuccessUrl("/admin/list") // 로그인 성공 시 이동할 URL
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/admin/logout") // 로그아웃 URL 지정
                .logoutSuccessUrl("/index") // 로그아웃 성공 시 이동할 URL 지정
                .invalidateHttpSession(true) // HTTP 세션 무효화
                .deleteCookies("JSESSIONID") // 로그아웃 시 쿠키 삭제
                .permitAll().and().exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    // 여기에 처리 코드를 작성하여 역할이 다른 경우에 대한 특정 동작을 수행합니다.
                	request.setAttribute("msg", "테스트 유저는 사용할 수 없는 기능입니다.");
                	request.setAttribute("url", "/admin/list");
                	request.getRequestDispatcher("/alert").forward(request, response);
                });
       ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser(username)
            .password("{noop}" + password)
            .roles("ADMIN");
        
        auth.inMemoryAuthentication()
            .withUser(test_username)
            .password("{noop}" + test_password)
            .roles("USER");
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers("/static/**","/upload_image/**");
    }
    
    
    @Component
    public static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException, ServletException {
            // 이전 페이지 URL을 가져옵니다.
            String referrer = request.getHeader("referer");

            // 사용자 역할을 확인합니다.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                // 사용자가 인증되지 않은 경우 로그인 페이지로 리디렉션합니다.
                response.sendRedirect("/admin/login");
            } else {
                // 경고 메시지를 설정합니다.
                String message = "테스터는 사용할 수 없는 기능입니다.";

                // 경고 메시지를 포함한 이전 페이지로 리디렉션합니다.
                response.sendRedirect(referrer + "?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
            }
        }
    }

}
