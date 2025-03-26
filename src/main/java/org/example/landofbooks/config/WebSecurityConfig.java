package org.example.landofbooks.config;

import org.example.landofbooks.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private JwtFilter jwtFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/authenticate",
                                "/api/v1/user/register",
                                "/api/v1/user/findByEmail",
                                "/api/v1/user/getAll",
                                "/api/v1/auth/refreshToken",
                                "/api/v1/admin/test1",
                                "/api/v1/admin/test2",
                                "/api/v1/category/save",
                                "/api/v1/category/delete/**",
                                "/api/v1/category/list",
                                "/api/v1/books/place",
                                "/api/v1/books/byStatus",
                                "/api/v1/books/delete/**",
                                "/api/v1/books/changeStatus/**",
                                "/api/v1/books/uploadImage",
                                "api/v1/books/sales",
                                "api/v1/books/ACTIVE",
                                "/api/v1/bookCart/add",
                                "api/v1/bidding/place",
                                "/api/v1/bidding/place/**",
                                "api/v1/bidding/byStatus",
                                "api/v1/bidding/delete/**",
                                "api/v1/bidding/changeStatus/**",
                                "/api/v1/bidding/end/**",
                                "/api/v1/bidding/end",
                                "/api/v1/bidding/deleteStorage/**",
                                "/api/v1/bidding/byUser",
                                "/api/v1/bidding/active",
                                "/api/v1/bidding/bids/**",
                                "/api/v1/bidCart/list",
                                "/api/v1/images/**",
                                "api/v1/bidCart/",
                                "api/v1/bidCart/delete/**",
                                "api/v1/bidCart/deleteAll/**",
                                "api/v1/bidCart/user/**",
                                "api/v1/bookCart/user/**",
                                "api/v1/bookCart/deleteAll/**",
                                "api/v1/bookCart/delete/**",
                                "api/v1/orders/place",
                                "api/v1/category/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/api/v1/bidStorage/placeBid",
                                "/api/v1/bidStorage/maxBid/**",
                                "/swagger-ui.html",
                                "/api/v1/user/**",
                                "/api/v1/bookCart/count/**",
                                "/api/v1/bidCart/count/**",
                                "/api/v1/orders/daily-count"
                        ).permitAll()
                        .requestMatchers("/uploads/**", "/api/v1/images/**", "/uploads/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}