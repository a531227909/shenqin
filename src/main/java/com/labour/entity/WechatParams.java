package com.labour.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Configuration
@ConfigurationProperties(prefix = "wechatparams", ignoreUnknownFields = false)
@PropertySource("classpath:wechatparams.properties")
@Data
@Component
public class WechatParams implements Serializable {

    private static final long serialVersionUID = 1L;
    //小程序 授权登录请求地址
    private String postUrl;
    //小程序 appId
    private String appid;
    //小程序 appSecret
    private String secret;
    //授权类型
    private String grant_type;

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    @Override
    public String toString() {
        return "WechatParams{" +
                "postUrl='" + postUrl + '\'' +
                ", appid='" + appid + '\'' +
                ", secret='" + secret + '\'' +
                ", grant_type='" + grant_type + '\'' +
                '}';
    }

}
