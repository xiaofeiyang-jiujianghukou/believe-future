package com.believe.common.core.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页返回结果
 * @param <T> 数据类型
 */
@Data
@Accessors(chain = true)
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long pageNum;

    /**
     * 每页大小
     */
    private Long pageSize;

    /**
     * 数据列表
     */
    private List<T> records;

    public PageResult() {
        this.total = 0L;
        this.pageNum = 1L;
        this.pageSize = 10L;
        this.records = Collections.emptyList();
    }

    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
        this.pageNum = 1L;
        this.pageSize = 10L;
    }

    public PageResult(Long total, Long pageNum, Long pageSize, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.records = records;
    }

    // ========== 静态工厂方法 ==========

    public static <T> PageResult<T> empty() {
        return new PageResult<>();
    }

    public static <T> PageResult<T> of(Long total, List<T> records) {
        return new PageResult<>(total, records);
    }

    public static <T> PageResult<T> of(Long total, Long pageNum, Long pageSize, List<T> records) {
        return new PageResult<>(total, pageNum, pageSize, records);
    }

    // ========== 便捷方法 ==========

    /**
     * 是否有数据
     */
    public boolean hasData() {
        return records != null && !records.isEmpty();
    }

    /**
     * 计算总页数
     */
    public Long getTotalPages() {
        if (total == null || pageSize == null || pageSize == 0) {
            return 0L;
        }
        return (total + pageSize - 1) / pageSize;
    }
}