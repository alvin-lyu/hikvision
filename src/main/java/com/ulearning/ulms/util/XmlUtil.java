package com.ulearning.ulms.util;

import com.ulearning.ulms.dto.CountingStatisticsDescription;
import com.ulearning.ulms.dto.CountingStatisticsResult;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author hai.lv
 * @date 2024/12/10
 */
public class XmlUtil {

    public static CountingStatisticsResult xmlToCountingStatisticsResult(String xmlString) {
        try {
            // 获取JAXB上下文
            JAXBContext jaxbContext = JAXBContext.newInstance(CountingStatisticsResult.class);

            // 创建Unmarshaller对象
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // 将XML字符串转换为Java对象
            StringReader reader = new StringReader(xmlString);
            return (CountingStatisticsResult) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String countingStatisticsDescriptionToXml(CountingStatisticsDescription desc) {

        try {
            // 获取JAXB上下文
            JAXBContext jaxbContext = JAXBContext.newInstance(CountingStatisticsDescription.class);
            // 创建Marshaller对象
            Marshaller marshaller = jaxbContext.createMarshaller();
            // 设置Marshaller属性，使输出格式化
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // 将Java对象转换为XML并输出到控制台
            StringWriter sw = new StringWriter();
            marshaller.marshal(desc, sw);
            return sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
