package com.example.report.service;

import com.example.report.config.PlaywrightConfig;
import com.example.report.dto.*;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.Margin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.List;

/**
 * 精力配置洞察报告服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final PlaywrightConfig playwrightConfig;
    private final TemplateEngine templateEngine;

    /**
     * 生成模拟精力报告数据
     */
    public ReportData generateMockData() {
        // 图1: 概览数据
        String summaryText = "45%用于会议审批（低效会议占比38%），仅28%聚焦客户开发，与岗位职责要求（战略活动≥40%）严重脱节，导致Q1销售额KPI缺口12%。优化后，预计释放20小时/周用于高价值客户拜访，可提升目标达成率18%。";

        // 图2: 时间分配环形图数据
        List<BusinessItem> businessItems = Arrays.asList(
                BusinessItem.builder().name("业务1").actualValue(25).idealValue(20).build(),
                BusinessItem.builder().name("业务2").actualValue(10).idealValue(15).build(),
                BusinessItem.builder().name("业务3").actualValue(8).idealValue(10).build(),
                BusinessItem.builder().name("业务4").actualValue(2).idealValue(5).build(),
                BusinessItem.builder().name("业务5").actualValue(6).idealValue(8).build()
        );

        // 图2: 职责对比折线图数据
        List<DutyComparison> dutyComparisons = Arrays.asList(
                DutyComparison.builder().dutyName("职责1").actualValue(100).idealValue(150).build(),
                DutyComparison.builder().dutyName("职责2").actualValue(140).idealValue(100).build(),
                DutyComparison.builder().dutyName("职责3").actualValue(230).idealValue(200).build(),
                DutyComparison.builder().dutyName("职责4").actualValue(100).idealValue(140).build(),
                DutyComparison.builder().dutyName("职责5").actualValue(130).idealValue(100).build()
        );

        String keyInsight = "团队管理职责要求35%时间，实际仅22%（缺口13%），因大量时间用于跨部门审批（占总时间28%）。核心业务仅投入15%，合理投入应为25%。";

        // 图3: 问题诊断
        List<ProblemItem> problems = Arrays.asList(
                ProblemItem.builder()
                        .title("时间碎片化")
                        .content("平均会议时长25分钟，但37%为5-10分钟碎片会议（如临时需求确认），导致深度工作被打断，影响任务交付KPI。")
                        .build(),
                ProblemItem.builder()
                        .title("职责错配型浪费")
                        .content("管理者花22%时间处理员工请假审批（本应由HRBP承担），导致战略任务时间被挤压。")
                        .build(),
                ProblemItem.builder()
                        .title("KPI预警")
                        .content("客户开发时间缺口22小时/周，当前Q2销售额KPI缺口12%；若不优化，Q3目标达成率<80%。")
                        .build()
        );

        // 图3: 精力平衡建议
        List<SuggestionItem> suggestions = Arrays.asList(
                SuggestionItem.builder()
                        .title("时间投资建议")
                        .details(Arrays.asList(
                                "根据您的KPI短板（产品创新不足），建议下月将'市场/竞品调研'类日程从5%提升至15%。"
                        ))
                        .build(),
                SuggestionItem.builder()
                        .title("授权/拒绝清单")
                        .details(Arrays.asList(
                                "识别出3个您可以授权给下属处理的例会（如：每日晨会）。",
                                "本周有2个会议您仅为知情者，建议以阅读纪代替参会。"
                        ))
                        .build(),
                SuggestionItem.builder()
                        .title("能量管理提醒")
                        .details(Arrays.asList(
                                "连续工作18天无休息日记录（出差/请假），建议安排调休，避免职业倦怠。"
                        ))
                        .build()
        );

        return ReportData.builder()
                .title("管理者精力配置洞察报告 – 2025年3月")
                .analyzedPerson("张总监")
                .department("销售部")
                .healthScore(85)
                .summaryText(summaryText)
                .businessItems(businessItems)
                .dutyComparisons(dutyComparisons)
                .keyInsight(keyInsight)
                .problems(problems)
                .suggestions(suggestions)
                .disclaimer("内容由AI生成，仅供参考")
                .build();
    }

    /**
     * 使用 Playwright 生成 PDF
     */
    public byte[] generatePdf(ReportData data) {
        log.info("开始生成 PDF 报表: {}", data.getTitle());

        try {
            Context context = new Context();
            context.setVariable("data", data);
            String html = templateEngine.process("report", context);

            Browser browser = playwrightConfig.getBrowser();
            BrowserContext browserContext = browser.newContext();
            Page page = browserContext.newPage();

            page.setContent(html);
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // 等待 ECharts 图表渲染完成
            page.waitForFunction("document.body.getAttribute('data-charts-ready') === 'true'");
            page.waitForTimeout(1000);

            byte[] pdf = page.pdf(new Page.PdfOptions()
                    .setFormat("A4")
                    .setPrintBackground(true)
                    .setMargin(new Margin()
                            .setTop("12mm")
                            .setBottom("12mm")
                            .setLeft("12mm")
                            .setRight("12mm"))
            );

            browserContext.close();

            log.info("PDF 生成成功，大小: {} bytes", pdf.length);
            return pdf;

        } catch (Exception e) {
            log.error("PDF 生成失败", e);
            throw new RuntimeException("PDF 生成失败: " + e.getMessage(), e);
        }
    }
}
