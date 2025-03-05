package com.outsourcing.config;

import static com.outsourcing.domain.user.enums.UserRole.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.outsourcing.common.filter.JwtAuthenticationFilter;
import com.outsourcing.common.filter.JwtAuthorizationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthorizationFilter jwtAuthorizationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(session ->
				session.sessionCreationPolicy(STATELESS))
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(request ->
				request.requestMatchers(POST, "/api/*/auth/**").permitAll()
					.requestMatchers(POST, "/api/*/auth/logout").authenticated()
					.requestMatchers("/api/*/owners/**").hasAuthority(OWNER.getAuthority())
					.requestMatchers("/api/*/customers/**").hasAuthority(CUSTOMER.getAuthority())
					.requestMatchers("/api/*/flies", "/api/*/flies/multiple").authenticated()
					.anyRequest().authenticated())
			.cors(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtAuthenticationFilter, JwtAuthorizationFilter.class);
		return http.build();
	}
}
