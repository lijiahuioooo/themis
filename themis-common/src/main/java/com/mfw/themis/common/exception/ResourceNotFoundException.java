package com.mfw.themis.common.exception;


import com.mfw.themis.common.constant.enums.ResourceEnum;

/**
 * @author liuqi
 */
public class ResourceNotFoundException extends ServiceException {

    private static final String ERROR_MESSAGE = "%s未找到，id:%d";

    public ResourceNotFoundException(ResourceEnum resource, Long id) {
        super(String.format(ERROR_MESSAGE, resource.getDesc(), id));
    }
}
