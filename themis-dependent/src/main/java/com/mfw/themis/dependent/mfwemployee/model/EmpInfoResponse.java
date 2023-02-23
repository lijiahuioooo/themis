package com.mfw.themis.dependent.mfwemployee.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author wenhong
 */
@Data
public class EmpInfoResponse {

    private Integer code;

    private String message;

    private EmpInfoData data;


}
