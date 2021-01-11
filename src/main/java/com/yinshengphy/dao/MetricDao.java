package com.yinshengphy.dao;

import java.util.Map;

public interface MetricDao
{
    //查询服务器cpu，内存，磁盘信息
    Map<String, Map<String, Map<String, String>>> getMetricInfo();
}
