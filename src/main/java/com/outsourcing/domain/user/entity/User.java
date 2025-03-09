package com.outsourcing.domain.user.entity;

import static com.outsourcing.domain.user.enums.UserRole.*;
import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static jakarta.persistence.InheritanceType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import com.outsourcing.common.entity.BaseTime;
import com.outsourcing.domain.user.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = PROTECTED)
@Inheritance(strategy = JOINED)
@DiscriminatorColumn(name = "type")
public class User extends BaseTime {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	private String profileUrl;

	@Column(nullable = false, unique = true)
	private String phoneNumber;

	@Enumerated(STRING)
	@Column(nullable = false, updatable = false)
	private UserRole role;

	private LocalDateTime deletedAt;

	public User(String email, String password, String name, String phoneNumber, UserRole role) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.profileUrl = (role == CUSTOMER) ? "profiles/customer.jpg" : "profiles/owner.png";
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void changeProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public void changePhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}

