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
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; " +
                                        "script-src 'self' 'unsafe-inline' https://sandbox.payhere.lk; " +
                                        "connect-src 'self' https://sandbox.payhere.lk; " +
                                        "img-src 'self' data:; " +
                                        "style-src 'self' 'unsafe-inline';"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/authenticate",
                                "/api/v1/user/register",
                                "/api/v1/user/findByEmail",
                                "/api/v1/user/update/**",
                                "/api/v1/user/getById/**",
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
                                "api/v1/books/pending",
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
                                "/api/v1/bidding/pending",
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
                                "api/v1/orders/getAll",
                                "api/v1/orders/updateStatus/**",
                                "api/v1/category/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/api/v1/bidStorage/placeBid",
                                "/api/v1/bidStorage/maxBid/**",
                                "/swagger-ui.html",
                                "/api/v1/user/**",
                                "/api/v1/bookCart/count/**",
                                "/api/v1/bidCart/count/**",
                                "/api/v1/orders/daily-count",
                                "/api/v1/auth/reset-password",
                                "/api/v1/auth/verify-otp",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/resend-otp",
                                "/api/v1/payment/create-payment",
                                "api/v1/payment/payment-success",
                                "api/v1/payment-webhook",
                                "/api/v1/payment-notify",
                                "/api/v1/auth/authenticate",
                                "/api/v1/user/register",
                                // Other endpoints to be allowed
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/v1/payment-success/**",
                                "/api/v1/payment-success",
                                "/api/v1/payments/notify",
                                "/api/v1/payments/notify/**",
                                "/api/v1/payments/success",
                                "/api/v1/payments/successY"
                        ).permitAll()
                        .requestMatchers("/user/return.html").permitAll()
                        .requestMatchers("/uploads/**", "/api/v1/images/**", "src/main/resources/uploads/images/**").permitAll()
                        .requestMatchers("/", "/index.html", "/return.html", "/user/**", "/css/**", "/js/**").permitAll()

                        .anyRequest().authenticated()

                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}