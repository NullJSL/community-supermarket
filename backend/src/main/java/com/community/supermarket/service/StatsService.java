package com.community.supermarket.service;

import com.community.supermarket.vo.SalesReportVO;

import java.time.LocalDate;

public interface StatsService {
    void generateDailyStats(LocalDate date);
    SalesReportVO getSalesReport(LocalDate startDate, LocalDate endDate);
    void applySeasonalAdjustments();
}
