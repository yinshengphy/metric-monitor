package com.yinshengphy.conf;

import ch.ethz.ssh2.Connection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

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

        connection.authenticateWithPublicKey(user, ResourceUtils.getFile("classpath:" + privateKeyPath), null);
        return connection;
    }
}
