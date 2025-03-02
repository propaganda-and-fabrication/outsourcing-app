package com.outsourcing.common.exception;

import static com.outsourcing.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.outsourcing.common.response.Response;
import com.outsourcing.common.response.ValidationErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

	@ExceptionHandler(BaseException.class)
	public Response<BaseException> baseExceptionHandler(BaseException be) {
		ErrorCode errorCode = be.getErrorCode();
		return Response.error(errorCode, errorCode.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Response<MethodArgumentNotValidException> methodArgumentNotValidExceptionHandler(
		MethodArgumentNotValidException manve) {

		List<FieldError> fieldErrors = manve.getBindingResult().getFieldErrors();
		List<ValidationErrorResponse.ValidationError> errors = fieldErrors.stream()
			.map(fieldError -> {
				String code = fieldError.getCode();
				String field = fieldError.getField();
				String message = fieldError.getDefaultMessage();
				return new ValidationErrorResponse.ValidationError(code, field, message);
			}).toList();
		return Response.validationError(errors);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public Response<MethodArgumentTypeMismatchException> methodArgumentTypeMismatchExceptionHandler(
		MethodArgumentTypeMismatchException matme) {

		String expectedType = matme.getRequiredType() == null
			? "Unknown" : matme.getRequiredType().getSimpleName();
		log.error("[MethodArgumentTypeMismatchException] field: {}, expected: {}, value:{}", matme.getName(),
			expectedType, matme.getValue());
		return Response.error(TYPE_MISMATCH, TYPE_MISMATCH.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public Response<Exception> exceptionHandler(Exception e) {
		log.error("[Exception]: {}", e.getLocalizedMessage());
		return Response.error(SERVER_NOT_WORK, SERVER_NOT_WORK.getMessage());
	}
}
