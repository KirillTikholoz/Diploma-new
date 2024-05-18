package org.example.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableCaching
@RequiredArgsConstructor
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtRequestFilter jwtRequestFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/test").permitAll()
                                .requestMatchers("/testSearch/project").permitAll()
                                .requestMatchers("/testSearch/solution").permitAll()
                                .requestMatchers("/testSearch/document").permitAll()
                                .requestMatchers("/project_to_decision").permitAll()
                                .requestMatchers("/hello").permitAll()
                                .requestMatchers("/resources/get/{id}").permitAll()
                                .requestMatchers("/resources/documents").permitAll()
                                .requestMatchers("/resources/documentsByAuthor").permitAll()
                                .requestMatchers("/resources/projects").permitAll()
                                .requestMatchers("/resources/solutions").permitAll()
                                .requestMatchers("/resources/news").permitAll()
                                .requestMatchers("/search/projects").permitAll()
                                .requestMatchers("/search/solutions").permitAll()
                                .requestMatchers("/search//documents").permitAll()
                                .requestMatchers("/getProjectSteps").permitAll()
                                .requestMatchers("/documents/download").hasRole("USER")
                                .requestMatchers("/documents/provide").hasRole("USER")
                                .requestMatchers("/documents/delete").hasRole("USER")
                                .requestMatchers("/addProjectStep").hasRole("USER")
                                .requestMatchers("/profile/getName").hasAnyRole("USER", "MODERATOR", "ADMIN")
                                .anyRequest().authenticated()
                )
                .logout(logout ->
                        logout.logoutUrl("/logout") // URL для выхода
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://localhost:8443", "https://localhost:3000", "http://localhost:3000","http://localhost:8000")); // Разрешите запросы с указанного домена
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.addAllowedHeader("*"); // Разрешите все заголовки запроса
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
