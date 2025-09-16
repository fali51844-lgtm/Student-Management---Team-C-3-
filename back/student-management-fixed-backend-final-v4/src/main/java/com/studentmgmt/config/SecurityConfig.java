//package com.studentmgmt.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.config.Customizer;
//
//@Configuration
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        var user = User.withUsername("admin")
//                       .password("adminpass")
//                       .roles("ADMIN")
//                       .build();
//        return new InMemoryUserDetailsManager(user);
//    }
//
//    @SuppressWarnings("deprecation")
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
//        http
//          .csrf(csrf -> csrf.disable())
//          .authorizeHttpRequests(auth -> auth
//              .requestMatchers("/api/auth/**","/api/public/**","/h2-console/**").permitAll()
//              .requestMatchers("/api/enrollments/**","/api/students/**","/api/courses/**").hasRole("ADMIN")
//              .anyRequest().authenticated()
//          )
//          .httpBasic(Customizer.withDefaults())
//          .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
//        return http.build();
//    }
//}
