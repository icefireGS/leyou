package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.properties.JwtProperties;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserClient userClient;

    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    public String authentication(String username, String password) {
        try {
            // 查询用户
            User user = this.userClient.queryUser(username, password);
            if (null == user) {
                logger.info("用户信息不存在，{}", username);
                return null;
            }

            // 生成token
            String token = JwtUtils.generateToken(
                    new UserInfo(user.getId(), user.getUsername()),
                    jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            return token;
        } catch (Exception e) {
            return null;
        }
    }
}