package com.liu.spark.streaming;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2018/12/8 16:56
 * @Description:
 */
public class ConnectionPool {
    private static LinkedList<Connection> connectionQueue = null;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * main.
     * @return conn
     * @throws SQLException e
     */
    public synchronized static Connection getConnection() throws SQLException {
        if (connectionQueue == null) {
            connectionQueue = new LinkedList<>();
            for (int i = 1; i <= 10; i++) {
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/spark", "root", "123456");
                connectionQueue.push(conn);
            }
        }
        return connectionQueue.poll();
    }

    public static void returnConnection(Connection conn) {
        connectionQueue.push(conn);
    }
}
