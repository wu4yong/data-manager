package com.qili.datamanager.web;

import com.alibaba.fastjson2.JSON;
import com.qili.datamanager.common.BeanResult;
import com.qili.datamanager.vo.HbaseTableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wuyong
 * @Description: Hbase表操作前端控制器
 * @DateTime: 2023/3/28 20:20
 **/
@RestController
@RequestMapping("/hbase")
public class HbaseOperationController {

    private static final Logger logger = LoggerFactory.getLogger(HbaseOperationController.class);

    @PostMapping("/saveTableInfo")
    public BeanResult<HbaseTableInfo> queryDailyVideo(@RequestBody @Validated HbaseTableInfo request) {
        logger.info("=== Hbase Operation  params {} ===", JSON.toJSONString(request));
        return null;
    }

}
