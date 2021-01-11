package com.yinshengphy.dao.impl;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.alibaba.fastjson.JSONObject;
import com.yinshengphy.dao.MetricDao;
import com.yinshengphy.po.InstancePo;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "instances")
@Configuration
public class MetricDaoImpl implements MetricDao
{
    @Setter
    private List<InstancePo> configs;

    //查询服务器cpu，内存，磁盘信息
    public Map<String, Map<String, Map<String, String>>> getMetricInfo()
    {
        Map<String, Map<String, Map<String, String>>> ipMaps = new HashMap<>();

        for (InstancePo n : configs)
        {
            Connection connection = n.getConnection();

            Map<String, Map<String, String>> metricMap = new HashMap<>();
            //获取用户CPU使用率 用户，系统
            Map<String, String> cpuMap = new HashMap<>();
            String cpuInfoCommand = "export TERM=dumb && top -bn 1 |grep Cpu|awk '{print$2,$4}'";
            String[] cpuItems = execShell(connection, cpuInfoCommand).split(" ");
            cpuMap.put("user", cpuItems[0].trim());
            cpuMap.put("sys", cpuItems[1].trim());
            metricMap.put("cpu", cpuMap);

            //获取内存使用 total，used
            Map<String, String> memMap = new HashMap<>();
            String memInfoCommand = "free -h|grep Mem|awk '{print$2,$3/$2*100\"%\"}'";
            String[] memItems = execShell(connection, memInfoCommand).split(" ");
            memMap.put("total", memItems[0].trim());
            memMap.put("percentage", memItems[1].trim());
            metricMap.put("mem", memMap);

            //获取磁盘使用
            Map<String, String> diskMap = new HashMap<>();
            String diskInfoCommand = "df -P|grep /dev/sdb1| awk '{total += $2;used += $3;percentage=used/total*100\"%\"};END " +
                    "{print total,percentage}'";
            String[] diskItems = execShell(connection, diskInfoCommand).split(" ");
            diskMap.put("total", diskItems[0].trim());
            diskMap.put("percentage", diskItems[1].trim());
            metricMap.put("disk", diskMap);

            //获取语义平台接口工作状态
            Map<String, String> interfaceMap = new HashMap<>();
            String interfaceInfoCommand = "curl --header \"Content-Type:application/json\" 'http://localhost:8082/nlu_service_interface/get_interface?userid=123456&question=%E5%BC%80%E9%80%9A%E5%9B%BD%E9%99%85%E6%BC%AB%E6%B8%B8%E6%B5%81%E9%87%8F%E5%8C%85&business=SHDX&uuid=1232323&function=3'";
            long startTime = System.currentTimeMillis();
            String result = execShell(connection, interfaceInfoCommand);
            long endTime = System.currentTimeMillis();
            interfaceMap.put("timeSpend", String.valueOf((endTime - startTime) / 1000));
            try
            {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getJSONArray("answers").size() != 0)
                    interfaceMap.put("available", "true");
                else
                    interfaceMap.put("available", "false");
            } catch (Exception e)
            {
                interfaceMap.put("available", "false");
            }

            metricMap.put("interface", interfaceMap);

            ipMaps.put(n.getHost(), metricMap);
        }

        return ipMaps;
    }

    //    执行linux命令
    private String execShell(Connection connection, String command)
    {
        Session session;
        try
        {
            session = connection.openSession();
        } catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }
        InputStream inputStream = session.getStdout();
        try
        {
            session.execCommand(command);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS, 30000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = "";
        StringBuilder builder = new StringBuilder();
        while (true)
        {
            try
            {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            builder.append(line);
            builder.append(System.lineSeparator());
        }

        session.close();
        return builder.toString();
    }
}
