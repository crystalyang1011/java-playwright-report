package com.example.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 职责对比项（折线图数据）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DutyComparison {
    private String dutyName;
    private int actualValue;
    private int idealValue;
}
