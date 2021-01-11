import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.alibaba.fastjson.JSONObject;
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

    @Test
    public void test3()
    {
        String s = "{\"answers\":[{\"answer\":\"business_scenario=国际漫游 ;code=202015\",\"credit\":\"0.81\"," +
                "\"data\":[{\"key\":\"reply_time\",\"value\":\"0.355\",\"value_type\":\"String\"},{\"key\":\"business_scenario\",\"value\":\"国际漫游\",\"value_type\":\"String\"},{\"key\":\"code\",\"value\":\"202015\",\"value_type\":\"String\"}],\"intention\":\"<办理>国际漫游\",\"intention_id\":\"11915665.85\",\"menu_items\":[],\"menu_items_link\":[]},{\"answer\":\"\",\"credit\":\"0.763\",\"data\":[{\"key\":\"reply_time\",\"value\":\"\",\"value_type\":\"String\"},{\"key\":\"business_scenario\",\"value\":\"\",\"value_type\":\"String\"},{\"key\":\"code\",\"value\":\"\",\"value_type\":\"String\"}],\"intention\":\"<办理>流量包语音包订购\",\"intention_id\":\"11915664.85\",\"menu_items\":[],\"menu_items_link\":[]}],\"end_time\":\"2020-12-29 09:22:56\",\"error_code\":\"0\",\"start_time\":\"2020-12-29 09:22:56\",\"uuid\":\"1232323\"}\n";
        JSONObject jsonObject = JSONObject.parseObject(s);
        System.out.println(jsonObject.getJSONArray("answers").size());
    }
}
