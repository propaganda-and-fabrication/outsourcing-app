package com.outsourcing.domain.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.outsourcing.domain.user.dto.UserInfo;
import com.outsourcing.domain.user.entity.User;
import com.outsourcing.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User getUser = userRepository.findByEmailAndDeletedAt(email)
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
		return new CustomUserDetails(UserInfo.of(getUser));
	}
}
