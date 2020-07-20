package com.liu.java.base.web.netty.https.simple;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/7/31 14:55
 * @Description:
 */
public class MySSLContextFactory {


    /**
     * 获取context.
     * 生成服务端的密钥文件
     * keytool -genkey
     * -keysize 2048         指定密钥长度 缺省值1024
     * -validity 365         有效期为365天
     * -keyalg RSA           指定密钥的算法 (如 RSA DSA（如果不指定默认采用DSA）)
     * -dname “CN=localhost” 指定证书拥有者信息 例如： “CN=名字与姓氏,OU=组织单位名称,O=组织名称,L=城市或区域名称,ST=州或省份名称”
     * -keypass hsc123       指定别名条目的密码(私钥密码)
     * -storepass hsc123     指定密钥库的密码(获取keystore信息所需的密码)
     * -keystore local.jks   指定密钥库的名称
     * @return s
     * @throws Exception e
     */
    public static SSLContext getSslContext() throws Exception {
        // 安全证书密码
        char[] passArray = "hsc123".toCharArray();
        // 输入协议
        SSLContext sslContext = SSLContext.getInstance("TLSv1");
        // 获取keystore
        KeyStore ks = KeyStore.getInstance("JKS");
        //加载keytool生成的服务端安全证书
        FileInputStream inputStream = new FileInputStream("/Users/liudongfei/local.jks");
        //输入服务端安全证书，密钥密码
        ks.load(inputStream, passArray);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        // 输入Keystore和安全证书密码
        kmf.init(ks, passArray);
        sslContext.init(kmf.getKeyManagers(), null, null);
        inputStream.close();
        return sslContext;

    }

    private static String CLIENT_KEY_STORE = "/Users/liudongfei/local.jks";
    private static String CLIENT_TRUST_KEY_STORE = "/Users/liudongfei/local.jks";
    private static String CLIENT_KEY_STORE_PASSWORD = "hsc123";
    private static String CLIENT_TRUST_KEY_STORE_PASSWORD = "hsc123";


    private static String SERVER_KEY_STORE = "/Users/liudongfei/local.jks";
    private static String SERVER_TRUST_KEY_STORE = "/Users/liudongfei/local.jks";
    private static String SERVER_KEY_STORE_PASSWORD = "hsc123";
    private static String SERVER_TRUST_KEY_STORE_PASSWORD = "hsc123";


    private static final String PROTOCOL = "TLSv1";
    private static final SSLContext SERVER_CONTEXT;
    private static final SSLContext CLIENT_CONTEXT;


    /**
     * 构造器.
     * @param type client or server
     * @param keyStore 密钥证书
     * @param keyStorePassword 密钥
     * @param trustKeyStore 信任密钥证书
     * @param trustKeyPassword 信任密钥
     */
    public MySSLContextFactory(String type, String keyStore, String keyStorePassword,
                               String trustKeyStore, String trustKeyPassword) {
        if ("server".equals(type)) {
            SERVER_KEY_STORE = keyStore;
            SERVER_KEY_STORE_PASSWORD = keyStorePassword;
            SERVER_TRUST_KEY_STORE = trustKeyStore;
            SERVER_TRUST_KEY_STORE_PASSWORD = trustKeyPassword;
        } else if ("client".equals(type)) {
            CLIENT_KEY_STORE = keyStore;
            CLIENT_KEY_STORE_PASSWORD = keyStorePassword;
            CLIENT_TRUST_KEY_STORE = trustKeyStore;
            CLIENT_TRUST_KEY_STORE_PASSWORD = trustKeyPassword;
        }
    }

    static {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        SSLContext serverContext;
        SSLContext clientContext;
        try {

            //加载证书库
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(SERVER_KEY_STORE), SERVER_KEY_STORE_PASSWORD.toCharArray());

            //加载信任库
            KeyStore trustKs = KeyStore.getInstance("JKS");
            trustKs.load(new FileInputStream(SERVER_TRUST_KEY_STORE), SERVER_TRUST_KEY_STORE_PASSWORD.toCharArray());

            //创建并初始化证书库工厂
            String alg = KeyManagerFactory.getDefaultAlgorithm();
            //        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(alg);
            kmf.init(ks, SERVER_KEY_STORE_PASSWORD.toCharArray());

            // 创建并初始化信任库工厂
            String alg_trust = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(alg_trust);
            tmf.init(trustKs);

            // Initialize the SSLContext to work with our key managers.
            serverContext = SSLContext.getInstance(PROTOCOL);
            /**
             * init(KeyManager[],TrustManager[],SecureRandom);
             */
            serverContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }

        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(CLIENT_KEY_STORE), CLIENT_KEY_STORE_PASSWORD.toCharArray());
            //加载信任库
            KeyStore trustKs = KeyStore.getInstance("JKS");
            trustKs.load(new FileInputStream(CLIENT_TRUST_KEY_STORE), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());

            //创建并初始化证书库工厂
            String alg = KeyManagerFactory.getDefaultAlgorithm();
            //        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(alg);
            kmf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());

            // 创建并初始化信任库工厂
            String alg_trust = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(alg_trust);
            tmf.init(trustKs);
            /**
             * TLS安全套接字
             * Returns a SSLContext object that implements the specified secure socket protocol.
             */
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext", e);
        }

        SERVER_CONTEXT = serverContext;
        CLIENT_CONTEXT = clientContext;
    }

    public static SSLContext getServerContext() {
        return SERVER_CONTEXT;
    }

    public static SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }
}
