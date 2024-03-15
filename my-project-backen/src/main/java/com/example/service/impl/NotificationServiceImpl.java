package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Notification;
import com.example.entity.vo.response.NotificationVO;
import com.example.mapper.NotificationMapper;
import com.example.service.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {
    /**
     * 获取用户通知
     * @param uid
     * @return
     */
    @Override
    public List<NotificationVO> findUserNotification(int uid) {
        return this.list(Wrappers.<Notification>query().eq("uid", uid))
                .stream()
                .map(notification -> {
                    NotificationVO vo = new NotificationVO();
                    BeanUtils.copyProperties(notification, vo);
                    return vo;
                })
                .toList();
    }

    /**
     * 删除用户通知
     * @param id
     * @param uid
     */
    @Override
    public void deleteUserNotification(int id, int uid) {
        this.remove(Wrappers.<Notification>query().eq("id", id).eq("uid", uid));
    }

    /**
     * 删除用户所有通知
     * @param uid
     */
    @Override
    public void deleteUserAllNotification(int uid) {
        this.remove(Wrappers.<Notification>query().eq("uid", uid));
    }

    /**
     * 添加通知
     * @param uid
     * @param title
     * @param content
     * @param type
     * @param url
     */
    @Override
    public void addNotification(int uid, String title, String content, String type, String url) {
        Notification notification = new Notification();
        notification.setUid(uid);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setUrl(url);
        this.save(notification);
    }
}
