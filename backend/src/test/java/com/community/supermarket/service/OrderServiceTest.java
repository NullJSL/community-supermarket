package com.community.supermarket.service;

import com.community.supermarket.util.OrderNoGenerator;
import com.community.supermarket.util.SeasonUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    @Test
    void testOrderNoGeneration() {
        String orderNo1 = OrderNoGenerator.generate();
        String orderNo2 = OrderNoGenerator.generate();

        assertNotNull(orderNo1);
        assertTrue(orderNo1.startsWith("CS"));
        assertEquals(20, orderNo1.length());
        assertNotEquals(orderNo1, orderNo2);
    }

    @Test
    void testSeasonUtil() {
        assertEquals("SPRING", SeasonUtil.getSeason(LocalDate.of(2024, 4, 1)));
        assertEquals("SUMMER", SeasonUtil.getSeason(LocalDate.of(2024, 7, 15)));
        assertEquals("AUTUMN", SeasonUtil.getSeason(LocalDate.of(2024, 10, 1)));
        assertEquals("WINTER", SeasonUtil.getSeason(LocalDate.of(2024, 1, 15)));

        String currentSeason = SeasonUtil.getCurrentSeason();
        assertNotNull(currentSeason);
        assertTrue(currentSeason.matches("SPRING|SUMMER|AUTUMN|WINTER"));
    }
}
