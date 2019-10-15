package com.liu.cdh.hive;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * 取出字段中双引号"的UDF.
 */
public class RemoveQuotesUDF extends UDF {

    /**
     * UDF处理逻辑.
     * @param str str
     * @return
     */
    public Text evaluate(Text str) {
        if (null == str) {
            return null;
        }
        return new Text(str.toString().replaceAll("\"", ""));
    }
}
