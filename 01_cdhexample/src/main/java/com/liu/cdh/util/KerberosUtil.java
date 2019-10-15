package com.liu.cdh.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

/**
 * kerberos认证工具类.
 * @Auther: liudongfei
 * @Date: 2019/6/18 13:41
 * @Description:
 */
public class KerberosUtil {

    /**
     * kerberos认证.
     * @param conf conf
     */
    public static void kerberosAuth(Configuration conf) {
        String domainName = PropertiesUtil
                .getStringValue("app.properties","kerberos.domainname");
        String username = PropertiesUtil
                .getStringValue("app.properties","kerberos.username");
        String keytab = PropertiesUtil
                .getStringValue("app.properties","kerberos.keytab");
        String krb5Path = PropertiesUtil
                .getStringValue("app.properties","kerberos.krb5");

        System.setProperty("java.security.krb5.conf", krb5Path);
        conf.set("hadoop.security.authentication", "Kerberos");
        conf.set("java.security.krb5.relams", domainName);
        try {
            UserGroupInformation.loginUserFromKeytab(username, keytab);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Configuration configuration = HBaseConfiguration.create();
        //configuration.set("hadoop.security.authentication", "Kerberos" );
        //UserGroupInformation. setConfiguration(configuration);
        //UserGroupInformation.loginUserFromKeytab("hdfs_admin@HOPERUN.COM", "/Users/liudongfei/hdfs_admin.keytab");

    }
}
