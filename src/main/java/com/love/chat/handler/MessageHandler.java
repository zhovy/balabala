package com.love.chat.handler;

import com.alibaba.fastjson.JSONObject;
import com.love.chat.entity.UserInfo;
import com.love.chat.proto.ChatCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @site http://www.wolfbe.com
 * @github https://github.com/beyondfengyu
 */
public class MessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        UserInfo userInfo = UserInfoManager.getUserInfo(ctx.channel());
        if (userInfo != null && userInfo.isAuth()) {
            JSONObject json = JSONObject.parseObject(frame.text());
            // 广播返回用户发送的消息文本
            UserInfoManager.broadcastMess(userInfo.getUserId(), userInfo.getNick(), json.getString("mess"));
        }
        // 管理员权限获取在线用户信息
        if (null != userInfo && ChatCode.ROOT_ACCOUNT.equals(userInfo.getNick())) {
            ArrayList<Object> resList = new ArrayList<>();
            JSONObject json = JSONObject.parseObject(frame.text());
            if ("获取在线用户".equals(json.get("mess"))) {
                List<UserInfo> onlineUser = UserInfoManager.getOnlineUser();
                for (final UserInfo info : onlineUser) {
                    JSONObject resJson = new JSONObject() {{
                        put("用户昵称", info.getNick());
                        put("连接地址", info.getAddr());
                        put("UID", info.getUserId());
                    }};
                    resList.add(resJson);
                }
                // 广播返回用户发送的消息文本
                UserInfoManager.broadcastMess(userInfo.getUserId(), userInfo.getNick(), resList.toString());
            }
        }
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        UserInfoManager.removeChannel(ctx.channel());
        UserInfoManager.broadCastInfo(ChatCode.SYS_USER_COUNT, UserInfoManager.getAuthUserCount());
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("connection error and close the channel", cause);
        UserInfoManager.removeChannel(ctx.channel());
        UserInfoManager.broadCastInfo(ChatCode.SYS_USER_COUNT, UserInfoManager.getAuthUserCount());
    }

}
