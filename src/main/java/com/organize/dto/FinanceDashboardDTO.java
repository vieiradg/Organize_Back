package com.organize.dto;

public record FinanceDashboardDTO(
        long monthlyRevenue,
        long monthlyExpenses,
        long monthlyProfit,
        double revenueGrowthPercent,
        double profitGrowthPercent,
        double averageRevenuePerAppointment,
        int totalAppointments
) {}
