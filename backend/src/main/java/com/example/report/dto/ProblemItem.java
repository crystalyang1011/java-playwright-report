package com.example.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 问题诊断项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemItem {
    private String title;
    private String content;
}
