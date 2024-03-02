package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.entity.vo.request.ModifyEmailVO;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
//    当您使用`UserDetailsService`加载用户信息时，Spring Security 框架会自动应用密码解密和比较逻辑。
//    因此，在`loadUserByUsername` 方法中返回的`UserDetails` 对象中包含的密码会自动进行解码和比较。
//    Spring Security会自动根据其内部配置应用相应的密码解码器（如`BCryptPasswordEncoder`）来比较存储在数据库中的密码和用户提供的密码。
    @Resource
    FlowUtils utils;

    @Resource
    AmqpTemplate amqpTemplate;// RabbitMQ

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    PasswordEncoder encoder;

    private String codeRedisKey(String email){//获取redis中存的对应Email的验证码
        return Const.VERIFY_EMAIL_DATA + email;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(username);
        if (account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    public Account findAccountByNameOrEmail(String text){
        return this.query()
                .eq("username",text).or()
                .eq("email",text)
                .one();
    }

    @Override
    public Account findAccountById(int id) {
        return this.query().eq("id", id).one();
    }

    @Override
    public String registerEmailVerifyCode(String email, String type, String ip) {
        synchronized (ip.intern()){
            //这是一个同步块, 在多线程环境中保证对某个代码块的互斥访问，以避免多个线程同时访问可能导致的数据安全问题
            //intern()方法会尝试将字符串对象放入字符串常量池中，并返回常量池中字符串对象的引用,对于相同值的字符串，不同的字符串对象可以共享同一个引用
            //在这个方法中, 就是同一ip地址返回的引用是相同的, 可以实现同一ip地址请求的互斥访问
            if (!this.verifyLimit(ip)){
                return "请求频繁, 请稍后再试";
            }
            Random random = new Random();//生成随机数
            int code = random.nextInt(899999) + 100000;//生成一个介于 100000 到 999999 之间的随机整数, 即6位数
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);//存储验证信息
            amqpTemplate.convertAndSend("mail", data);//是将消息`data` 发送到名为 "mail" 的消息队列中, Spring 会自动将这个对象转换成消息格式，并发送到指定的队列中
            stringRedisTemplate.opsForValue()//缓存验证码的信息
                    .set(codeRedisKey(email), String.valueOf(code), 3, TimeUnit.MINUTES);//3分钟有效
            return null;
        }
    }

    private boolean verifyLimit(String ip){
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return utils.limitOnceCheck(key, 60);
    }

    @Override
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        String username = vo.getUsername();
//        String code =  stringRedisTemplate.opsForValue().get(codeRedisKey(email));
        String code = getEmailVerifyCode(email);
        if (code == null) return "请先获取验证码";
        if (!code.equals(vo.getCode())) return "验证码输入错误, 请重新输入";
        if (this.existsAccountByEmail(email)) return "此电子邮件已被其他用户注册";
        if (this.existsAccountByUsername(username)) return "此用户名已被其他用户注册";
        String password = encoder.encode(vo.getPassword());
        Account account = new Account(null, username, password, email, "user", new Date());
        if (this.save(account)){
            stringRedisTemplate.delete(codeRedisKey(email));
            return null;
        }else {
            return "内部错误, 请联系管理员";
        }
    }

    private boolean existsAccountByEmail(String email){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email",email));
    }
    private boolean existsAccountByUsername(String username){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username",username));
    }


    @Override
    public String resetConfirm(ConfirmResetVO vo) {
        String email = vo.getEmail();
        if (!this.existsAccountByEmail(email)){
            return "用户不存在, 请重新输入";
        }else {
            String code = stringRedisTemplate.opsForValue().get(codeRedisKey(email));
            if (code == null) return "请先获取验证码";
            if (!code.equals(vo.getCode())) return "验证码输入错误, 请重新输入";
            return null;
        }
    }

    @Override
    public String resetEmailAccountPassword(EmailResetVO vo) {
        String email = vo.getEmail();
        String verify = this.resetConfirm(new ConfirmResetVO(vo.getEmail(), vo.getCode()));
        if (verify != null) return verify;
        String password = encoder.encode(vo.getPassword());
        boolean update = this.update().eq("email", email).set("password", password).update();
        if (update){
//            stringRedisTemplate.delete(codeRedisKey(email));
            deleteEmailVerifyCode(email);
        }
        return null;
    }

    /**
     * 修改电子邮件
     * @param id
     * @param vo
     * @return
     */
    @Override
    public String modifyEmail(int id, ModifyEmailVO vo) {
        String email = vo.getEmail();
        String code = getEmailVerifyCode(email);
        if (code == null) return "请先获取验证码!";
        if (!code.equals(vo.getCode())) return "验证码错误, 请重新输入";
        this.deleteEmailVerifyCode(email);
        Account account = findAccountByNameOrEmail(email);
        if (account != null && account.getId() != id){
            return "该电子邮件已经被其它账号绑定";
        }
        this.update()
                .set("email", email)
                .eq("id", id)
                .update();
        return null;
    }

    /**
     * 删除redis中对应邮件的验证码缓存
     * @param email
     */
    private void deleteEmailVerifyCode(String email){
        stringRedisTemplate.delete(codeRedisKey(email));
    }

    /**
     * 获取redis中对应邮件的验证码
     * @param email
     * @return
     */
    private String getEmailVerifyCode(String email){
        return stringRedisTemplate.opsForValue().get(codeRedisKey(email));
    }
}
