package com.yinshengphy.dao;

import java.util.Map;

public interface MetricDao
{

    //查询指定进程CPU内存信息
    Map<String, Map<String, String>> getMetricInfo(String[] name);

    //查询服务器cpu，内存，磁盘信息
    Map<String, Map<String, String>> getMetricInfo();

    //获取配置端口连接数
    Map<String, String> getPortConnectNum(String[] ports);
}
