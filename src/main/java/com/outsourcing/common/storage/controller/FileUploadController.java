package com.outsourcing.common.storage.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.outsourcing.common.response.Response;
import com.outsourcing.common.storage.dto.response.UploadResponse;
import com.outsourcing.common.storage.s3.S3StorageService;
import com.outsourcing.domain.auth.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileUploadController {

	private final S3StorageService s3StorageService;

	@PostMapping("/v1/flies")
	public Response<UploadResponse> uploadFile(
		//TODO: "[Exception]: Required part 'multipartFile' is not present" 핸들링 필요
		@RequestPart("multipartFile") MultipartFile multipartFile,
		String type,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		UploadResponse response = s3StorageService.upload(multipartFile, type, currentUser);
		return Response.of(response);
	}
}
