package com.outsourcing.common.storage.controller;

import static com.outsourcing.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.response.Response;
import com.outsourcing.common.storage.dto.response.MultipleUploadResponse;
import com.outsourcing.common.storage.dto.response.UploadResponse;
import com.outsourcing.common.storage.service.s3.S3StorageService;
import com.outsourcing.domain.auth.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileUploadController {

	private final S3StorageService s3StorageService;

	@PostMapping("/v1/flies")
	public Response<UploadResponse> uploadFile(
		@RequestPart("multipartFile") MultipartFile multipartFile,
		@RequestParam("type") String type,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		UploadResponse response = s3StorageService.upload(multipartFile, type, currentUser);
		return Response.of(response);
	}

	@PostMapping("/v1/flies/multiples")
	public Response<MultipleUploadResponse> uploadMultipleFiles(
		@RequestPart("multipartFiles") List<MultipartFile> multipartFiles,
		@RequestParam("type") String type,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		if (multipartFiles.size() > 3) {
			throw new BaseException(FILE_UPLOAD_LIMIT_EXCEEDED);
		}
		MultipleUploadResponse response = s3StorageService.multipleUpload(multipartFiles, type, currentUser);
		return Response.of(response);
	}
}
