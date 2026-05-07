package com.example.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 精力平衡建议项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionItem {
    private String title;
    private List<String> details;
}
