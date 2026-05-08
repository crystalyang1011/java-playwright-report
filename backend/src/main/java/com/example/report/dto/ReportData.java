package com.example.report.dto;

import java.util.List;
import java.util.Map;

public class ReportData {

    private String title;
    private String analyzedPerson;
    private String department;
    private int healthScore;
    private String 精力总结;
    private List<Map<String, Object>> 业务类别占比;
    private List<Map<String, Object>> 职责对比;
    private String 关键洞察;
    private String 问题诊断与风险预警;
    private String 精力平衡建议;

    public ReportData() {}

    public ReportData(String title, String analyzedPerson, String department, int healthScore,
                      String 精力总结, List<Map<String, Object>> 业务类别占比,
                      List<Map<String, Object>> 职责对比,
                      String 关键洞察, String 问题诊断与风险预警,
                      String 精力平衡建议) {
        this.title = title;
        this.analyzedPerson = analyzedPerson;
        this.department = department;
        this.healthScore = healthScore;
        this.精力总结 = 精力总结;
        this.业务类别占比 = 业务类别占比;
        this.职责对比 = 职责对比;
        this.关键洞察 = 关键洞察;
        this.问题诊断与风险预警 = 问题诊断与风险预警;
        this.精力平衡建议 = 精力平衡建议;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAnalyzedPerson() { return analyzedPerson; }
    public void setAnalyzedPerson(String analyzedPerson) { this.analyzedPerson = analyzedPerson; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getHealthScore() { return healthScore; }
    public void setHealthScore(int healthScore) { this.healthScore = healthScore; }

    public String get精力总结() { return 精力总结; }
    public void set精力总结(String 精力总结) { this.精力总结 = 精力总结; }

    public List<Map<String, Object>> get业务类别占比() { return 业务类别占比; }
    public void set业务类别占比(List<Map<String, Object>> 业务类别占比) { this.业务类别占比 = 业务类别占比; }

    public List<Map<String, Object>> get职责对比() { return 职责对比; }
    public void set职责对比(List<Map<String, Object>> 职责对比) { this.职责对比 = 职责对比; }

    public String get关键洞察() { return 关键洞察; }
    public void set关键洞察(String 关键洞察) { this.关键洞察 = 关键洞察; }

    public String get问题诊断与风险预警() { return 问题诊断与风险预警; }
    public void set问题诊断与风险预警(String 问题诊断与风险预警) { this.问题诊断与风险预警 = 问题诊断与风险预警; }

    public String get精力平衡建议() { return 精力平衡建议; }
    public void set精力平衡建议(String 精力平衡建议) { this.精力平衡建议 = 精力平衡建议; }
}
