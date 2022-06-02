package com.love.chat.vo;

import io.netty.handler.codec.http.FullHttpRequest;

public class FileUploadRequestVo {

    private FullHttpRequest request;

    public FileUploadRequestVo(FullHttpRequest request) {
        this.request = request;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

}