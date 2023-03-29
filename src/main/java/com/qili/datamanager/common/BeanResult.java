package com.qili.datamanager.common;

import java.io.Serializable;

/**
 * @Author: wuyong
 * @Description: 封装统一响应对象
 * @DateTime: 2023/3/28 19:24
 **/
public class BeanResult<T> implements Serializable {

    /**
     * 成功
     */
    public static final int SUCCESS = 0;

    /**
     * 失败
     */
    public static final int FAIL = 1;

    /** 特殊业务描述场景返回值 */
    /**
     * 未登录返回
     */
    public static final int LOGIN = 2;

    /**
     * 多账户返回
     */
    public static final int MOREACCOUNT = 3;
    /**
     * 强提示
     */
    public static final int FORCEALERT = 4;

    /**
     * 强认证
     */
    public static final int FORCECONFIRM = 5;

    /**
     * 默认的系统失败code
     */
    public static final int SYS_FAIL = -1;

    int code = SUCCESS;

    String message;

    T data;

    public BeanResult() {
        this(SUCCESS);
    }

    /**
     * 单纯指定code的返回类
     * @param code
     */
    public BeanResult(int code) {
        this.code = code;
    }

    /**
     * 指定消息类型和消息
     * @param code
     * @param message
     */
    public BeanResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 默认的成功的数据消息
     * @param data
     */
    public BeanResult(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
