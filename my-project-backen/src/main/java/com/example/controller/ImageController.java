package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ImageService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Slf4j
@RestController
@RequestMapping("/api/image")
public class ImageController {
    @Resource
    ImageService service;

    @PostMapping("/avatar")
    public RestBean<String> uploadAvatar(@RequestParam("file")MultipartFile file,
                                         // @RequestParam("file") 表示该参数是通过请求中的名称为"file"的表单字段传递过来的数据
                                         // MultipartFile file 表示参数类型为MultipartFile，是Spring框架中用于封装文件上传相关信息的接口类型
                                         // 这个参数用于接收前端上传的文件数据
                                         @RequestAttribute(Const.ATTR_USER_ID) int id,
                                         HttpServletResponse response) throws IOException {
        if (file.getSize() > 1024 * 200) {
            return RestBean.failure(400, "头像图片不能大于200KB");
        }
        log.info("正在进行头像上传操作...");
        String url = service.uploadAvatar(file, id);
        if (url != null) {
            log.info("头像上传成功, 大小:" + file.getSize());
            return RestBean.success(url);
        } else {
            response.setStatus(400);
            return RestBean.failure(400, "头像上传失败, 请联系管理人员!");
        }
    }

    @PostMapping("/cache")
    public RestBean<String> uploadImage(@RequestParam("file")MultipartFile file,
                                        @RequestAttribute(Const.ATTR_USER_ID) int id) throws IOException {
        if (file.getSize() > 1024 * 1024 * 5) {
            return RestBean.failure(400, "图片不能大于5MB");
        }
        log.info("正在进行图片上传操作...");
        String url = service.uploadImage(file, id);
        if (url != null) {
            log.info("图片上传成功, 大小:" + file.getSize());
            return RestBean.success(url);
        } else {
            return RestBean.failure(400, "图片上传失败, 请联系管理人员!");
        }
    }
}
