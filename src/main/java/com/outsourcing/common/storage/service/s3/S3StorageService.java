package com.outsourcing.common.storage.service.s3;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.common.storage.enums.UploadType.*;
import static org.springframework.http.MediaType.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.storage.dto.response.MultipleUploadResponse;
import com.outsourcing.common.storage.dto.response.UploadResponse;
import com.outsourcing.common.storage.enums.UploadType;
import com.outsourcing.common.storage.service.StorageService;
import com.outsourcing.common.util.FileUtils;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.user.enums.UserRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

	private final S3Client s3Client;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${spring.cloud.aws.s3.region}")
	private String region;

	@Override
	public UploadResponse upload(MultipartFile originalImage, String type, CustomUserDetails currentUser) {
		checkFileType(originalImage.getContentType());

		// convertedEmail + / + System.currentTimeMillis() + "_" + originalFilename + fileExtension
		String filename = FileUtils.buildFilename(
			Objects.requireNonNull(originalImage.getOriginalFilename()), currentUser.getUsername());

		UserRole currentUserRole = currentUser.getUserInfo().getRole();
		UploadType uploadType = UploadType.from(type);
		if (!uploadType.isAllowedFor(currentUserRole)) {
			throw new BaseException(UPLOAD_ACCESS_DENIED);
		}

		String key = uploadType.getPrefix() + filename;

		PutObjectRequest request = PutObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.contentType(originalImage.getContentType())
			.build();

		byte[] resizedImageBytes = FileUtils.resize(originalImage, 300, 300);

		//  리사이즈 후 반환되는 InputStream의 크기와 S3 업로드 시 제공하는 콘텐츠 길이가 일치해야 함
		s3Client.putObject(
			request,
			RequestBody.fromInputStream(new ByteArrayInputStream(resizedImageBytes), resizedImageBytes.length));
		log.info("https://{}.s3.{}.amazonaws.com/{}", bucket, region, key);
		return UploadResponse.of(key);
	}

	@Override
	public MultipleUploadResponse multipleUpload(
		List<MultipartFile> originalImages,
		String type,
		CustomUserDetails currentUser
	) {

		int counter = 0;
		UploadType uploadType = UploadType.from(type);
		if (!uploadType.isAllowedFor(currentUser.getUserInfo().getRole())) {
			throw new BaseException(UPLOAD_ACCESS_DENIED);
		}
		
		if (uploadType == PROFILES || uploadType == STORES) {
			throw new BaseException(INVALID_UPLOAD_TYPE);
		}

		List<String> responses = new ArrayList<>();
		for (MultipartFile image : originalImages) {
			try {
				counter++;
				UploadResponse upload = upload(image, type, currentUser);
				responses.add(upload.getUploadUrl());
			} catch (Exception e) {
				log.error("{} 번째 업로드 중 예외 발생: {}", counter, e.getLocalizedMessage());
				throw new BaseException(IMAGE_UPLOAD_IO_EXCEPTION);
			}
		}
		return MultipleUploadResponse.of(responses);
	}

	private void checkFileType(String contentType) {
		if (!IMAGE_PNG.toString().equals(contentType) && !IMAGE_JPEG.toString().equals(contentType)) {
			throw new BaseException(INVALID_FILE_TYPE);
		}
	}
}
