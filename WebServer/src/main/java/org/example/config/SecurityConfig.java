package org.example.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/test").permitAll()
                                .requestMatchers("/testSearch").permitAll()
                                .requestMatchers("/download").permitAll()
                                .requestMatchers("/hello").permitAll()
                                .requestMatchers("/resources/get/{id}").permitAll()
                                .requestMatchers("/resources/documents").permitAll()
                                .requestMatchers("/resources/projects").permitAll()
                                .requestMatchers("/resources/solutions").permitAll()
                                .requestMatchers("/search").permitAll()
                                .requestMatchers("/provide").permitAll()
                                .anyRequest().authenticated()
                )
                .logout(logout ->
                        logout.logoutUrl("/logout") // URL для выхода
                );
        return http.build();
    }
}
