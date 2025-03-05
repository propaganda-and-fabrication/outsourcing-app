package com.outsourcing.common.handler;

import static com.outsourcing.common.exception.ErrorCode.*;
import static jakarta.servlet.http.HttpServletResponse.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsourcing.common.exception.ErrorCodeDto;
import com.outsourcing.common.response.Response;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {

		log.error("권한 없음: {}", accessDeniedException.getLocalizedMessage());

		ObjectMapper objectMapper = new ObjectMapper();
		String errorResponse = objectMapper.writeValueAsString(Response.error(new ErrorCodeDto(ACCESS_DENIED)));

		response.setContentType(APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.setStatus(SC_FORBIDDEN);
		response.getWriter().write(errorResponse);
	}
}
