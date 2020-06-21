package com.yinshengphy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yinshengphy.dao.MetricDao;
import com.yinshengphy.service.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@PropertySource(value = {"classpath:monitorConfig.properties"})
public class MetricServiceImpl implements MetricService
{
    @Autowired
    private MetricDao metricDao;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Value("#{'${monitor.ports-config}'.split(',')}")
    private String[] ports;

    @Value("#{'${monitor.prosname-config}'.split(',')}")
    private String[] prosNames;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

    @Scheduled(cron = "0 0/1 * * * ?")
    public void recordMetric()
    {
        Map<String, Map<String, Map<String, String>>> map = new HashMap<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        Map<String, Map<String, String>> metricInfo = metricDao.getMetricInfo();
        metricInfo.put("ports", metricDao.getPortConnectNum(ports));
        map.put("sysInfo", metricInfo);
        map.put("procInfo", metricDao.getMetricInfo(prosNames));

        ops.set(format.format(new Date()), JSONObject.toJSONString(map));
    }
}
