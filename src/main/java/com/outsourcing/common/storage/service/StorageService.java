package com.outsourcing.common.storage.service;

import org.springframework.web.multipart.MultipartFile;

import com.outsourcing.common.storage.dto.response.UploadResponse;
import com.outsourcing.domain.auth.service.CustomUserDetails;

public interface StorageService {

	UploadResponse upload(MultipartFile multipartFile, String type, CustomUserDetails currentUser);
}
