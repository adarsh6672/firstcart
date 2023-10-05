package com.firstcart_ecommerce.firstcart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
    @Autowired
    public CustomAuthSuccessHandler successHandler;








    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService getDetailsService(){
        return new CustomUserDetailsService();
    }


    @Bean
    public DaoAuthenticationProvider getAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(getDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeHttpRequests()
                .requestMatchers("img/**","fonts/**","/static/**","/js/**","css/**","/","/register","/login","/saveuser","/sendOTP","/verifyOTP").permitAll()
                .requestMatchers("/user/**").hasAnyRole("USER","ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN").and()
                .formLogin().loginPage("/login").loginProcessingUrl("/login")
                .successHandler(successHandler)
                .failureUrl("/login?error")
                .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID");

        return http.build();
    }

}
