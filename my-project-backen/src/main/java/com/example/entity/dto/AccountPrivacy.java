package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
}
