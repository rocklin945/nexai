package com.rocklin.nexai.common.response;

import lombok.Data;

import java.util.List;

/**
 * @ClassName PageResponse
 * @Description 分页响应封装类
 * @Author: rocklin
 * @Date 2025/8/3 22:30
 * @Version 1.0
 */
@Data
public class PageResponse<T> {
    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 是否有下一页
     */
    private boolean hasNext;

    /**
     * 是否有上一页
     */
    private boolean hasPrevious;

    public PageResponse() {
    }

    public PageResponse(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = (int) Math.ceil((double) total / pageSize);
        this.hasNext = pageNum < pages;
        this.hasPrevious = pageNum > 1;
    }
} 