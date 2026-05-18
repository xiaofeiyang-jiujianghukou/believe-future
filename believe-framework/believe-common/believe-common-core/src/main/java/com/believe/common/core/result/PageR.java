package com.believe.common.core.result;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PageR<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private long timestamp;
    private List<T> data;
    private long total;
    private long page;
    private long size;

    private PageR() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> PageR<T> of(List<T> data, long total, long page, long size) {
        PageR<T> r = new PageR<>();
        r.code = 200;
        r.message = "success";
        r.data = data != null ? data : Collections.emptyList();
        r.total = total;
        r.page = page;
        r.size = size;
        return r;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public long getPage() { return page; }
    public void setPage(long page) { this.page = page; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
}
