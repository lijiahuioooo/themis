package com.mfw.themis.common.model.bo.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wenhong
 */
@Getter
@Setter
public class AppSuggestBO {

    @JsonProperty("appId")
    private Long id;

    private String appCode;

    private String appName;
}
