package com.liu.logmsgcommon.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * jdbc connection util pool.
 * @Auther: liudongfei
 * @Date: 2019/4/29 14:06
 * @Description:
 */
public class C3P0Util {
    private static final Logger logger = LoggerFactory.getLogger(C3P0Util.class);
    private static final ComboPooledDataSource mysqlPoolDataSource = new ComboPooledDataSource("mysql");
    private static final ComboPooledDataSource hivePoolDataSource = new ComboPooledDataSource("hive");

    /**
     * get mysql connection.
     * @return connection
     * @throws Exception e
     */
    public static Connection getMysqlConnection() throws Exception {
        Connection conn = null;
        try {
            conn = mysqlPoolDataSource.getConnection();
        } catch (SQLException e) {
            logger.error("exception occur when get connection", e);
            throw new Exception("error occur when get connection", e);
        }
        return conn;
    }

    /**
     * get hive connection.
     * @return connection
     * @throws Exception e
     */
    public static Connection getHiveConnection() throws Exception {
        Connection conn = null;
        try {
            conn = hivePoolDataSource.getConnection();
        } catch (SQLException e) {
            logger.error("exception occur when get connection", e);
            throw new Exception("error occur when get connection", e);
        }
        return conn;
    }

    /**
     * close connection.
     * @param conn connetion
     * @param pst pst
     * @param rs rs
     * @throws Exception e
     */
    public static void close(Connection conn, PreparedStatement pst, ResultSet rs) throws Exception {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("exception occur when close resultset", e);
                throw new Exception("error occur when close resultset", e);
            }
        }
        if (pst != null) {
            try {
                pst.close();
            } catch (SQLException e) {
                logger.error("exception occur when close statment", e);
                throw new Exception("error occur when close statment", e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("exception occur when close connection", e);
                throw new Exception("error occur when close connection", e);
            }
        }
    }
}
