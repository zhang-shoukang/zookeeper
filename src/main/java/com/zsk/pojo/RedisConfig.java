package com.zsk.pojo;

import java.io.Serializable;

/**
 * Create by zsk on 2018/8/14
 **/
public class RedisConfig implements Serializable {
    private String type;
    private String url;
    private String remark;

    public RedisConfig() {
    }

    public RedisConfig(String type, String url, String remark) {
        this.type = type;
        this.url = url;
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
