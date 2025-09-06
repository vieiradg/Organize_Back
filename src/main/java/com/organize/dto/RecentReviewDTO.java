package com.organize.dto;

public record RecentReviewDTO(
    int rating,
    String customerName,
    String comment
) {}
