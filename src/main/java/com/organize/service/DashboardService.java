package com.organize.service;

import com.organize.dto.DashboardDTO;
import com.organize.dto.AppointmentDTO;
import com.organize.dto.TopCustomerDTO;
import com.organize.dto.RecentReviewDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class DashboardService {

    public DashboardDTO getDashboardData() {
        return new DashboardDTO(
            1250000,
            8,
            3,
            "14:00h",
            "Com Maria Clara - Consultoria",
            5,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );
    }
}
