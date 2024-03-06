package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.ImageStore;
import com.example.mapper.AccountMapper;
import com.example.mapper.ImageStoreMapper;
import com.example.service.ImageService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
@Slf4j
@Service
public class ImageServiceImpl extends ServiceImpl<ImageStoreMapper, ImageStore> implements ImageService{
    @Resource
    MinioClient client;

    @Resource
    AccountMapper mapper;

    @Resource
    private FlowUtils flowUtils;

    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd"); // 日期格式化
    /**
     *上传头像至minio桶中
     * @param file
     * @param id
     * @return
     * @throws IOException
     */
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
            String avatar = mapper.selectById(id).getAvatar();
            this.deleteOldAvatar(avatar);
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

    /**
     *  上传图片至minio
     * @param file
     * @param id
     * @return
     * @throws IOException
     */
    @Override
    public String uploadImage(MultipartFile file, int id) throws IOException {
        String key = Const.FORUM_IMAGE_COUNTER + id;
        if (!flowUtils.limitPeriodCounterCheck(key, 20, 3600)){
            return null;
        }
        String imageName = UUID.randomUUID().toString().replace("-","");// 给图片一个随机名
        Date date = new Date();
        imageName = "/cache/" + format.format(date) + "/" + imageName;
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("study")
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();
        try {
            client.putObject(args);
            if (this.save(new ImageStore(id, imageName, date))){
                return imageName;
            }else {
                return null;
            }
        }catch (Exception e){
            log.error("图片上传出现问题: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从minio中获取头像
     * @param stream
     * @param image
     * @throws Exception
     */
    @Override
    public void fetchImageFromMinio(OutputStream stream, String image) throws Exception {
        // 创建获取对象的参数
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket("study")  // 设置存储桶名称为 "study"
                .object(image)     // 设置对象名称为传入的 image 参数
                .build();
        // 使用客户端从MinIO获取对象，返回一个GetObjectResponse对象
        GetObjectResponse response = client.getObject(args);
        // 将从MinIO获取的对象数据复制到输出流中
        IOUtils.copy(response, stream);
    }

    private void deleteOldAvatar(String avatar) throws Exception{
        // 如果 avatar 为 null，那么 avatar.isEmpty() 会抛出 NullPointerException 异常，
        // 因为不能在 null 上调用方法。因此，通常在判断变量是否为空之前，应该先判断它是否为 null。
        if (avatar == null || avatar.isEmpty()) return;
        RemoveObjectArgs remove = RemoveObjectArgs.builder()
                .bucket("study")
                .object(avatar)
                .build();
        client.removeObject(remove);
    }
}
