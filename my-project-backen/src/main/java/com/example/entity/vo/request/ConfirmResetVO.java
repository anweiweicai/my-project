package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor//构造函数
public class ConfirmResetVO {

    @Email
    String email;

    @Length(min = 6, max = 6)
    String code;
}
