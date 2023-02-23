package com.mfw.themis.common.util;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DebugHelper {
    @Value("${log.debug.metricIds:}")
    private String debugMetricIds;

    public boolean isDebugMetricId(Long id) {
        return Arrays.stream(StringUtils.split(debugMetricIds, ",")).map(Long::valueOf).anyMatch(e -> e.equals(id));
    }
}
