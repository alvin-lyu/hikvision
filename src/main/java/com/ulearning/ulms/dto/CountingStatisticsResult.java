package com.ulearning.ulms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author hai.lv
 * @date 2024/12/10
 */
@Data
@NoArgsConstructor
@XmlRootElement(name = "CountingStatisticsResult", namespace = "http://www.hikvision.com/ver20/XMLSchema")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountingStatisticsResult {


    /**
     * Response status
     */
    @XmlElement(name = "responseStatus", namespace = "http://www.hikvision.com/ver20/XMLSchema")
    private Boolean responseStatus;

    /**
     * Response status string
     */
    @XmlElement(name = "responseStatusStrg", namespace = "http://www.hikvision.com/ver20/XMLSchema")
    private String responseStatusStrg;

    /**
     * Number of matches
     */
    @XmlElement(name = "numOfMatches", namespace = "http://www.hikvision.com/ver20/XMLSchema")
    private Integer numOfMatches;

    /**
     * Match list
     */
    @XmlElementWrapper(name = "matchList", namespace = "http://www.hikvision.com/ver20/XMLSchema")
    @XmlElement(name = "matchElement", namespace = "http://www.hikvision.com/ver20/XMLSchema")
    private List<MatchElement> matchList;

}
