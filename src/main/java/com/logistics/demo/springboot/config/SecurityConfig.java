package com.logistics.demo.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Phase 2: Spring Security 検証
 * 
 * 意図的な問題:
 * - CSRF無効化
 * - 全エンドポイント認証なし
 * - パスワードエンコーダーなし
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF無効化（危険）
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 全エンドポイント認証なし
            );
        
        return http.build();
    }
    
    // パスワードエンコーダーの定義なし
    // UserDetailsServiceの定義なし
}
