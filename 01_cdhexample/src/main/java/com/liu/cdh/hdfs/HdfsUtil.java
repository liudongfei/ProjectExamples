package com.liu.cdh.hdfs;

import com.liu.cdh.util.PropertiesUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HDFS文件系统操作工具类.
 * @author admin
 *
 */
public class HdfsUtil {
    private Configuration conf;
    private FileSystem fs;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader reader;
    private BufferedWriter writer;

    /**
     * 默认构造器.
     */
    public HdfsUtil() {
        if (conf == null) {
            try {
                conf = new Configuration();
                //KerberosUtil.kerberosAuth(conf);
                //System.out.println("kerberosAuth successed!");
                fs = FileSystem.get(conf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取指定路径的HDFS文件的文件输出流.
     *
     * @param dir dir
     * @return
     */
    public InputStream getFileInputStream(String dir) {
        if (dir == null || dir.trim().equals("")) {
            return null;
        }
        if (fs == null) {
            return null;
        }
        try {
            Path path = new Path(dir);
            if (fs.exists(path)) {
                inputStream = fs.open(path);
                return inputStream;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取指定路径的HDFS文件的文件输入流.
     *
     * @param dir dir
     * @param isOverWrite isOverWrite
     * @return
     */
    public OutputStream getFileOutputStream(String dir, boolean isOverWrite) {
        if (dir == null || dir.trim().equals("")) {
            return null;
        }
        if (fs == null) {
            return null;
        }
        try {
            Path path = new Path(dir);
            if (isOverWrite) {
                outputStream = fs.create(path, true);
            } else {
                if (fs.exists(path)) {
                    outputStream = fs.append(path);
                }
            }
            return outputStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取指定的HDFS文件的字符输出流.
     * @param dir dir
     * @return
     */
    public BufferedReader getBufferedReader(String dir) {
        getFileInputStream(dir);
        if (inputStream != null) {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader;
        }
        return null;
    }

    /**
     * 获取指定的HDFS文件的字符输入流.
     * @param dir dir
     * @param isOverWrite isOverWrite
     * @return
     */
    public BufferedWriter getBufferedWriter(String dir, boolean isOverWrite) {
        getFileOutputStream(dir, isOverWrite);
        if (outputStream != null) {
            writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            return writer;
        }
        return writer;
    }

    /**
     * 获取指定路径下的所有文件，包含目录.
     * @param dir dir
     * @return
     */
    public List<Map<String, String>> getFileList(String dir) {
        return getFileList(dir, null);
    }

    /**
     * 获取指定路径下的符合指定格式的文件，包含目录.
     * @param dir dir
     * @param pattern pattern
     * @return
     */
    public List<Map<String, String>> getFileList(String dir, final String pattern) {
        if (dir == null || dir.trim().equals("")) {
            return null;
        }
        if (fs == null) {
            return null;
        }
        try {
            FileStatus[] fileStatuses = null;
            Path path = new Path(dir);
            if (pattern == null) {
                fileStatuses = fs.listStatus(path);
            } else {
                fileStatuses = fs.listStatus(path, new PathFilter() {
                    public boolean accept(Path path) {
                        if (path.getName().contains(pattern)) {
                            return true;
                        }
                        return false;
                    }
                });
            }
            if (fileStatuses.length > 0) {
                List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                Map<String, String> tempMap = null;
                for (FileStatus fileStatus : fileStatuses) {
                    tempMap = new HashMap<String, String>();
                    tempMap.put("fullName", fileStatus.getPath().toString());
                    tempMap.put("shortName", fileStatus.getPath().getName());
                    tempMap.put("blockSize", Long.toString(fileStatus.getBlockSize()));
                    tempMap.put("size", Long.toString(fileStatus.getLen()));
                    result.add(tempMap);
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建指定的目录.
     * @param dir dir
     * @return
     */
    public boolean createDirectory(String dir) {
        if (dir == null || dir.trim().equals("")) {
            return false;
        }
        if (fs == null) {
            return false;
        }
        try {
            Path path = new Path(dir);
            if (fs.exists(path)) {
                return true;
            } else {
                return fs.mkdirs(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除指定文件.
     * @param fileDir fileDir
     * @return
     */
    public boolean deleteFile(String fileDir) {
        if (fileDir == null || fileDir.trim().equals("")) {
            return false;
        }
        if (fs == null) {
            return false;
        }
        try {
            Path path = new Path(fileDir);
            if (fs.exists(path) && fs.isFile(path)) {
                fs.deleteOnExit(path);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除指定目录.
     * @param dir dir
     * @return
     */
    public boolean deleteDir(String dir) {
        if (dir == null || dir.trim().equals("")) {
            return false;
        }
        if (fs == null) {
            return false;
        }
        try {
            Path path = new Path(dir);
            if (fs.exists(path) && fs.isDirectory(path)) {
                return fs.delete(path, true);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 上传本地文件到HDFS文件系统.
     * @param src src
     * @param dst dst
     * @param isDeleteSource isDeleteSource
     * @return
     */
    public boolean uploadFile(String src, String dst, boolean isDeleteSource) {
        if (conf == null || fs == null) {
            return false;
        }
        try {
            return FileUtil.copy(FileSystem.getLocal(conf), new Path(src), fs, new Path(dst),
                    isDeleteSource, true, conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从HDFS文件系统下载文件到本地.
     * @param src src
     * @param dst dst
     * @param isDeleteSource isDeleteSource
     * @return
     */
    public boolean downloadFile(String src, String dst, boolean isDeleteSource) {
        if (conf == null || fs == null) {
            return false;
        }
        try {
            return FileUtil.copy(fs, new Path(src), FileSystem.getLocal(conf), new Path(dst),
                    isDeleteSource, true, conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭数据流.
     * @return
     */
    public boolean close() {
        try {
            if (fs != null) {
                fs.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fs = null;
            inputStream = null;
            outputStream = null;
            reader = null;
            writer = null;
        }
        return true;
    }

    /**
     * 保存日志到hdfs.
     * @param logContent logContent
     * @param filePath filePath
     * @param fileName fileName
     * @return
     */
    public synchronized String savaLogToHDFS(String logContent, String filePath, String fileName) {
        if (logContent == null || logContent.trim().equals("")) {
            return null;
        }
        if (filePath == null || filePath.trim().equals("")) {
            return null;
        }
        if (fileName == null) {
            return null;
        }
        if (fs == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        String dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .format(calendar.getTime()).replaceAll(":", "")
                .replaceAll("\\.", "");
        String date = dayTime.split(" ")[0];
        String time = dayTime.split(" ")[1];
        Float perFileSize = PropertiesUtil.getFloatValue("app.properties", "hdfs.file.log.size");
        Path path = null;
        if (filePath.lastIndexOf("/") == filePath.length()) {
            path = new Path(filePath + date);
        } else {
            path = new Path(filePath + "/" + date);
        }
        try {
            String localIp = InetAddress.getLocalHost().getHostAddress();
            if (!fs.exists(path)) {
                fs.mkdirs(path);
            }
            if (fileName.equals("")) {
                fileName = localIp + time + ".txt";
            } else {
                //if (fs.getFileStatus(new Path(path.toString() + "/" + fileName)).getLen() > perFileSize) {
                //fileName = localIp + time + ".txt";
                //}
                fileName = fileName + "-" + time + ".log";
            }

            getFileOutputStream(path.toString() + "/" + fileName, true);
            outputStream.write(logContent.getBytes("UTF-8"));
            outputStream.flush();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return null;
    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        HdfsUtil hdfsUtil = new HdfsUtil();
        List<Map<String, String>> fileList = hdfsUtil.getFileList("/user");
        System.out.println(fileList);

        String log = "hello world";
        String filePath = "/tmp/log";
        hdfsUtil.savaLogToHDFS(log, filePath, "mylog");
    }
}
