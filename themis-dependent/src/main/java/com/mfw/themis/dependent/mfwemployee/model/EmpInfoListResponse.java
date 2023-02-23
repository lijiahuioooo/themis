package com.mfw.themis.dependent.mfwemployee.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author wenhong
 */
@Data
public class EmpInfoListResponse {

    private Integer code;

    private String message;

    private List<EmpInfoData> data;


}
