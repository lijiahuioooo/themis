package com.mfw.themis.common.constant;

/**
 * @author liuqi
 */
public class SdkCollectFields {

    public static final String REMOTE_ADDR = "remoteAddr";
    public static final String LOCAL_ADDR = "localAddr";
    public static final String ENDPOINT = "endpoint";
    public static final String RT = "rt";
    public static final String SUCCESS = "success";
    /**
     * http
     */
    public static final String HTTP_EVENT = "default_http_event";
    public static final String HTTP_URL = "httpUrl";
    public static final String HTTP_METHOD = "httpMethod";
    public static final String HTTP_STATUS = "httpStatus";
    public static final String HTTP_THREAD = "httpThread";
    public static final String HTTP_RESPONSE_SIZE = "httpResSize";

    /**
     * dubbo
     */
    public static final String DUBBO_EVENT = "default_dubbo_event";
    public static final int DUBBO_STATUS_SUCCESS = 1;
    public static final int DUBBO_STATUS_EXCEPTION = 2;
    public static final String DUBBO_APPLICATION = "dubbo_application_name";
    public static final String DUBBO_SERVICE = "dubbo_service_name";
    public static final String DUBBO_CLASS = "dubbo_class_name";
    public static final String DUBBO_METHOD = "dubbo_method_name";
    public static final String DUBBO_VERSION = "dubbo_version";
    public static final String DUBBO_GROUP = "dubbo_group";
    public static final String DUBBO_STATUS = "dubbo_status";
    public static final String DUBBO_CODE = "dubbo_code";
    public static final String DUBBO_MSG = "dubbo_msg";
    public static final String DUBBO_PARAM = "dubboParam";

    public static final String DUBBO_RESPONSE_TIME = "dubbo_rt";
    public static final String DUBBO_COUNT = "count";
    public static final String DUBBO_ARGUMENT = "dubbo_argument";
    /**
     * grpc
     */
    public static final String GRPC_EVENT = "default_grpc_event";
    public static final String GRPC_METHOD = "grpcMethod";
    public static final String GRPC_PARAM = "grpcParam";

}
