package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ImageService;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ObjectController {
    @Resource
    ImageService service;
    @GetMapping("/images/**")
    public void imageFetch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "image/jpeg"); // 设置响应头，指定图片类型
        this.fetchImage(request, response);
    }

    /**
     * 从minio中获取图片
     * @param request 请求
     * @param response 回复
     * @throws Exception
     */
    private void fetchImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 从request的servlet路径中去掉前面的7个字符，得到子路径
        // 例如: http://localhost:8080/images/avatar/7d064e392bc04e04993bd777f4a86355, 前七个字符即/images
        String imagePath = request.getServletPath().substring(7);
        // 获取response的输出流
        ServletOutputStream stream = response.getOutputStream();
        // 如果子路径长度小于等于13
        if (imagePath.length() <= 13){
            // 向输出流中打印失败信息，并设置状态码为404
            stream.println(RestBean.failure(404, "Not found").toString());
        }else {
            try {
                // 调用service的fetchImageFromMinio方法，将图片数据写入输出流
                service.fetchImageFromMinio(stream, imagePath);
                // 设置响应头，指定图片缓存时间为1个月
                response.setHeader("Cache-Control", "max-age=2592000");
            }catch (ErrorResponseException e){
                // 如果捕获到404异常
                if (e.response().code() == 404){
                    // 设置响应状态码为404，并向输出流中打印失败信息
                    response.setStatus(404);
                    stream.println(RestBean.failure(404, "Not found").toString());
                }else {
                    // 记录异常日志
                    log.error("从minio获取图片出现异常: " + e.getMessage(), e);
                }
            }
        }
    }
}
