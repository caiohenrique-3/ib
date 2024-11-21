package com.example.config;

import com.example.filters.CsrfTokenResponseHeaderBindingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.POST, "/createThread")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/replyTo/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/login/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("admin")
                        .requestMatchers(HttpMethod.POST, "/lockThread/**").hasRole("admin")
                        .requestMatchers(HttpMethod.POST, "/unlockThread/**").hasRole("admin")
                        .requestMatchers(HttpMethod.DELETE, "/threads/**").hasRole("admin")
                        .requestMatchers(HttpMethod.DELETE, "/posts/**").hasRole("admin")
                        .requestMatchers(HttpMethod.POST, "/search/**").hasRole("admin")
                        .requestMatchers(HttpMethod.POST, "/threads/multiAction").hasRole("admin")
                        .requestMatchers(HttpMethod.POST, "/posts/multiAction").hasRole("admin")
                        .anyRequest()
                        .permitAll())
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());

        http.addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.builder()
                        .username("user")
                        .password("{bcrypt}$2a$12$3bka.6PuzHB2KrRctqCrN.IGQvXx6JBhOaCmqSH91vLorG9WBfHeW")
                        .roles("user")
                        .build();

        UserDetails admin =
                User.builder()
                        .username("admin")
                        .password("{bcrypt}$2a$12$3bka.6PuzHB2KrRctqCrN.IGQvXx6JBhOaCmqSH91vLorG9WBfHeW")
                        .roles("user", "admin")
                        .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}
