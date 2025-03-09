package com.outsourcing.common.aspect;

import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsourcing.common.response.Response;
import com.outsourcing.domain.order.dto.OrderRequest;
import com.outsourcing.domain.order.dto.OrderResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogTraceAspect {

	private final HttpServletRequest request;
	private final ObjectMapper objectMapper;

	@Pointcut("execution(* com.outsourcing.domain.order.controller.*.createOrder(..))")
	public void createOrderPointcut() {
	}

	@Pointcut("@annotation(com.outsourcing.common.annotation.LogTrace)")
	public void logTracePointcut() {
	}

	@Around("createOrderPointcut()")
	public Object loggingCreateOrder(ProceedingJoinPoint joinPoint) throws Throwable {
		String traceId = UUID.randomUUID().toString();
		String requestURI = request.getRequestURI();
		String method = request.getMethod();

		Long storeId = null;
		for (Object arg : joinPoint.getArgs()) {
			if (arg instanceof OrderRequest) {
				storeId = ((OrderRequest)arg).getStoreId();
				break;
			}
		}

		log.info(
			toRequestLog(traceId, method, requestURI, storeId, System.currentTimeMillis(),
				getGetArgToString(joinPoint.getArgs())));

		Object proceed = joinPoint.proceed();

		Response<OrderResponse> wrappedResponse = (Response<OrderResponse>) proceed; // 올바른 캐스팅
		OrderResponse orderResponse = wrappedResponse.getData(); // 내부 OrderResponse 추출
		String result = objectMapper.writeValueAsString(orderResponse);
		log.info(
			toResponseLog(traceId, method, requestURI, storeId, orderResponse.getOrderId(), System.currentTimeMillis(), result));

		return proceed;
	}

	@Around("logTracePointcut()")
	public Object loggingLogTrace(ProceedingJoinPoint joinPoint) throws Throwable {
		String traceId = UUID.randomUUID().toString();
		String requestURI = request.getRequestURI();
		String method = request.getMethod();

		// 포인트 컷을 만족하는 메서드의 파라미터 목록 가져오기
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		String[] parameterNames = signature.getParameterNames();

		// 포인트 컷을 만족하는 메서드의 args 가져오기
		Object[] args = joinPoint.getArgs();

		Long storeId = null;
		for (int i = 0; i < parameterNames.length; i++) {
			if ("storeId".equals(parameterNames[i])) {
				storeId = (Long)args[i];
				break;
			}
		}

		log.info(
			toRequestLog(traceId, method, requestURI, storeId, System.currentTimeMillis(),
				getGetArgToString(joinPoint.getArgs())));

		Object proceed = joinPoint.proceed();

		Response<OrderResponse> wrappedResponse = (Response<OrderResponse>) proceed; // 올바른 캐스팅
		OrderResponse orderResponse = wrappedResponse.getData(); // 내부 OrderResponse 추출
		String result = objectMapper.writeValueAsString(orderResponse);
		log.info(
			toResponseLog(traceId, method, requestURI, storeId, orderResponse.getOrderId(), System.currentTimeMillis(), result));

		return proceed;
	}

	private String getGetArgToString(Object[] args) throws JsonProcessingException {
		return objectMapper.writeValueAsString(args);
	}

	private String toRequestLog(
		String traceId,
		String method,
		String requestUrl,
		Long storeId,
		long requestTime,
		String requestBody
	) {
		return String.format(
			"%n========== HTTP REQUEST LOG ==========%n" +
				"Trace ID         : %s%n" +
				"HTTP Method      : %s%n" +
				"Request URI      : %s%n" +
				"Request Store ID : %s%n" +
				"Request Time     : %s%n" +
				"Request Body     : %s%n" +
				"======================================",
			traceId, method, requestUrl, storeId, requestTime, requestBody
		);
	}

	private String toResponseLog(
		String traceId,
		String method,
		String requestUrl,
		Long storeId,
		Long orderId,
		long requestTime,
		Object result
	) {
		return String.format(
			"%n========== HTTP RESPONSE LOG ==========%n" +
				"Trace ID         : %s%n" +
				"HTTP Method      : %s%n" +
				"Request URI      : %s%n" +
				"Request Store ID : %s%n" +
				"Request Order ID : %s%n" +
				"Request Time     : %s%n" +
				"Response Body    : %s%n" +
				"======================================",
			traceId, method, requestUrl, storeId, orderId, requestTime, result
		);
	}
}
