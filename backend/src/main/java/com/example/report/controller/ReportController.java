package com.example.report.controller;

import com.example.report.dto.ReportData;
import com.example.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "*")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/report/json")
    public ReportData getReportJson() {
        log.info("获取报表原始 JSON 数据");
        return reportService.generateMockData();
    }

    /**
     * 调试接口：直接返回渲染后的 HTML（浏览器打开即可预览）
     */
    @GetMapping("/report/html")
    public org.springframework.web.servlet.ModelAndView viewReportHtml() {
        log.info("预览报表 HTML");
        org.springframework.web.servlet.ModelAndView mv = new org.springframework.web.servlet.ModelAndView("report");
        mv.addObject("data", reportService.generateMockData());
        return mv;
    }

    @GetMapping("/report/download")
    public ResponseEntity<byte[]> downloadReport() {
        log.info("一键下载精力配置洞察报告");

        ReportData data = reportService.generateMockData();
        byte[] pdf = reportService.generatePdf(data);
        String filename = encodeFilename("精力配置洞察报告_" + data.getAnalyzedPerson() + ".pdf");

        return buildPdfResponse(pdf, filename);
    }

    private ResponseEntity<byte[]> buildPdfResponse(byte[] pdf, String encodedFilename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);
        headers.setContentLength(pdf.length);
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    private String encodeFilename(String filename) {
        return URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}
