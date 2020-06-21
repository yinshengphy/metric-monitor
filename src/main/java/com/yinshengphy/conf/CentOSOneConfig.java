package com.yinshengphy.conf;

import ch.ethz.ssh2.Connection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.*;

@Configuration
public class CentOSOneConfig
{

    @Value("${bdyum.host}")
    private String host;

    @Value("${bdyum.user}")
    private String user;

    @Value("${bdyum.private-key-path}")
    private String privateKeyPath;

    @Bean("bdyum")
    public Connection getSession() throws Exception
    {
        Connection connection = new Connection(host);
        connection.connect();
//        System.out.println(new ClassPathResource(privateKeyPath).getInputStream().read());
        Reader reader = new InputStreamReader(new ClassPathResource(privateKeyPath).getInputStream());
        StringBuilder builder = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1)
            builder.append((char) ch);

        connection.authenticateWithPublicKey(user, builder.toString().toCharArray(), null);
        return connection;
    }
}
