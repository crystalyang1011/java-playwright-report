package com.example.report.service;

import com.example.report.config.PlaywrightConfig;
import com.example.report.dto.ReportData;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.Margin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final PlaywrightConfig playwrightConfig;
    private final TemplateEngine templateEngine;

    public ReportService(PlaywrightConfig playwrightConfig, TemplateEngine templateEngine) {
        this.playwrightConfig = playwrightConfig;
        this.templateEngine = templateEngine;
    }

    public ReportData generateMockData() {
        List<Map<String, Object>> 业务类别占比 = Arrays.asList(
                Map.of("外部商务", 38.5),
                Map.of("技术讨论/研讨", 32.7),
                Map.of("其他", 28.8)
        );

        List<Map<String, Object>> 职责对比 = Arrays.asList(
                Map.of("name", "职责1", "actual", 100, "ideal", 150),
                Map.of("name", "职责2", "actual", 140, "ideal", 100),
                Map.of("name", "职责3", "actual", 230, "ideal", 200),
                Map.of("name", "职责4", "actual", 100, "ideal", 140),
                Map.of("name", "职责5", "actual", 130, "ideal", 100)
        );

        String 精力总结 = "当前精力投入中，外部商务类事务占比最高（约38.5%），主要为接待王总、张总、李总及华东集团等高频客户来访；技术讨论/研讨类事务次之（约32.7%），集中于团队协作与项目推进；其他类事务（如模糊标题日程）占约28.8%。该分布与开发工程师岗位核心职责存在显著偏差：外部商务本应属低相关度事务，而高价值的技术开发、代码质量保障、系统稳定性优化等高相关度活动未在日程中体现，表明实际精力分配严重偏离岗位主责。";

        String 关键洞察 = "团队管理职责要求35%时间，实际仅22%（缺口13%），因大量时间用于跨部门审批（占总时间28%）。核心业务仅投入15%，合理投入应为25%。";

        String 问题诊断与风险预警 = "1、外部商务事务过度挤占核心开发时间，存在职责错位与精力稀释风险；2、高相关度职责如代码质量保障、系统稳定性优化、学习成长等完全缺失日程映射，反映关键能力投入缺位与长期发展隐患；3、大量「日程」「哈哈」等模糊标题事务占比近三成，暴露日程管理粗放、目标导向薄弱、事务价值评估机制缺失问题。";

        String 精力平衡建议 = "1、立即建立日程准入审核机制，对外部接待类事务设定审批阈值，非战略级客户来访一律转交客户成功或BD部门承接；2、强制将每日上午2小时设为'深度开发黄金时段'，屏蔽会议与接待，专注编码、评审与技术攻坚，并纳入个人OKR跟踪。";

        ReportData data = new ReportData();
        data.setTitle("管理者精力配置洞察报告 – 2025年3月");
        data.setAnalyzedPerson("张总监");
        data.setDepartment("销售部");
        data.setHealthScore(85);
        data.set精力总结(精力总结);
        data.set业务类别占比(业务类别占比);
        data.set职责对比(职责对比);
        data.set关键洞察(关键洞察);
        data.set问题诊断与风险预警(问题诊断与风险预警);
        data.set精力平衡建议(精力平衡建议);
        return data;
    }

    public byte[] generatePdf(ReportData data) {
        log.info("开始生成 PDF 报表: {}", data.getTitle());

        try {
            Context context = new Context();
            context.setVariable("data", data);
            String html = templateEngine.process("report", context);

            Browser browser = playwrightConfig.getBrowser();
            BrowserContext browserContext = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(1400, 900)
                    .setDeviceScaleFactor(2.0));
            Page page = browserContext.newPage();

            page.setContent(html);
            page.waitForLoadState(LoadState.NETWORKIDLE);

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
