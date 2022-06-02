package com.love.chat.vo;

import io.netty.handler.codec.http.FullHttpRequest;

public class HttpRequestVo {

    private FullHttpRequest request;

    public HttpRequestVo(FullHttpRequest request) {
        this.request = request;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }
}