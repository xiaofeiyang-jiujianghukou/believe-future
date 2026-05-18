package com.believe.common.data.config.dynamic;

/**
 * 动态数据源上下文持有者
 * <p>使用 ThreadLocal 存储当前线程需要使用的数据源标识</p>
 */
public class DynamicDataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 数据源标识常量
     */
    public static final String MASTER = "master";
    public static final String SLAVE = "slave";
    public static final String REPORT = "report";

    public static void setDataSourceKey(String key) {
        CONTEXT_HOLDER.set(key);
    }

    public static String getDataSourceKey() {
        return CONTEXT_HOLDER.get();
    }

    public static void useMaster() {
        setDataSourceKey(MASTER);
    }

    public static void useSlave() {
        setDataSourceKey(SLAVE);
    }

    public static void useReport() {
        setDataSourceKey(REPORT);
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}