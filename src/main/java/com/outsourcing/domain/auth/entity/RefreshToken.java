package com.outsourcing.domain.auth.entity;

import static lombok.AccessLevel.*;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import com.outsourcing.common.entity.BaseTime;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 14)    // 단위는 초
public class RefreshToken extends BaseTime {

	@Id
	private String email;

	@Indexed
	private String refreshToken;

	@Builder
	public RefreshToken(String email, String refreshToken) {
		this.email = email;
		this.refreshToken = refreshToken;
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
