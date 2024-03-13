package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

@Data
@TableName("db_account_privacy")
public class AccountPrivacy{
    @TableId(type = IdType.AUTO)
    final Integer id;
    boolean phone = true;
    boolean email = true;
    boolean qq = true;
    boolean wx = true;
    boolean gender = true;

    /**
     * 获取隐藏的字段
     * @return
     */
    public String[] hiddenFields() {
        List<String> strings = new LinkedList<>();
        // 获取所有字段
        Field[] fields = this.getClass().getDeclaredFields();
        // 遍历字段
        for (Field field : fields) {
            // 判断字段是否为私有
            try{
                // 如果是私有字段 并且值为true 则隐藏该字段
                if (field.getType().equals(boolean.class) && !field.getBoolean(this)) { // 判断是否为boolean类型并且值为false
                    // 将字段名添加到集合中
                    strings.add(field.getName());
                }
            }catch (Exception ignore){}
        }
        // 将集合转换为数组
        return strings.toArray(String[]::new);
    }
}
