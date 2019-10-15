package com.liu.logmsgproc.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.bean.LogMsgParserResult;
import com.liu.logmsgproc.bean.LogDomainBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http json translog parser.
 * @Auther: liudongfei
 * @Date: 2019/4/19 14:35
 * @Description:
 */
public class HttpJsonParser implements LogParser {
    @Override
    public LogMsgParserResult parse(
            List<LogDomainBean> logDefRule, KafkaEventBean msg, LogMsgParserResult parserResult) {
        try {
            Map<String, String> map = new HashMap<>();
            String msg1 = new String(msg.getDataContent());
            String regStr = "(\\{[\\d\\D]*?\\}\\n)";
            Pattern pattern = Pattern.compile(regStr);
            Matcher matcher = pattern.matcher(msg1);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                sb.append(matcher.group(0));
            }
            String msg2 = sb.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readValue(msg2, JsonNode.class);
            Iterator fits =  rootNode.fieldNames();
            while (fits.hasNext()) {
                Object obj = fits.next();
                JsonNode object = rootNode.get(obj.toString());
                Iterator sits = object.fieldNames();
                while (sits.hasNext()) {
                    String secondobj = sits.next().toString();
                    StringBuilder tag = new StringBuilder();
                    tag.append(obj);
                    tag.append(".");
                    tag.append(secondobj);
                    String tagStr = tag.toString();
                    Object tmp = object.get(secondobj);
                    if (tmp instanceof TextNode) {
                        map.put(tagStr, ((TextNode)tmp).textValue());
                    } else  {
                        JsonNode tobj = (JsonNode) tmp;
                        Iterator tobject = tobj.fieldNames();
                        while (tobject.hasNext()) {
                            String fobj = tobject.next().toString();
                            Object fv = tobj.get(fobj);
                            fobj = tagStr + "." + fobj;
                            map.put(fobj, ((TextNode)fv).textValue());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            parserResult.setSucceed(false);
        }
        parserResult.setSucceed(true);
        parserResult.setSourceMsgBean(msg);
        return parserResult;
    }

    public static void main(String[] args) throws IOException {

    }
}
