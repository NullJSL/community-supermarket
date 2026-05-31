package com.community.supermarket.controller;

import com.community.supermarket.common.PageResult;
import com.community.supermarket.common.Result;
import com.community.supermarket.service.RestockService;
import com.community.supermarket.vo.RestockAlertVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "补货提醒模块")
@RestController
@RequestMapping("/admin/restock")
@RequiredArgsConstructor
public class RestockController {

    private final RestockService restockService;

    @Operation(summary = "补货提醒列表")
    @GetMapping("/alerts")
    public Result<PageResult<RestockAlertVO>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(PageResult.of(restockService.listAlerts(status, page, size)));
    }

    @Operation(summary = "处理补货提醒")
    @PutMapping("/alert/{id}/process")
    public Result<Void> process(@PathVariable Long id) {
        restockService.processAlert(id);
        return Result.success();
    }

    @Operation(summary = "忽略补货提醒")
    @PutMapping("/alert/{id}/ignore")
    public Result<Void> ignore(@PathVariable Long id) {
        restockService.ignoreAlert(id);
        return Result.success();
    }

    @Operation(summary = "补货分析报告")
    @GetMapping("/analysis")
    public Result<Map<String, Object>> analysis(@RequestParam Long productId) {
        return Result.success(restockService.getRestockAnalysis(productId));
    }

    @Operation(summary = "手动触发补货检查")
    @PostMapping("/check")
    public Result<Void> manualCheck() {
        restockService.checkAndGenerateAlerts();
        return Result.success();
    }
}
