package com.ulearning.ulms.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;

/**
 * @author hai.lv
 * @date 2024/12/10
 */
@Data
@XmlRootElement(name = "matchElement")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchElement {

    /**
     * 时间段
     */
    @XmlElement(name = "timeSpan", namespace = "http://www.hikvision.com/ver20/XMLSchema")
    private TimeSpan timeSpan;
    /**
     * 进入人数
     */
    @XmlElement(name = "enterCount", namespace = "http://www.hikvision.com/ver20/XMLSchema")
    private Long enterCount;

    /**
     * 离开人数
     */
    @XmlElement(name = "exitCount", namespace = "http://www.hikvision.com/ver20/XMLSchema")
    private Long exitCount;

    /**
     * 通过人数
     */
    @XmlElement(name = "peoplePassingCount", namespace = "http://www.hikvision.com/ver20/XMLSchema")
    private Long peoplePassingCount;

    /**
     * 重复人数
     */
    @XmlElement(name = "duplicatePeopleCount", namespace = "http://www.hikvision.com/ver20/XMLSchema")
    private Long duplicatePeopleCount;

    @Data
    @XmlRootElement(name = "timeSpan")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TimeSpan {
        /**
         * 开始时间
         */
        @XmlElement(name = "startTime", namespace = "http://www.hikvision.com/ver20/XMLSchema")
        private String startTime;

        /**
         * 结束时间
         */
        @XmlElement(name = "endTime", namespace = "http://www.hikvision.com/ver20/XMLSchema")
        private String endTime;
    }

}
