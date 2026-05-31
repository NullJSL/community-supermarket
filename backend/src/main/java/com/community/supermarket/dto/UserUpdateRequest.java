package com.community.supermarket.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String nickname;
    private String phone;
    private String avatarUrl;
    private String address;
}
