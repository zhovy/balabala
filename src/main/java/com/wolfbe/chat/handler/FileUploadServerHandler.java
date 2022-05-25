package com.wolfbe.chat.handler;

import com.wolfbe.chat.entity.UserInfo;
import com.wolfbe.chat.util.Constants;
import com.wolfbe.chat.util.ParamUtil;
import com.wolfbe.chat.util.ResponseUtil;
import com.wolfbe.chat.vo.FileUploadRequestVo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * @Author: zhouy
 **/
public class FileUploadServerHandler extends SimpleChannelInboundHandler<FileUploadRequestVo> {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadServerHandler.class);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileUploadRequestVo requestVo) throws Exception {

        UserInfo userInfo = UserInfoManager.getUserInfo(ctx.channel());

        if (userInfo != null && userInfo.isAuth()) {

            FullHttpRequest request = requestVo.getRequest();
            FileUpload fileUpload = ParamUtil.getFileUpload(request);
            if (fileUpload == null || !fileUpload.isCompleted()) {
                ResponseUtil.sendHttpResponse(ctx, request, ResponseUtil.get400Response());
                return;
            }
            String fileName = fileUpload.getFilename();
            //毫秒数+.文件后缀
            String newName = System.currentTimeMillis() + fileName.substring(fileName.lastIndexOf("."));
            fileUpload.renameTo(new File(Constants.FILE_UPLOAD_ABS_PATH_PREFIX + newName));
            ResponseUtil.sendHttpResponse(ctx, request, ResponseUtil.get200Response(Constants.FILE_UPLOAD_MAPPING_URL_PREFIX + newName));

        }
    }


    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }
}
