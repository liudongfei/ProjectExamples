package com.liu.logmsgcollect;

import com.liu.logmsgcommon.util.YamlConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * produce translog from impala.
 * @Auther: liudongfei
 * @Date: 2019/4/19 13:36
 * @Description:
 */
public class ImpalaTransLogProducer {

    /**
     * get connection.
     * @param dbUrl dbUrl
     * @param dbUsername dbUsername
     * @param dbPassword dbPassword
     * @return
     */
    public Connection getConnection(String driverName, String dbUrl, String dbUsername, String dbPassword) {
        Connection conn = null;
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * query db with sql.
     * @param conn conn
     * @param sql sql
     * @return
     */
    public ResultSet query(Connection conn, String sql) {
        if (conn == null) {
            return null;
        }

        Statement stat = null;
        ResultSet res = null;
        try {
            stat = conn.createStatement();
            res = stat.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * insert db with sql.
     * @param conn conn
     * @param sql sql
     */
    public void insert(Connection conn, String sql) {
        if (conn == null) {
            return;
        }

        Statement stat = null;
        int res = 0;
        try {
            stat = conn.createStatement();
            res = stat.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * .
     * @param args args
     * @throws SQLException e
     */
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String configPath = "LogMsgCollect.yaml";
        Map configMap = YamlConfigUtil.findAndReadConfigFile(configPath, true);
        String impalaDriver = (String) configMap.get("impala.driver");
        String impalaUrl = (String) configMap.get("impala.url");
        String impalaUsername = (String) configMap.get("impala.username");
        String impalaPassword = (String) configMap.get("impala.password");
        ImpalaTransLogProducer impalaTransLogProducer = new ImpalaTransLogProducer();
        Connection conn = impalaTransLogProducer.getConnection(impalaDriver, impalaUrl, impalaUsername, impalaPassword);
        ResultSet res = impalaTransLogProducer.query(conn, "show databases");
        while (res.next()) {
            System.out.println(res.getString(1));
        }
    }
}
