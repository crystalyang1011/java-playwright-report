package com.example.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 精力配置洞察报告数据 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportData {

    /** 报告标题，如：管理者精力配置洞察报告 – 2025年3月 */
    private String title;

    /** 被分析人，如：张总监 */
    private String analyzedPerson;

    /** 部门，如：销售部 */
    private String department;

    /** 精力健康度评分 */
    private int healthScore;

    /** 概览总结文字 */
    private String summaryText;

    /** 时间分配环形图数据 */
    private List<BusinessItem> businessItems;

    /** 职责对比折线图数据 */
    private List<DutyComparison> dutyComparisons;

    /** 关键洞察 */
    private String keyInsight;

    /** 问题诊断列表 */
    private List<ProblemItem> problems;

    /** 精力平衡建议列表 */
    private List<SuggestionItem> suggestions;

    /** 免责声明 */
    private String disclaimer;
}
