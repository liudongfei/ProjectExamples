package com.liu.logmsgcommon.util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * yaml config util.
 * @Auther: liudongfei
 * @Date: 2019/3/13 23:15
 * @Description:
 */
public class YamlConfigUtil {
    /**
     * find and read config.
     * @param name name
     * @param mustExist mustExist
     * @return
     */
    public static Map findAndReadConfigFile(String name, boolean mustExist) {
        InputStream configFileInputStream = null;
        boolean configFileIsEmpty = false;
        try {
            configFileInputStream = getConfigFileInputStream(name);
            if (null != configFileInputStream) {
                Yaml yaml = new Yaml(new SafeConstructor());
                Map res = (Map)yaml.load(configFileInputStream);
                if (res != null) {
                    return new HashMap(res);
                } else {
                    configFileIsEmpty = true;
                }
            }
            if (mustExist) {
                if (configFileIsEmpty) {
                    throw new RuntimeException("配置文件为空，不包含任何配置");
                } else {
                    throw new RuntimeException("配置文件不存在");
                }
            } else {
                return new HashMap();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != configFileInputStream) {
                try {
                    configFileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * get config file as inputstream.
     * @param configFilePath configFilePath
     * @return inputstream
     * @throws IOException exception
     */
    public static InputStream getConfigFileInputStream(String configFilePath) throws IOException {
        if (configFilePath == null) {
            throw new IOException("can not find config file, config file is not set!");
        }
        HashSet<URL> urls = new HashSet<>(findResource(configFilePath));
        if (urls.isEmpty()) {
            FileInputStream fileInputStream = new FileInputStream(configFilePath);
            return fileInputStream;
        } else if (urls.size() > 1) {
            throw new IOException("find multi config file!");
        } else {
            URL url = urls.iterator().next();
            return url.openStream();
        }
    }

    /**
     * find resource file.
     * @param name name
     * @return
     */
    public static List<URL> findResource(String name) {
        ArrayList<URL> urls = new ArrayList<>();
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(name);
            while (resources.hasMoreElements()) {
                urls.add(resources.nextElement());
            }
            return urls;
        } catch (Exception e) {
            e.printStackTrace();
            return urls;
        }
    }
}
