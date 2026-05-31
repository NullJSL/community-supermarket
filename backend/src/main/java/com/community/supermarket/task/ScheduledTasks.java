package com.community.supermarket.task;

import com.community.supermarket.service.RestockService;
import com.community.supermarket.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final RestockService restockService;
    private final StatsService statsService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void dailySalesStats() {
        log.info("开始生成日销售统计...");
        statsService.generateDailyStats(LocalDate.now().minusDays(1));
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void checkRestock() {
        log.info("开始检查补货提醒...");
        restockService.checkAndGenerateAlerts();
    }

    @Scheduled(cron = "0 0 0 1 3,6,9,12 ?")
    public void seasonalAdjustment() {
        log.info("开始季节性库存调整...");
        statsService.applySeasonalAdjustments();
    }
}
