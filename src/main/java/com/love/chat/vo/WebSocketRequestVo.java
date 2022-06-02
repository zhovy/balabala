package com.love.chat.vo;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketRequestVo {

    //握手请求
    private FullHttpRequest request;
    //websocket请求
    private WebSocketFrame frame;

    public WebSocketRequestVo(FullHttpRequest request) {
        this.request = request;
    }

    public WebSocketRequestVo(WebSocketFrame frame) {
        this.frame = frame;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public WebSocketFrame getFrame() {
        return frame;
    }

    public void setFrame(WebSocketFrame frame) {
        this.frame = frame;
    }
}