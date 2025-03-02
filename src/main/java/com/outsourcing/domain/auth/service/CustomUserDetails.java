package com.outsourcing.domain.auth.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.outsourcing.domain.user.dto.UserInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	private final UserInfo userInfo;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(userInfo.getRole().getAuthority()));
	}

	@Override
	public String getPassword() {
		return userInfo.getPassword();
	}

	@Override
	public String getUsername() {
		return userInfo.getEmail();
	}

	@Override
	public boolean isEnabled() {    // 사용자 활성화 여부
		// 삭제 시간이 적혀있지 않다면 true
		return userInfo.getDeletedAt() == null;
	}
}
