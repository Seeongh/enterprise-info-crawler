package com.antock.enterprise_info_crawler.api.coseller.application.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CorpApiJsonResponse {

    private String ResultCode;
    private String resultMsg;
    private List<CorpItem> items;

}
