package com.liu.logmsgcommon.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * properties util.
 * @Auther: liudongfei
 * @Date: 2019/3/12 16:42
 * @Description:
 */
public class PropertiesUtil {
    /**
     * get properties.
     * @param fileName fileName
     * @return
     */
    public static Properties getProperties(String fileName) {
        InputStream stream = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName + ".properties");
        Properties properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    /**
     * get properties from map.
     * @param configMap configMap
     * @return
     */
    public static Properties fromMap(Map configMap) {
        Properties properties = new Properties();
        for (Object key: configMap.keySet()) {
            properties.setProperty((String) key, String.valueOf(configMap.get(key)));

        }
        return properties;
    }


}
