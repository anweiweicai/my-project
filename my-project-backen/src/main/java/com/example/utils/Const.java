package com.example.utils;
//存储属性
public class Const {
    public static final String JWT_BLACK_LIST = "jwt:blacklist:";
//    在Java中，`final`关键字有不同的作用，取决于它被应用的上下文。在上下文中使用`final`通常表示以下几种含义：
//            1. 不可变性：对于变量来说，使用`final`关键字表示该变量被赋值后不能再被修改。如果对`final`变量再次赋值将导致编译时错误。这是“不可变”的含义。
//            2. 初始化保证：对于实例字段，`final`关键字要求在构造函数结束之前为其赋值。这样可以确保在实例创建后，`final`字段中始终都有值。
//            3. 线程安全：对于参数，`final`关键字可以在方法中保护参数不被修改，这有助于确保线程安全。
    public static final String ATTR_USER_ID = "attr:user:id:";

    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    public static final String VERIFY_EMAIL_DATA = "verify:email:data:";
    public static final int ORDER_CORS = -102;//跨域请求优先级
    public static final int ORDER_LIMIT = -101;//跨域请求优先级

    public static final String FLOW_LIMIT_COUNTER = "flow:counter:";
    public static final String FLOW_LIMIT_BLOCK = "flow:block:";

    public static final String FORUM_WEATHER_CACHE = "weather:cache:";
    public static final String FORUM_IMAGE_COUNTER = "forum:image:counter:";
    public static final String FORUM_TOPIC_CREATE_COUNTER = "forum:topic:create:counter:";
    public static final String FORUM_TOPIC_COMMENT_COUNTER = "forum:topic:create:comment:";
    public static final String FORUM_TOPIC_PREVIEW_CACHE = "forum:topic:preview:cache:";
}
