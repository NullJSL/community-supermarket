package com.community.supermarket.controller;

import com.community.supermarket.common.Result;
import com.community.supermarket.service.StatsService;
import com.community.supermarket.vo.SalesReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "统计分析模块")
@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "销售报表")
    @GetMapping("/sales")
    public Result<SalesReportVO> salesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getSalesReport(startDate, endDate));
    }

    @Operation(summary = "手动生成日统计")
    @PostMapping("/generate")
    public Result<Void> generateStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        statsService.generateDailyStats(date);
        return Result.success();
    }

    @Operation(summary = "手动触发季节性调整")
    @PostMapping("/seasonal-adjust")
    public Result<Void> seasonalAdjust() {
        statsService.applySeasonalAdjustments();
        return Result.success();
    }
}
