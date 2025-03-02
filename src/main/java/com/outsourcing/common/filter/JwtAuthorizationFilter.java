package com.outsourcing.common.filter;

import static com.outsourcing.common.exception.ErrorCode.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsourcing.common.exception.ErrorCodeDto;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		if (requestURI.startsWith("/api/v1/auth")) {
			filterChain.doFilter(request, response);
			return;
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			log.error("인증 정보 누락");
			response.setStatus(MISSING_AUTHENTICATION_INFORMATION.getHttpStatus().value());
			response.setContentType(APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("UTF-8");
			ErrorCodeDto errorCodeDto = new ErrorCodeDto(MISSING_AUTHENTICATION_INFORMATION);

			// 응답 구조를 맞춰주기 위해 ObjectMapper 사용
			ObjectMapper objectMapper = new ObjectMapper();
			response.getWriter().write(objectMapper.writeValueAsString(errorCodeDto));
			return;
		}

		filterChain.doFilter(request, response);
	}

}
