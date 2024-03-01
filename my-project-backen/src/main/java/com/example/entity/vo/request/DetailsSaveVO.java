package com.example.entity.vo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class DetailsSaveVO {
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 1, max = 15)
    String username;

    @Min(0)
    @Max(1)
    int gender;

    @Pattern(regexp = "^1\\d{10}$")//中国的手机号
    String phone;

    @Pattern(regexp = "^[1-9]\\d{4,10}$")//QQ号的格式为5-11位数字，且第一位数字不能为0
    String qq;

    @Pattern(regexp = "^[a-zA-Z]([-_a-zA-Z0-9]{5,19})+$")
    String wx;

    @Length(max = 200)
    String desc;
}
