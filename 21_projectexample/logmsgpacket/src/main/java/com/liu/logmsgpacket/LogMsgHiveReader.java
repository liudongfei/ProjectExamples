package com.liu.logmsgpacket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * logmsg hive reader.
 * @Auther: liudongfei
 * @Date: 2019/4/24 14:43
 * @Description:
 */
public class LogMsgHiveReader {
    private static String driverClass = "org.apache.hive.jdbc.HiveDriver";
    private Connection conn;
    private static LogMsgHiveReader instance;
    private static boolean initialized;

    /**
     * get instance.
     * @param configMap configMap
     * @return
     */
    public static LogMsgHiveReader getInstance(Map configMap) throws SQLException, ClassNotFoundException {
        if (initialized) {
            return instance;
        }
        instance = new LogMsgHiveReader();
        instance.init(configMap);
        initialized = true;
        return instance;
    }

    private void init(Map configMap) throws SQLException, ClassNotFoundException {
        Class.forName(driverClass);
        String dbUrl = (String) configMap.get("hive.db.url");
        String dbUsername = (String) configMap.get("hive.db.username");
        String dbPassword = (String) configMap.get("hive.db.password");
        conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    /**
     * get translink List.
     * @param sql sql
     * @return list
     * @throws SQLException e
     */
    public List<String> getTranslinkList(String sql) throws SQLException {
        List<String> translinkList = new ArrayList<>();
        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery(sql);
        if (rs.next()) {
            String translinked_id = rs.getString("translinked_id");
            translinkList.add(translinked_id);
        }
        return translinkList;
    }


}
