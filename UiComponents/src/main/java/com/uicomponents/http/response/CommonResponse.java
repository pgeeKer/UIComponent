package com.uicomponents.http.response;

/**
 * 所有从服务器获取数据的父类
 * 包含请求失败返回的数据项
 * 所有用于 API 请求的 model 都应该继承该类
 * 处理方式见 {@link ResponseCallback}
 *
 * @author panxiangxing
 * @data 17/1/2
 */
public class CommonResponse {
    /**
     * 状态码
     */
    private int statusCode;

    /**
     * 请求返回的提示信息
     */
    private String info;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isSuccess() {
        return statusCode == 0;
    }
}
