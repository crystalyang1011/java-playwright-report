package com.example.report.controller;

import com.example.report.dto.ReportData;
import com.example.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 精力配置洞察报告 API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    /**
     * 获取报表数据（JSON，调试用）
     */
    @GetMapping("/report-data")
    public ReportData getReportData() {
        log.info("获取报表数据");
        return reportService.generateMockData();
    }

    /**
     * 一键生成并下载 PDF 报表（推荐）
     */
    @GetMapping("/report/download")
    public ResponseEntity<byte[]> downloadReport() {
        log.info("一键下载精力配置洞察报告");

        ReportData data = reportService.generateMockData();
        byte[] pdf = reportService.generatePdf(data);
        String filename = encodeFilename("精力配置洞察报告_" + data.getAnalyzedPerson() + ".pdf");

        return buildPdfResponse(pdf, filename);
    }

    /**
     * 构建 PDF 响应，处理中文文件名编码
     */
    private ResponseEntity<byte[]> buildPdfResponse(byte[] pdf, String encodedFilename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);
        headers.setContentLength(pdf.length);
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    /**
     * 编码中文文件名（RFC 5987）
     */
    private String encodeFilename(String filename) {
        return URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}
