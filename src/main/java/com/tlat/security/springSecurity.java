package com.tlat.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class springSecurity {
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests((authorize) -> authorize
            // Public access
            .requestMatchers("/webjars/**", "/register/**", "/forgot/**", "/index", "/").permitAll()
            
            // Shared access (both ADMIN and USER)
            .requestMatchers("/main").hasAnyRole("ADMIN", "USER")
            .requestMatchers("/lectures").hasAnyRole("ADMIN", "USER")
            .requestMatchers("/rooms").hasAnyRole("ADMIN", "USER")
            
            // ADMIN only access
            .requestMatchers("/users/**").hasRole("ADMIN")
            .requestMatchers("/add/**").hasRole("ADMIN")
            .requestMatchers("/edit/**").hasRole("ADMIN")
            .requestMatchers("/delete/**").hasRole("ADMIN")
            .requestMatchers("/rooms/add/**").hasRole("ADMIN")
            .requestMatchers("/rooms/edit/**").hasRole("ADMIN") 
            .requestMatchers("/rooms/delete/**").hasRole("ADMIN")
            .requestMatchers("/lectures/add/**").hasRole("ADMIN")
            .requestMatchers("/lectures/edit/**").hasRole("ADMIN")
            .requestMatchers("/lectures/delete/**").hasRole("ADMIN")
            .requestMatchers("/lectures/import/**").hasRole("ADMIN")
            
            .anyRequest().authenticated()
        )
            .formLogin(
                form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/main")  // Redirect to main page after login
                    .permitAll())
            .logout(
                logout -> logout
                    .logoutRequestMatcher(
                        new AntPathRequestMatcher("/logout"))
                    .permitAll());
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }
}