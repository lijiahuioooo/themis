package com.mfw.themis.collector.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wenhong
 */
@RestController
public class AosController {

    @RequestMapping("/")
    public String index() {
        return "ok";
    }
}
