package com.liu.cdh.hive;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期格式转换UDF.
 * 31/Aug/2015:00:04:37 +0800
 */
public class DateTransformUDF extends UDF {
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
    private SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * evaluate.
     * @param text text
     * @return
     */
    public Text evaluate(Text text) {
        if (null == text) {
            return null;
        }
        if (text.toString().trim().length() == 0) {
            return null;
        }
        try {
            Date date = inputDateFormat.parse(text.toString());
            String output = outputDateFormat.format(date);
            return new Text(output);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(new DateTransformUDF().evaluate(new Text("31/Aug/2015:00:04:37 +0800")));
    }

}
