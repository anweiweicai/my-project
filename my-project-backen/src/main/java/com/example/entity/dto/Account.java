package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data//是 Lombok 提供的注解，它是一个组合注解，包含了`@Getter``@Setter``@ToString``@EqualsAndHashCode` 等注解的功能。使用`@Data` 注解可以自动生成属性的 getter、setter 方法，以及`toString`、`equals`和`hashCode` 方法。
@TableName("db_account")//是 MyBatis-Plus 提供的注解, 用于指定实体类对应的数据库表名。在这个例子中，它指定了实体类`Account` 对应数据库表`db_account`。
@AllArgsConstructor//是 Lombok 提供的注解，它会为类生成一个全参构造方法。也就是用来生成一个包含所有成员变量的构造方法。
public class Account {
    @TableId(type = IdType.AUTO)//是 MyBatis-Plus 提供的注解，用于指定实体类的主键属性，以及主键生成策略。在这个例子中，它指定了`id`属性是主键，并且采用自动增长的方式生成主键值
    Integer id;
    String username;
    String password;
    String email;
    String role;
    String avatar;
    Date   registerTime;
}
//@Data//是 Lombok 提供的注解，它是一个组合注解，包含了`@Getter``@Setter``@ToString``@EqualsAndHashCode` 等注解的功能。使用`@Data` 注解可以自动生成属性的 getter、setter 方法，以及`toString`、`equals`和`hashCode` 方法。
//@TableName("db_account")//是 MyBatis-Plus 提供的注解, 用于指定实体类对应的数据库表名。在这个例子中，它指定了实体类`Account` 对应数据库表`db_account`。
//@AllArgsConstructor//是 Lombok 提供的注解，它会为类生成一个全参构造方法。也就是用来生成一个包含所有成员变量的构造方法。
//public class Account implements BaseData {
//    @TableId(type = IdType.AUTO)//是 MyBatis-Plus 提供的注解，用于指定实体类的主键属性，以及主键生成策略。在这个例子中，它指定了`id`属性是主键，并且采用自动增长的方式生成主键值
//    Integer id;
//    String username;
//    String password;
//    String email;
//    String role;
//    Date   registerTime;
//}
