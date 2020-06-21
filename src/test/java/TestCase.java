import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.*;

public class TestCase
{
    @Test
    public void test1() throws Exception
    {
        Connection connection = new Connection("106.12.89.13");
        connection.connect();
        connection.authenticateWithPublicKey("root", ResourceUtils.getFile("classpath:id_rsa"), null);

        Session sess = connection.openSession();
        sess.execCommand("export TERM=dumb && top -bn 1");
        InputStream inputStream = sess.getStdout();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null)
            System.out.println(line);

    }

    @Test
    public void test2()
    {
        System.out.print(("ss" + System.lineSeparator()).trim());
    }
}
