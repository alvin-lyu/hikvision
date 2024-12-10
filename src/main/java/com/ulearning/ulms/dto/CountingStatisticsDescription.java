package com.ulearning.ulms.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author hai.lv
 * @date 2024/12/10
 */
@Data
@XmlRootElement(name = "CountingStatisticsDescription")
public class CountingStatisticsDescription {

    /**
     * 统计类型
     */
    private String statisticType;

    /**
     * 统计维度
     */
    private String reportType;

    /**
     * 时间段列表
     */
    private List<TimeSpan> timeSpanList;

    @Data
    @XmlRootElement(name = "timeSpan")
    public static class TimeSpan {
        private String startTime;
        private String endTime;
    }

}
