package com.community.supermarket.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class OrderNoGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    private OrderNoGenerator() {}

    public static String generate() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        long seq = SEQUENCE.incrementAndGet() % 10000;
        return "CS" + timestamp + String.format("%04d", seq);
    }
}
