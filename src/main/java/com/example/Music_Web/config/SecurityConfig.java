package com.example.Music_Web.config;

import com.example.Music_Web.service.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        private final JpaUserDetailsService jpaUserDetailsService;
        private final LoginSuccessHandler loginSuccessHandler;

        public SecurityConfig(JpaUserDetailsService jpaUserDetailsService,
                        LoginSuccessHandler loginSuccessHandler) {
                this.jpaUserDetailsService = jpaUserDetailsService;
                this.loginSuccessHandler = loginSuccessHandler;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable()) // <-- only use this temporarily!
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/", "/discover", "/topchart", "/song/**", "/artist/**",
                                                                "/album/**", "/genre/**", "/search/**",
                                                                "/auth/**", "/css/**", "/js/**", "/images/**",
                                                                "/uploads/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/playlist/**", "/setting", "/favorite")
                                                .authenticated()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/auth/signin")
                                                .loginProcessingUrl("/auth/signin")
                                                // .successHandler(loginSuccessHandler)
                                                .defaultSuccessUrl("/", true)
                                                .failureUrl("/auth/signin?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/auth/logout")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .deleteCookies("JSESSIONID")
                                                .logoutSuccessUrl("/auth/signin?logout=true")
                                                .permitAll())
                                .build();
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
