package com.yinshengphy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MetricController
{
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @ResponseBody
    @RequestMapping(value = "/metric/{timestamp}", method = RequestMethod.GET, produces = "application/json")
    public String query(@PathVariable String timestamp)
    {
        return redisTemplate.opsForValue().get(timestamp);
    }
}
