package com.ulearning.ulms.dto;

import com.alibaba.fastjson.JSON;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import javax.xml.namespace.QName;
import java.io.StringWriter;

/**
 * @author hai.lv
 * @date 2024/12/10
 */
public class XmlDemo {

    public static void main(String[] args) {

        // testCountingStatisticsDescription();
        testCountingStatisticsResult();

        // String xmlString = getXMLString("/Users/alvin/Downloads/result.xml");
        // System.out.println(xmlString);

    }

    public static void testCountingStatisticsResult() {

        // XML字符串
        String xmlString = getXMLString("/Users/alvin/Downloads/result.xml");

        try {

            // 获取JAXB上下文
            JAXBContext jaxbContext = JAXBContext.newInstance(CountingStatisticsResult.class);

            // 创建Unmarshaller对象
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // 将XML字符串转换为Java对象
            StringReader reader = new StringReader(xmlString);
            CountingStatisticsResult result = (CountingStatisticsResult) unmarshaller.unmarshal(reader);

            // 输出Java对象
            System.out.println(JSON.toJSONString(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testCountingStatisticsDescription() {

        try {
            CountingStatisticsDescription.TimeSpan timeSpan = new CountingStatisticsDescription.TimeSpan();
            timeSpan.setStartTime("2024-12-09T00:00:00");
            timeSpan.setEndTime("2024-12-09T23:59:59");
            CountingStatisticsDescription desc = new CountingStatisticsDescription();
            desc.setStatisticType("enterExitDuplicate");
            desc.setReportType("daily");
            desc.setTimeSpanList(Collections.singletonList(timeSpan));

            // 获取JAXB上下文
            JAXBContext jaxbContext = JAXBContext.newInstance(CountingStatisticsDescription.class);

            // 创建Marshaller对象
            Marshaller marshaller = jaxbContext.createMarshaller();

            // 设置Marshaller属性，使输出格式化
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // 将Java对象转换为XML并输出到控制台
            StringWriter sw = new StringWriter();
            marshaller.marshal(desc, sw);
            System.out.println(sw.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getXMLString(String path) {
        try {
            // 1. 创建TransformerFactory
            TransformerFactory factory = TransformerFactory.newInstance();

            // 2. 创建Transformer
            Transformer transformer = factory.newTransformer();

            // 3. 读取XML文件并转换为字符串
            File xmlFile = new File(path);
            StreamSource source = new StreamSource(xmlFile);

            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);

            // 4. 输出XML字符串
            String xmlString = writer.toString();

            return xmlString;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
