package com.community.supermarket.service;

import com.community.supermarket.dto.UserUpdateRequest;
import com.community.supermarket.dto.WxLoginRequest;
import com.community.supermarket.entity.User;

import java.util.Map;

public interface UserService {
    Map<String, Object> wxLogin(WxLoginRequest request);
    Map<String, Object> devLogin(Long userId);
    User getUserById(Long id);
    User updateUser(Long id, UserUpdateRequest request);
}
