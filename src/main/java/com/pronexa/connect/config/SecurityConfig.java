package com.pronexa.connect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.pronexa.connect.services.impl.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {

    private final AuthFailureHandler authFailureHandler;
    private final OAuthAuthenticationSuccessHandler oauthSuccessHandler;
    private final SecurityCustomUserDetailService userDetailsService;

    public SecurityConfig(AuthFailureHandler authFailureHandler,
                          OAuthAuthenticationSuccessHandler oauthSuccessHandler,
                          SecurityCustomUserDetailService userDetailsService) {
        this.authFailureHandler = authFailureHandler;
        this.oauthSuccessHandler = oauthSuccessHandler;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Modern AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // AuthenticationProvider using userDetailsService
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // OK in Spring Security 6
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Public and protected endpoints
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/home", "/register", "/services").permitAll()
                .requestMatchers("/user/**").authenticated()
                .anyRequest().permitAll()
        );

        // Form login
        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/authenticate")
                .defaultSuccessUrl("/user/profile", true)
                .usernameParameter("email")
                .passwordParameter("password")
                .failureHandler(authFailureHandler)
        );

        // OAuth2 login
        http.oauth2Login(oauth -> oauth
                .loginPage("/login")
                .successHandler(oauthSuccessHandler)
        );

        // Logout
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
        );

        // // Handle DisabledException globally (optional, but we mainly rely on failureHandler)
        // http.exceptionHandling(ex -> ex
        //         .authenticationEntryPoint((request, response, authException) -> {
        //             if (authException instanceof DisabledException) {
        //                 HttpSession session = request.getSession(true);
        //                 session.setAttribute("message", Message.builder()
        //                         .content("Your account is disabled. Check your email for verification link!")
        //                         .type(MessageType.red)
        //                         .build());
        //                 response.sendRedirect("/login");
        //             } else {
        //                 response.sendRedirect("/login?error=true");
        //             }
        //         })
        // );

        // Disable CSRF if needed (otherwise handle via Thymeleaf tokens)
        http.csrf(AbstractHttpConfigurer::disable);

        // Set the authentication provider
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}
