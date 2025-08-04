package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 请求封装类
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    @Min(value = 1, message = "当前页号不能小于1")
    private int pageNum = 1;

    /**
     * 页面大小
     */
    @Min(value = 1, message = "页面大小不能小于1")
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "desc";
}