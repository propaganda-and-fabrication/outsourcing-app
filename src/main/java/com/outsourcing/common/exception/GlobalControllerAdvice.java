package com.outsourcing.common.exception;

import static com.outsourcing.common.exception.ErrorCode.*;
import static jakarta.servlet.http.HttpServletResponse.*;

import java.util.List;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.outsourcing.common.response.Response;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

	@ExceptionHandler(BaseException.class)
	public Response<ErrorCodeDto> baseExceptionHandler(BaseException be, HttpServletResponse response) {
		ErrorCode errorCode = be.getErrorCode();
		errorCode.apply(response);
		return Response.error(new ErrorCodeDto(errorCode));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Response<List<ValidationErrorDto.ValidationError>> methodArgumentNotValidExceptionHandler(
		MethodArgumentNotValidException manve, HttpServletResponse response) {

		List<FieldError> fieldErrors = manve.getBindingResult().getFieldErrors();
		List<ValidationErrorDto.ValidationError> errors = fieldErrors.stream()
			.map(fieldError -> {
				String code = fieldError.getCode();
				String field = fieldError.getField();
				String message = fieldError.getDefaultMessage();
				return new ValidationErrorDto.ValidationError(code, field, message);
			}).toList();
		response.setStatus(SC_BAD_REQUEST);
		return Response.error(errors);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public Response<ErrorCodeDto> methodArgumentTypeMismatchExceptionHandler(
		MethodArgumentTypeMismatchException matme, HttpServletResponse response) {

		String expectedType = matme.getRequiredType() == null
			? "Unknown" : matme.getRequiredType().getSimpleName();
		log.error("[MethodArgumentTypeMismatchException] field: {}, expected: {}, value:{}", matme.getName(),
			expectedType, matme.getValue());
		response.setStatus(SC_BAD_REQUEST);
		return Response.error(new ErrorCodeDto(TYPE_MISMATCH));
	}

	@ExceptionHandler(Exception.class)
	public Response<ErrorCodeDto> exceptionHandler(Exception e, HttpServletResponse response) {
		log.error("[Exception]: {}", e.getLocalizedMessage());
		response.setStatus(SC_INTERNAL_SERVER_ERROR);
		return Response.error(new ErrorCodeDto(SERVER_NOT_WORK));
	}
}
