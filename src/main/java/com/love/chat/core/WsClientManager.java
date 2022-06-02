package com.love.chat.core;

import com.alibaba.fastjson.JSON;
import com.love.chat.util.MsgTypeEnum;
import com.love.chat.vo.MsgVo;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WsClientManager {

    //单例
    private static WsClientManager instance = new WsClientManager();
    private WsClientManager(){}
    public static WsClientManager getInstance(){
        return instance;
    }

    //socket自定义ID与信道的对应关系
    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    //添加信道
    public void putChannel(String id, Channel channel){
        this.channelMap.put(id, channel);
    }

    //获取信道
    public void getChannel(String id){
        this.channelMap.get(id);
    }

    //删除信道
    public void removeChannel(String id){
        this.channelMap.remove(id);
    }

    //发送消息
    public void sendMsg(String id, String msg){
        TextWebSocketFrame frame = new TextWebSocketFrame(msg);
        Channel channel = channelMap.get(id);
        if(channel != null){
            channel.writeAndFlush(frame);
        }
    }

    //群发消息
    public void sendMsg2All(String msg){
        for(Channel channel : channelMap.values()){
            TextWebSocketFrame frame = new TextWebSocketFrame(msg);
            channel.writeAndFlush(frame);
        }
    }

    //处理消息
    public void handleMsg(String msgJson){
        MsgVo msgVo = JSON.parseObject(msgJson, MsgVo.class);
        if(msgVo.getType() == MsgTypeEnum.ONE2ONE.getCode()){
            this.sendMsg(msgVo.getToId(), msgJson);
            this.sendMsg(msgVo.getFromId(), msgJson);
        }else if(msgVo.getType() == MsgTypeEnum.ONE2ALL.getCode()){
            this.sendMsg2All(msgJson);
        }
    }

}