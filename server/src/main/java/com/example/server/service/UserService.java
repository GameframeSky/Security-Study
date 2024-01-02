package com.example.server.service;

import com.example.server.entity.Account;
import com.example.server.mapper.UserMapper;
import com.example.server.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author army
 * @date 2023/12/29 15:55
 */

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private RedisUtils redisUtils;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.isEmpty()) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        Account account = userMapper.selectByUsername(username).get(0);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User.withUsername(account.getUsername())
                .password(new BCryptPasswordEncoder().encode(account.getPassword()))
                .roles("user").build();
    }

    public String sendValidateEmail(String email, String sessionId, boolean hasAccount) {
        String key = "email:" + sessionId + ":" + email + ":" + hasAccount;
        if (Boolean.TRUE.equals(redisUtils.hasKey(key))) {
            Long expire = Optional.ofNullable(redisUtils.getExpire(key)).orElse(0L);
            if (expire > 120) {
                return "请求频繁，请稍后再试";
            }
        }
        Account account = userMapper.selectByEmail(email).get(0);
        if (hasAccount && account == null) {
            return "没有此邮件地址的账户";
        }
        if (!hasAccount && account != null) {
            return "此邮箱已被其他用户注册";
        }
        Random random = new Random();
        int code = random.nextInt(899999) + 100000;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("您的验证邮件");
        message.setText("验证码是：" + code);
        try {
            mailSender.send(message);
            redisUtils.set(key, String.valueOf(code), 3);
            return null;
        } catch (MailException e) {
            e.printStackTrace();
            return "邮件发送失败，请坚持邮件地址是否有效";
        }
    }

    public boolean resetPassword(String password, String email) {
        password = encoder.encode(password);
        return userMapper.resetPasswordByEmail(password, email) > 0;
    }

}
