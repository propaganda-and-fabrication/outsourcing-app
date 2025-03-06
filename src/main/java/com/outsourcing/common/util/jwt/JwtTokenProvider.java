package com.outsourcing.common.util.jwt;

import static com.outsourcing.common.constant.Const.*;
import static com.outsourcing.common.exception.ErrorCode.*;
import static io.jsonwebtoken.Jwts.SIG.*;
import static io.jsonwebtoken.io.Decoders.*;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.auth.service.CustomUserDetailsService;
import com.outsourcing.domain.user.enums.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final CustomUserDetailsService userDetailsService;
	private SecretKey key;
	@Value("${jwt.secret.expiration}")
	private long expiration;

	@Value("${jwt.secret.key}")
	private String secretKey;

	@Value("${jwt.secret.issuer}")
	private String issuer;

	@PostConstruct    // 설정 파일에서 값을 읽어오기 때문에 애플리케이션이 완전히 올라간 이후 초기화 하도록 함
	public void init() {
		byte[] keyBytes = BASE64.decode(secretKey);
		key = Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateAccessToken(Long id, String email, UserRole userRole) {
		Date now = new Date(System.currentTimeMillis());
		return BEARER_PREFIX + Jwts.builder()
			.header()
			.add("typ", "JWT")
			.and()
			.subject(email)
			.claim("userId", id)
			.claim("userRole", userRole)
			.issuedAt(now)
			.expiration(new Date(now.getTime() + (1000 * 60 * 15)))
			.issuer(issuer)
			.signWith(key, HS256)
			.compact();
	}

	public String generateRefreshToken(String email) {
		Date now = new Date(System.currentTimeMillis());
		return Jwts.builder()
			.header()
			.add("typ", "JWT")
			.and()
			.subject(email)
			.issuedAt(now)
			.expiration(new Date(now.getTime() + expiration))
			.issuer(issuer)
			.signWith(key, HS256)
			.compact();
	}

	public String substringToken(String token) {
		if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
			return token.substring(7);
		}
		throw new BaseException(INVALID_TOKEN);
	}

	public Claims extractClaims(String token) {
		return Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	public Authentication getAuthentication(String token) {
		String subject = getSubject(token);
		CustomUserDetails userDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(subject);
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public boolean isTokenValidated(String token) {
		Claims payload = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
		// 만료 시간이 현재보다 과거가 아니고, 발급한 사람이 동일하면 true
		return !payload.getExpiration().before(new Date()) && payload.getIssuer().equals(issuer);
	}

	public String getSubject(String token) {
		return Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getSubject();
	}
}
