package com.outsourcing.common.filter;

import static com.outsourcing.common.exception.ErrorCode.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.common.response.Response;
import com.outsourcing.common.util.jwt.JwtTokenProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final List<String> whitelist = new ArrayList<>(
		List.of("/api/v1/auth/customers", "/api/v1/auth/owners", "/api/v1/auth/sign-in", "/api/v1/auth/reissue"));

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String requestURI = request.getRequestURI();

		// whitelist에 해당하는 URL이면 토큰 검증을 건너뛰고 바로 다음 필터로 넘어감
		boolean isWhitelisted = whitelist.stream().anyMatch(requestURI::startsWith);
		if (isWhitelisted) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessTokenWithBearer = request.getHeader("Authorization");

		// Header에 토큰이 존재하는지 확인
		if (accessTokenWithBearer == null || !accessTokenWithBearer.startsWith("Bearer ")) {
			returnErrorResponse(MISSING_AUTHORIZATION_HEADER, response);
			return;
		}

		Boolean isBlacklisted = redisTemplate.hasKey("blacklist:" + accessTokenWithBearer);
		if (Boolean.TRUE.equals(isBlacklisted)) {
			returnErrorResponse(MISSING_AUTHENTICATION_INFORMATION, response);
			return;
		}

		String accessTokenWithoutBearer = jwtTokenProvider.substringToken(accessTokenWithBearer);
		try {
			// 만료 시간 검증
			if (!jwtTokenProvider.isTokenValidated(accessTokenWithoutBearer)) {
				throw new BaseException(INVALID_TOKEN);
			}

			// claims가 비어 있는지 검증
			Claims claims = jwtTokenProvider.extractClaims(accessTokenWithoutBearer);
			if (claims == null) {
				throw new BaseException(INVALID_TOKEN);
			}

			// SecurityContextHolder에 저장
			Authentication authentication = jwtTokenProvider.getAuthentication(accessTokenWithoutBearer);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (SecurityException | MalformedJwtException e) {
			log.error("[{}]: {}", e.getClass().getSimpleName(), e.getLocalizedMessage());
			returnErrorResponse(INVALID_TOKEN_SIGNATURE, response);
		} catch (ExpiredJwtException e) {
			log.error("[{}]: {}", e.getClass().getSimpleName(), e.getLocalizedMessage());
			returnErrorResponse(TOKEN_ALREADY_EXPIRED, response);
		} catch (UnsupportedJwtException e) {
			log.error("[{}]: {}", e.getClass().getSimpleName(), e.getLocalizedMessage());
			returnErrorResponse(UNSUPPORTED_TOKEN, response);
		} catch (BaseException e) {
			log.error("[{}]: {}", e.getClass().getSimpleName(), e.getLocalizedMessage());
			returnErrorResponse(e.getErrorCode(), response);
		}

		filterChain.doFilter(request, response);
	}

	private void returnErrorResponse(ErrorCode errorCode, HttpServletResponse response) throws IOException {
		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		Response<ErrorCode> error = Response.error(errorCode, errorCode.getMessage());

		// 응답 구조를 맞춰주기 위해 ObjectMapper 사용
		ObjectMapper objectMapper = new ObjectMapper();
		response.getWriter().write(objectMapper.writeValueAsString(error));
	}
}
