package com.outsourcing.domain.auth.repository;

import java.util.Optional;

import com.outsourcing.domain.auth.entity.RefreshToken;

public interface RefreshTokenRepository {

	void save(RefreshToken refreshToken);

	Optional<RefreshToken> findByEmail(String email);

	void delete(String email);

	String getValueByKey(String email);

	void addBlacklist(String accessToken, long expiration);
}
