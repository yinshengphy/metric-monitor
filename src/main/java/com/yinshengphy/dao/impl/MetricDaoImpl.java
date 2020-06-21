package com.yinshengphy.dao.impl;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.yinshengphy.dao.MetricDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MetricDaoImpl implements MetricDao
{
    @Autowired
    private Connection connection;

    //查询指定进程CPU内存信息
    public Map<String, Map<String, String>> getMetricInfo(String[] names)
    {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(names).forEach(n -> builder.append(execShell("ps -aux|grep " + n + "|grep -v grep|awk " +
                "'{print$11,$3,$4}'")));

        String[] lines = builder.toString().split(System.lineSeparator());

        Map<String, Map<String, String>> map = new HashMap<>();

        Arrays.stream(lines).forEach(n ->
        {
            Map<String, String> metricMap = new HashMap<>();
            String[] items = n.split(" ");
            metricMap.put("cpu", items[1]);
            metricMap.put("mem", items[2]);
            map.put(items[0], metricMap);
        });

        return map;
    }

    //查询服务器cpu，内存，磁盘信息
    public Map<String, Map<String, String>> getMetricInfo()
    {
        Map<String, Map<String, String>> map = new HashMap<>();
        //获取用户CPU使用率 用户，系统
        Map<String, String> cpuMap = new HashMap<>();
        String cpuInfoCommand = "export TERM=dumb && top -bn 1 |grep Cpu|awk '{print$2,$4}'";
        String[] cpuItems = execShell(cpuInfoCommand).split(" ");
        cpuMap.put("user", cpuItems[0].trim());
        cpuMap.put("sys", cpuItems[1].trim());
        map.put("cpu", cpuMap);

        //获取内存使用 total，used
        Map<String, String> memMap = new HashMap<>();
        String memInfoCommand = "free -h|grep Mem|awk '{print$2,$3}'";
        String[] memItems = execShell(memInfoCommand).split(" ");
        memMap.put("total", memItems[0].trim());
        memMap.put("used", memItems[1].trim());
        map.put("mem", memMap);

        //获取磁盘使用
        Map<String, String> diskMap = new HashMap<>();
        String diskInfoCommand = "df -P|grep /dev| awk '{total += $2;used += $3};END {print total,used}'";
        String[] diskItems = execShell(diskInfoCommand).split(" ");
        diskMap.put("total", diskItems[0].trim());
        diskMap.put("used", diskItems[1].trim());
        map.put("disk", diskMap);

        return map;
    }

    //获取配置端口连接数
    public Map<String, String> getPortConnectNum(String[] ports)
    {
        return Arrays.stream(ports).collect(Collectors.toMap(n -> n, n -> execShell("netstat -aln |grep ':" + n + " " +
                "'|grep" +
                " " +
                "ESTABLISHED|wc -l").trim()));

    }

    //    执行linux命令
    private String execShell(String command)
    {
        Session session;
        try
        {
            session = connection.openSession();
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
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
