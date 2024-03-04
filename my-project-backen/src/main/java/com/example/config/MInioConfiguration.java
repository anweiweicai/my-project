package com.example.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Slf4j
@Configuration
public class MInioConfiguration {
    @Value("${spring.minio.endpoint}")
    String endpoint;

    @Value("${spring.minio.username}")
    String username;

    @Value("${spring.minio.password}")
    String password;
    @Bean
    public MinioClient minioClient(){
        log.info("Init minio client...");
        return MinioClient.builder()
                .endpoint(endpoint) // 设置MinIO服务的端点地址。
                .credentials(username, password) // 设置MinIO服务的访问凭证，包括用户名和密码。
                .build();
    }
}
