package com.hako.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.username}")
    private String username;

    @Value("${security.password}")
    private String password;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .csrf().disable()
            .authorizeRequests()
            .antMatchers("/admin/**").authenticated() // admin 페이지에 대해 인증 필요
            .anyRequest().permitAll() // 나머지 페이지에 대해 인증 없이 접근 가능
            .and()
            .formLogin()
                .defaultSuccessUrl("/admin/list") // 로그인 성공 시 이동할 URL
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser(username)
            .password("{noop}" + password) // {noop} 접두어는 NoOpPasswordEncoder를 사용하겠다는 의미
            .roles("ADMIN");
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers("/static/**","/upload_image/**");
    }
    
 

}
