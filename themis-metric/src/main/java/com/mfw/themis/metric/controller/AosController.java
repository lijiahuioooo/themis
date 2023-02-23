package com.mfw.themis.metric.controller;

import com.mfw.themis.common.model.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuqi
 */
@RestController
public class AosController {


    @GetMapping
    public ResponseResult index() {
        return ResponseResult.OK("ok");
    }

}
