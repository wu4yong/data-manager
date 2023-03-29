package com.qili.datamanager.common;

/**
 * @Author: wuyong
 * @Description: 公共状态枚举
 * @DateTime: 2023/3/29 16:32
 **/
public enum CommonStatusEnum {
    /**
     * 成功
     */
    SUCCESS(0, "Success"),

    /**
     * 失败
     */
    FAIL(-1, "Failure");


    private final int code;


    private final String value;

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    CommonStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
