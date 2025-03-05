package com.outsourcing.common.filter;

import static com.outsourcing.common.exception.ErrorCode.*;
import static java.lang.Boolean.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.common.exception.ErrorCodeDto;
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
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		// Header에 토큰이 존재하는지 확인하고 없으면 바로 다음 필터로 보냄
		String accessTokenWithBearer = request.getHeader("Authorization");
		if (accessTokenWithBearer == null || !accessTokenWithBearer.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			Boolean isBlacklisted = redisTemplate.hasKey("blacklist:" + accessTokenWithBearer);
			if (TRUE.equals(isBlacklisted)) {
				throw new BaseException(MISSING_AUTHENTICATION_INFORMATION);
			}

			String accessTokenWithoutBearer = jwtTokenProvider.substringToken(accessTokenWithBearer);
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

			filterChain.doFilter(request, response);
		} catch (SecurityException | MalformedJwtException e) {
			handleJwtException(request, response, e, INVALID_TOKEN_SIGNATURE);
		} catch (ExpiredJwtException e) {
			handleJwtException(request, response, e, TOKEN_ALREADY_EXPIRED);
		} catch (UnsupportedJwtException e) {
			handleJwtException(request, response, e, UNSUPPORTED_TOKEN);
		} catch (BaseException e) {
			handleJwtException(request, response, e, e.getErrorCode());
		}
	}

	private void handleJwtException(
		HttpServletRequest request,
		HttpServletResponse response,
		Exception e,
		ErrorCode errorCode
	) throws IOException {
		log.error("Request URI [{}], Error [{}]: {}",
			request.getRequestURI(), e.getClass().getSimpleName(), e.getLocalizedMessage());

		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		// 응답 구조를 맞춰주기 위해 ObjectMapper 사용
		ObjectMapper objectMapper = new ObjectMapper();
		ErrorCodeDto errorCodeDto = new ErrorCodeDto(errorCode);
		Response<ErrorCodeDto> errorResponse = Response.error(errorCodeDto);
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
