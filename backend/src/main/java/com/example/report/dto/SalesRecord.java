package com.example.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 销售记录 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesRecord {

    private Integer id;
    private String date;
    private String product;
    private String category;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
}
