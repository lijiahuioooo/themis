package com.mfw.themis.alarm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuqi
 */
@RestController
public class AosController {

    @RequestMapping("/")
    public String index() {
        return "ok";
    }


}
