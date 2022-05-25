package com.wolfbe.chat.handler;

import com.wolfbe.chat.util.Constants;
import com.wolfbe.chat.util.ParamUtil;
import com.wolfbe.chat.vo.FileUploadRequestVo;
import com.wolfbe.chat.vo.HttpRequestVo;
import com.wolfbe.chat.vo.WebSocketRequestVo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import jdk.internal.net.http.websocket.WebSocketRequest;

/**
 * @author: zhouy
 * @date: 2022/05/23
 **/
public class DispatchHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            //判断是否为websocket握手请求
            if (isWebSocketHandShake(request)) {
                ctx.fireChannelRead(new WebSocketRequestVo(request));
                //文件上传
            } else if (isFileUpload(request)) {
                ctx.fireChannelRead(new FileUploadRequestVo(request));
                //Http请求
            } else {
                ctx.fireChannelRead(new HttpRequestVo(request));
            }
            //websocket请求
        } else if (msg instanceof WebSocketRequest) {
            WebSocketFrame frame = (WebSocketFrame) msg;
            ctx.fireChannelRead(new WebSocketRequestVo(frame));
        }

    }

    //判断是否为websocket握手请求
    private boolean isWebSocketHandShake(FullHttpRequest request){
        //1、判断是否为get 2、判断Upgrade头是否包含websocket 3、Connection头是否包含upgrade
        return request.method().equals(HttpMethod.GET)
                && request.headers().contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true)
                && request.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true);
    }

    //判断是否为文件上传
    private boolean isFileUpload(FullHttpRequest request){
        //1、判断是否为文件上传自定义URI 3、判断是否为POST方法 2、判断Content-Type头是否包含multipart/form-data
        String uri = ParamUtil.getUri(request);
        String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if(contentType == null || contentType.isEmpty()){
            return false;
        }
        return Constants.FILE_UPLOAD_URL.equals(uri)
                && request.method() == HttpMethod.POST
                && contentType.toLowerCase().contains(HttpHeaderValues.MULTIPART_FORM_DATA);
    }

}
