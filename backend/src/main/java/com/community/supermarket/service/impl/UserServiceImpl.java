package com.community.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.supermarket.common.BusinessException;
import com.community.supermarket.dto.UserUpdateRequest;
import com.community.supermarket.dto.WxLoginRequest;
import com.community.supermarket.entity.User;
import com.community.supermarket.mapper.UserMapper;
import com.community.supermarket.service.UserService;
import com.community.supermarket.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Value("${app.wechat.app-id}")
    private String appId;

    @Value("${app.wechat.app-secret}")
    private String appSecret;

    @Override
    public Map<String, Object> wxLogin(WxLoginRequest request) {
        String openid = getOpenidFromWechat(request.getCode());

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));

        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname(request.getNickname());
            user.setAvatarUrl(request.getAvatarUrl());
            user.setRole("USER");
            userMapper.insert(user);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public Map<String, Object> devLogin(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = getUserById(id);
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        userMapper.updateById(user);
        return user;
    }

    private String getOpenidFromWechat(String code) {
        try {
            String url = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    appId, appSecret, code);
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            String openid = jsonNode.get("openid").asText();
            if (openid == null || openid.isEmpty()) {
                throw new BusinessException("微信登录失败: " + jsonNode.get("errmsg").asText());
            }
            return openid;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信登录失败", e);
            throw new BusinessException("微信登录失败: " + e.getMessage());
        }
    }
}
