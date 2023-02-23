package com.mfw.themis.metric;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class AviatorCaculateTest {

    @Test
    public void baseOperator(){
        String formula = "https_response_success/https_response_total";
        Expression expression = AviatorEvaluator.compile(formula);

        Map<String, Object> params = new HashMap<>();
        params.put("https_response_success", new BigDecimal(10));
        params.put("https_response_total", new BigDecimal(100));

        BigDecimal caculateValue = (BigDecimal) expression.execute(params);

        Assert.assertTrue(new BigDecimal("0.1").compareTo(caculateValue) == 0);

        formula = "https_response_success / https_response_total < 0.2 && https_response_total > 20";
        expression = AviatorEvaluator.compile(formula);

        params = new HashMap<>();
        params.put("https_response_success", new BigDecimal(10));
        params.put("https_response_total", new BigDecimal(100));

        Boolean boolValue = (Boolean) expression.execute(params);

        Assert.assertTrue(boolValue);

        formula = "https_response_total > 0 && https_response_success / https_response_total";
        expression = AviatorEvaluator.compile(formula);
        params = new HashMap<>();
        params.put("https_response_success", new BigDecimal(10));
        params.put("https_response_total", new BigDecimal(0));

        Boolean caculateValue2 =(Boolean) expression.execute(params);
        Assert.assertFalse(caculateValue2);

        formula = "t1 > 0 && t3 > 0 && ( t1 - t3) / t3 > 0.5";
        expression = AviatorEvaluator.compile(formula);
        params = new HashMap<>();
        params.put("t1", new BigDecimal(0.0));
        params.put("t3", new BigDecimal(0.0));
        System.out.println(expression.execute(params));

    }
}
