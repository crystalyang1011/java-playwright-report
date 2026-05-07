package com.example.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业务时间分配项（环形图数据）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessItem {
    private String name;
    private int actualValue;
    private int idealValue;
}
