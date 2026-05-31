package com.community.supermarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.supermarket.vo.RestockAlertVO;

import java.util.Map;

public interface RestockService {
    void checkAndGenerateAlerts();
    IPage<RestockAlertVO> listAlerts(String status, int page, int size);
    void processAlert(Long alertId);
    void ignoreAlert(Long alertId);
    Map<String, Object> getRestockAnalysis(Long productId);
}
