package com.liu.kafka.simple;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * properties文件读取工具.
 */
public class PropertiesUtil {
    private static ConcurrentHashMap<String, Properties>
            propertiesInstanceMap = new ConcurrentHashMap<String, Properties>();

    /**
     * 获取字符串类型的值.
     * @param propertiesName  propertiesName
     * @param key key
     * @return
     */
    public static String getStringValue(String propertiesName, String key) {
        String result = null;
        Properties properties = getPropertiesInstance(propertiesName);
        result = properties.getProperty(key);
        return result;
    }

    /**
     * 获取int类型的值.
     * @param propertiesName propertiesName
     * @param key key
     * @return
     */
    public static int getIntValue(String propertiesName, String key) {
        Properties properties = getPropertiesInstance(propertiesName);
        if (properties.contains(key)) {
            return Integer.parseInt(properties.getProperty(key));
        }
        return 0;
    }

    /**
     * 获取long类型的值.
     * @param propertiesName propertiesName
     * @param key key
     * @return
     */
    public static long getLongValue(String propertiesName, String key) {
        Properties properties = getPropertiesInstance(propertiesName);
        if (properties.contains(key)) {
            return Long.parseLong(properties.getProperty(key));
        }
        return 0;
    }

    /**
     * 获取float类型的值.
     * @param propertiesName propertiesName
     * @param key key
     * @return
     */
    public static float getFloatValue(String propertiesName, String key) {
        Properties properties = getPropertiesInstance(propertiesName);
        if (properties.contains(key)) {
            return Float.parseFloat(properties.getProperty(key));
        }
        return 0;
    }

    /**
     * 获取boolean类型的值.
     * @param propertiesName propertiesName
     * @param key key
     * @return
     */
    public static boolean getBooleanValue(String propertiesName, String key) {
        Properties properties = getPropertiesInstance(propertiesName);
        if (properties.contains(key)) {
            return Boolean.parseBoolean(properties.getProperty(key));
        }
        return false;
    }

    /**
     * 获取properties文件对象.
     * @param propertiesName propertiesName
     * @return
     */
    private static Properties getPropertiesInstance(String propertiesName) {
        Properties properties = propertiesInstanceMap.get(propertiesName);
        if (properties == null) {
            properties = new Properties();
            try {
                InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(propertiesName);
                properties.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            propertiesInstanceMap.put(propertiesName, properties);
        }

        return properties;
    }
}
