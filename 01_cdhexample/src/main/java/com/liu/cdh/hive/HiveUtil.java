package com.liu.cdh.hive;

import com.liu.cdh.util.PropertiesUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * HIVE表操作工具类.
 */
public class HiveUtil {
    private static String driverName = PropertiesUtil
            .getStringValue("app.properties","hive.driver");
    private static String databaseUrl = PropertiesUtil
            .getStringValue("app.properties", "hive.url");
    private static String databaseUserName = PropertiesUtil
            .getStringValue("app.properties", "hive.username");
    private static String databasePassword = PropertiesUtil
            .getStringValue("app.properties", "hive.password");

    /**
     * 获取数据库连接.
     * @throws SQLException e
     * @throws ClassNotFoundException e
     */
    public static Connection getConn() throws SQLException, ClassNotFoundException {
        Class.forName(driverName);
        Connection conn = DriverManager.getConnection(databaseUrl, databaseUserName, databasePassword);
        return conn;
    }

    /**
     * 删除指定的表.
     * @param stmt stmt
     * @param tableName tableName
     * @return
     */
    public static String dropTable(Statement stmt, String tableName) throws SQLException {
        String sql = "drop table " + tableName;
        stmt.executeQuery(sql);
        return tableName;
    }

    /**
     * 创建表.
     * @param stmt stmt
     * @throws SQLException e
     */
    public static void creatTable(Statement stmt, String sql) throws SQLException {
        stmt.executeQuery(sql);
    }

    /**
     * 显示指定的表.
     * @param stmt stmt
     * @param tableName tableName
     * @throws SQLException e
     */
    public static void showTables(Statement stmt, String tableName) throws SQLException {
        String sql = "show tables '" + tableName + "'";
        System.out.println("Running:" + sql);
        ResultSet res = stmt.executeQuery(sql);
        System.out.println("执行 show tables 运行结果:");
        if (res.next()) {
            System.out.println(res.getString(1));
        }
    }

    /**
     * 表的描述.
     * @param stmt stmt
     * @param tableName tableName
     * @throws SQLException e
     */
    public static void describeTables(Statement stmt, String tableName) throws SQLException {
        String sql = "describe " + tableName;
        System.out.println("Running:" + sql);
        ResultSet res = stmt.executeQuery(sql);
        System.out.println("执行 describe table 运行结果:");
        while (res.next()) {
            System.out.println(res.getString(1) + "\t" + res.getString(2));
        }
    }

    /**
     * 加载数据.
     * @param stmt stmt
     * @param sql sql
     * @throws SQLException e
     */
    public static void loadData(Statement stmt, String sql) throws SQLException {
        System.out.println("Running:" + sql);
        ResultSet res = stmt.executeQuery(sql);
    }

    /**
     * 查询数据.
     * @param stmt stmt
     * @param sql sql
     * @throws SQLException e
     */
    public static void selectData(Statement stmt, String sql) throws SQLException {
        System.out.println("Running:" + sql);
        ResultSet res = stmt.executeQuery(sql);
        System.out.println("执行 select * query 运行结果:");
        while (res.next()) {
            System.out.println(res.getInt(1) + "\t" + res.getString(2));
        }
    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        try {
            Connection conn = HiveUtil.getConn();
            Statement stmt = conn.createStatement();
            HiveUtil.selectData(stmt, "select * from employees.employees limit 5");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
