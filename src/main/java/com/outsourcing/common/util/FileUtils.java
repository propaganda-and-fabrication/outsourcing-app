package com.outsourcing.common.util;

import static com.outsourcing.common.exception.ErrorCode.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

import com.outsourcing.common.exception.BaseException;

public class FileUtils {
	private static final String FILE_EXTENSION_SEPARATOR = ".";

	public static String buildFilename(String originalFilename, String email) {
		int fileExtensionIndex = originalFilename.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		String fileExtension = originalFilename.substring(fileExtensionIndex).toLowerCase();
		String filename = originalFilename.substring(0, fileExtensionIndex);
		String now = String.valueOf(System.currentTimeMillis());

		String convertedEmail = DigestUtils.md5DigestAsHex(email.getBytes());

		return convertedEmail + "/" + now + "_" + filename + fileExtension;
	}

	public static byte[] resize(MultipartFile originalImage, int width, int height) {
		try (InputStream inputStream = originalImage.getInputStream()) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			Thumbnails.of(inputStream)
				.size(width, height)
				.outputFormat("jpeg")
				.toOutputStream(byteArrayOutputStream);
			return byteArrayOutputStream.toByteArray();
		} catch (IOException ioe) {
			throw new BaseException(FILE_RESIZE_ERROR);
		}
	}
}
