package com.mfw.themis.alarm.manager;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

/**
 * 普罗米修斯上报
 */
public class PrometheusMonitor {

    public static void counter(String name, Map<String, Object> tags) {

        List<Tag> tagArrayList = new ArrayList<>();

        tags.forEach((k ,v) -> {
            tagArrayList.add(Tag.of(k, v.toString()));
        });

        Metrics.counter(name, tagArrayList).increment();
    }
}
