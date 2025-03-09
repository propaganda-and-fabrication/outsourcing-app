package com.outsourcing.common.handler;

import static com.outsourcing.common.exception.ErrorCode.*;
import static jakarta.servlet.http.HttpServletResponse.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsourcing.common.exception.ErrorCodeDto;
import com.outsourcing.common.response.Response;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		log.error("인증 정보 없음: {}", authException.getLocalizedMessage());

		ErrorCodeDto errorCodeDto = new ErrorCodeDto(MISSING_AUTHENTICATION_INFORMATION);
		ObjectMapper objectMapper = new ObjectMapper();
		String errorResponse = objectMapper.writeValueAsString(Response.error(errorCodeDto));

		response.setContentType(APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.setStatus(SC_UNAUTHORIZED);
		response.getWriter().write(errorResponse);
	}
}
