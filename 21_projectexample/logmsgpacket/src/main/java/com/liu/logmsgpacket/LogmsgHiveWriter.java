package com.liu.logmsgpacket;

import com.liu.logmsgcommon.util.YamlConfigUtil;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

/**
 *hive writer.
 * @Auther: liudongfei
 * @Date: 2019/4/11 11:24
 * @Description:
 */
public class LogmsgHiveWriter {
    private static String driverClass = "org.apache.hive.jdbc.HiveDriver";
    private Jedis jedis ;
    private Connection conn;
    private static LogmsgHiveWriter instance;
    private static boolean initialized;

    /**
     * getInstance.
     * @param configMap configMap
     * @return instance
     * @throws ClassNotFoundException e
     */
    public  static LogmsgHiveWriter getInstance(Map configMap) throws ClassNotFoundException, SQLException {
        if (initialized) {
            return instance;
        }
        instance = new LogmsgHiveWriter();
        instance.init(configMap);
        initialized = true;
        return instance;
    }

    private void init(Map configMap) throws ClassNotFoundException, SQLException {
        Class.forName(driverClass);
        String dbUrl = (String) configMap.get("hive.db.url");
        String dbUsername = (String) configMap.get("hive.db.username");
        String dbPassword = (String) configMap.get("hive.db.password");
        String jedisHost = (String) configMap.get("redis.host");
        int jedisPort = Integer.valueOf((String) configMap.get("redis.port"));
        conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        jedis = new Jedis(jedisHost, jedisPort);
    }

    /**
     * load logmsg data from redis.
     * @return is succeed
     * @throws SQLException e
     */
    public int loadLogMsgFromRedis() throws SQLException {
        jedis.select(1);
        String insertSql = "insert into db_hive.log_translink values";
        Set<String> keys = jedis.keys("*");
        StringBuffer sb = new StringBuffer();
        for (String key : keys) {
            String[] split = key.split("_");
            sb.append(",('" + split[0]);
            sb.append("','" + split[1]);
            sb.append("','" + split[2]);
            sb.append("','" + jedis.get(key) + "')");
        }
        String sql = insertSql + sb.toString().replaceFirst(",", "");
        Statement stat = conn.createStatement();
        return stat.executeUpdate(sql);
    }

    /**
     * load translink from redis to hive.
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        Map configMap = YamlConfigUtil.findAndReadConfigFile("LogMsgPacket.yaml", true);
        LogmsgHiveWriter logmsgHiveWriter = LogmsgHiveWriter.getInstance(configMap);
        System.out.println(logmsgHiveWriter.loadLogMsgFromRedis());
    }
}
