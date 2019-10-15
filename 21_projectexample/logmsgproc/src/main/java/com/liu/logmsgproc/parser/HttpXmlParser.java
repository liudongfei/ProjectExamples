package com.liu.logmsgproc.parser;

import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.bean.LogMsgParserResult;
import com.liu.logmsgproc.bean.LogDomainBean;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;


/**
 * http xml translog parser.
 * @Auther: liudongfei
 * @Date: 2019/4/19 14:36
 * @Description:
 */
public class HttpXmlParser implements LogParser {
    @Override
    public LogMsgParserResult parse(
            List<LogDomainBean> logDefRule, KafkaEventBean msg, LogMsgParserResult logMsgParserResult) {

        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            byte[] content = msg.getDataContent();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new InputStreamReader(
                    new ByteArrayInputStream(content), "utf-8"));
            LinkedList<String> curXmlPath = new LinkedList<>();
            while (xmlStreamReader.hasNext()) {
                int eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLEvent.START_ELEMENT:
                        String localName = xmlStreamReader.getLocalName();
                        System.out.println(localName);
                        curXmlPath.add(localName);
                        break;
                    case XMLEvent.CHARACTERS:
                        if (!curXmlPath.isEmpty()) {
                            StringBuffer stringBuffer = new StringBuffer();
                            for (String path : curXmlPath) {
                                stringBuffer.append("/");
                                stringBuffer.append(path);
                            }
                            String text = xmlStreamReader.getText();
                            System.out.println(stringBuffer.toString() + ":\t" + text);
                        }
                        break;
                    case XMLEvent.END_ELEMENT:
                        curXmlPath.removeLast();
                        break;
                    default:
                        break;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        String content = "<note>\n"
                + "<to>Tove</to>\n"
                + "<from>Jani</from>\n"
                + "<heading>Reminder</heading>\n"
                + "<body>Don't forget me this weekend!</body>\n"
                + "</note>";
        KafkaEventBean kafkaEventBean = new KafkaEventBean();
        kafkaEventBean.setDataContent(content.getBytes(Charset.forName("utf-8")));
        HttpXmlParser httpXmlParser = new HttpXmlParser();
        httpXmlParser.parse(null, kafkaEventBean, null);

    }
}
