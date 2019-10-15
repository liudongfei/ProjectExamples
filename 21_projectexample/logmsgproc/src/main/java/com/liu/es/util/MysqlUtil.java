package com.liu.es.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * mysql util.
 * @Auther: liudongfei
 * @Date: 2019/4/5 15:52
 * @Description:
 */
public class MysqlUtil {

    /**
     * get mysql connection.
     * @param dbUrl dbUrl
     * @param dbName dbName
     * @param dbPassword dbPassword
     * @return
     */
    public static Connection getConnection(String dbUrl, String dbName, String dbPassword) throws SQLException {
        try {
            // TODO create connection pool
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(dbUrl, dbName, dbPassword);
            return connection;
        } catch (Exception e) {
            throw new SQLException("error when get mysql connection", e);
        }
    }

    /**
     * close connection.
     * @param connection connection
     * @throws SQLException exception
     */
    public static void close(Connection connection) throws SQLException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new SQLException("error when close mysql connection", e);
            }
        }
    }

}
