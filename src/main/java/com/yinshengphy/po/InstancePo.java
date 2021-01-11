package com.yinshengphy.po;

import ch.ethz.ssh2.Connection;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Data
public class InstancePo
{
    private String host;

    private String user;

    private String passwd;

    private Connection connection;

    public Connection getConnection()
    {
        if (connection == null)
        {
            Connection connection = new Connection(host);
            try
            {
                connection.connect();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                connection.authenticateWithPassword(user, passwd);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            this.connection = connection;
            return connection;
        } else return connection;
    }
}
