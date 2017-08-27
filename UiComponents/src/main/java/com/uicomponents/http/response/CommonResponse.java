package com.uicomponents.http.response;

import lombok.Data;

/**
 * 所有从服务器获取数据的父类
 * 包含请求失败返回的数据项
 * 所有用于 API 请求的 model 都应该继承该类
 * 处理方式见 {@link ResponseCallback}
 *
 * @author panxiangxing
 * @data 17/1/2
 */
@Data
public class CommonResponse {
    /**
     * 状态码
     */
    private int status;

    /**
     * 请求返回的提示信息
     */
    private String info;

    public boolean isSuccess() {
        return status == 0;
    }
}
