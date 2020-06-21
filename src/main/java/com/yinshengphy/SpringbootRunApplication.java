package com.yinshengphy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringbootRunApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SpringbootRunApplication.class, args);
    }
}