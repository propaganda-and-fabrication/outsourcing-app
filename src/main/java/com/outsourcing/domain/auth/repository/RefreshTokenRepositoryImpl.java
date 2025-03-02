package com.outsourcing.domain.auth.repository;

import static java.util.concurrent.TimeUnit.*;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.outsourcing.domain.auth.entity.RefreshToken;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

	private final RedisTemplate<String, String> redisTemplate;

	@Value("${redis.timeout}")
	private Long timeout;

	@Override
	public void save(RefreshToken refreshToken) {
		// opsFor: 특정 컬렉션의 작업(operation)들을 호출할 수 있는 인터페이스를 반환. 현재는 String을 위한 것
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		// 만약 이미 그 email을 key값으로 한 refreshToken이 있을 경우, 업데이트를 위해 삭제
		if (valueOperations.get(refreshToken.getEmail()) != null) {
			redisTemplate.delete(refreshToken.getEmail());
		}

		// redis에 refreshToken을 저장
		valueOperations.set(refreshToken.getEmail(), refreshToken.getRefreshToken());

		// refreshToken 시간 지정 -> 14일
		redisTemplate.expire(refreshToken.getEmail(), timeout, SECONDS);
	}

	@Override
	public Optional<RefreshToken> findByEmail(String email) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		String refreshToken = valueOperations.get(email);

		if (refreshToken == null) {
			return Optional.empty();
		}

		return Optional.of(new RefreshToken(email, refreshToken));
	}

	@Override
	public String getKey(String email) {
		return redisTemplate.opsForValue().get(email);
	}

	@Override
	public void addBlacklist(String accessToken, long expiration) {
		redisTemplate.opsForValue().set("blacklist:" + accessToken, "logout", expiration, MILLISECONDS);
	}

	@Override
	public void delete(String email) {
		redisTemplate.delete(email);
	}
}
