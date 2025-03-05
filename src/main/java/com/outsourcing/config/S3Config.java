package com.outsourcing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

	@Value("${spring.cloud.aws.credentials.access-key}")
	private String iamAccessKey;

	@Value("${spring.cloud.aws.credentials.secret-key}")
	private String iamSecretKey;

	@Value("${spring.cloud.aws.s3.region}")
	private String s3Region;

	@Bean
	public AwsCredentials awsCredentials() {
		return AwsBasicCredentials.create(iamAccessKey, iamSecretKey);
	}

	@Bean
	public S3Client s3Client(AwsCredentials awsCredentials) {
		return S3Client.builder()
			.region(Region.of(s3Region))
			.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
			.build();
	}
}
