package com.community.supermarket.controller;

import com.community.supermarket.common.Result;
import com.community.supermarket.dto.UserUpdateRequest;
import com.community.supermarket.dto.WxLoginRequest;
import com.community.supermarket.entity.User;
import com.community.supermarket.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "用户模块")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "微信登录")
    @PostMapping("/wx-login")
    public Result<Map<String, Object>> wxLogin(@Valid @RequestBody WxLoginRequest request) {
        return Result.success(userService.wxLogin(request));
    }

    @Operation(summary = "开发环境登录（测试用）")
    @PostMapping("/dev-login")
    public Result<Map<String, Object>> devLogin(@RequestParam Long userId) {
        return Result.success(userService.devLogin(userId));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getUserById(userId));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public Result<User> updateUserInfo(HttpServletRequest request,
                                       @RequestBody UserUpdateRequest updateRequest) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.updateUser(userId, updateRequest));
    }
}
