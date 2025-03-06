package com.outsourcing.config;

import static com.outsourcing.domain.user.enums.UserRole.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.outsourcing.common.filter.JwtAuthenticationFilter;
import com.outsourcing.common.handler.CustomAccessDeniedHandler;
import com.outsourcing.common.handler.CustomAuthenticationEntryPoint;
import com.outsourcing.common.util.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider tokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(session ->
				session.sessionCreationPolicy(STATELESS))
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(request ->
				request.requestMatchers(POST, "/api/*/auth/owners/**", "/api/*/auth/customers/**").permitAll()
					.requestMatchers("/api/*/auth/logout").authenticated()
					.requestMatchers("/api/v1/users").authenticated()
					.requestMatchers("/api/v1/orders/**").authenticated()
					.requestMatchers("/api/*/flies/**").authenticated()
					.requestMatchers("/api/*/owners/**").hasAuthority(OWNER.getAuthority())
					.requestMatchers("/api/*/customers/**").hasAuthority(CUSTOMER.getAuthority())
					.anyRequest().authenticated())
			.cors(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.exceptionHandling(conf ->
				conf.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
					.accessDeniedHandler(new CustomAccessDeniedHandler()))
			.addFilterBefore(new JwtAuthenticationFilter(tokenProvider, redisTemplate),
				UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
