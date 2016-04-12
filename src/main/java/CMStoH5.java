import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import redis.clients.jedis.Jedis;
import utils.JSONUtil;
import utils.JedisPoolUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Exception;import java.lang.String;import java.lang.System;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by root on 16-4-11.
 */
public class CMStoH5 {
    public static void main(String[] args) throws Exception{
//        executCMD("sh /opt/test/plus.sh \n");
//        saveToRedis();
    }

    public static Set<String> executCMD(String path) {
        String hostname = "118.118.118.10";
        String username = "root";
        String password = "213456";


        Set<String> result = new HashSet<String>();
        try {
            Connection conn = new Connection(hostname);
            conn.connect();

            boolean isAuthenticated = conn.authenticateWithPassword(username,
                    password);
            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");

            Session sess = conn.openSession();
            sess.execCommand(path);

            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            String size;

            while ((size = br.readLine()) != null) {
                result.add(size);
            }

            sess.close();
            conn.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(2);
        }
        System.out.println(result);
        return result;
    }

    public static void saveToRedis(String id, String dbinfo,String indedomain,String meminfo,String tag) throws Exception{
        Jedis jedis = new JedisPoolUtils().getJedisPool().getResource();
        jedis.select(3);
        Map dataMap = new HashMap<>();
        dataMap.put("id",id);
        dataMap.put("dbinfo",dbinfo);
        dataMap.put("indedomain",indedomain);
        dataMap.put("meminfo",meminfo);
        dataMap.put("tag",tag);
        jedis.hset("sparkcrawl:heartbeat", "id", JSONUtil.object2JacksonString(dataMap));
        System.out.println(jedis.hgetAll("sparkcrawl:heartbeat"));
    }
}
