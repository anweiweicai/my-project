package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.entity.dto.Account;
import com.example.mapper.AccountMapper;
import com.example.service.ImageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {
    @Resource
    MinioClient client;

    @Resource
    AccountMapper mapper;
    @Override
    public String uploadAvatar(MultipartFile file, int id) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-","");// 给图片一个随机名
        imageName = "/avatar/" + imageName;
        PutObjectArgs args = PutObjectArgs.builder() // 上传文件的操作
                .bucket("study")
                // 这部分指定了要上传到的存储桶的名称为"study"，即指定了文件要存储的位置。
                .stream(file.getInputStream(), file.getSize(), -1)
                // 这部分使用file对象获取文件的输入流file.getInputStream()
                // 并指定了文件大小file.getSize()和不指定分块大小，即将整个文件作为一个流上传。
                .object(imageName)
                // 这部分指定了存储对象的名称为imageName，即上传后文件在存储桶中的名称。
                .build();
        try {
            client.putObject(args);
            if (mapper.update(null, Wrappers.<Account>update().eq("id", id).set("avatar", imageName)) > 0){
                return imageName;
            }else {
                return null;
            }

        }catch (Exception e){
            log.error("图片上传出现问题: " + e.getMessage(), e);
            return null;
        }
    }
}
