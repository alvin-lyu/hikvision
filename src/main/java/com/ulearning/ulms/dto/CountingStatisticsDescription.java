package com.ulearning.ulms.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author hai.lv
 * @date 2024/12/10
 */
@Data
@XmlRootElement(name = "CountingStatisticsDescription")
@XmlAccessorType(XmlAccessType.FIELD)
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
    @XmlElementWrapper(name = "timeSpanList")
    @XmlElement(name = "timeSpan")
    private List<TimeSpan> timeSpanList;

    @Data
    @XmlRootElement(name = "timeSpan")
    public static class TimeSpan {
        /**
         * 开始时间
         */
        private String startTime;

        /**
         * 结束时间
         */
        private String endTime;
    }

}
