package com.wolfbe.chat.util;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamUtil {

    /**
     * 获取请求参数
     */
    public static Map<String, String> getRequestParams(HttpRequest request){
        Map<String, String>requestParams=new HashMap<>();
        // 处理get请求
        if (request.method() == HttpMethod.GET) {
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            Map<String, List<String>> params = decoder.parameters();
            for(Map.Entry<String, List<String>> entry : params.entrySet()){
                requestParams.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        // 处理POST请求
        if (request.method() == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
            List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();
            for(InterfaceHttpData data : postData){
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    MemoryAttribute attribute = (MemoryAttribute) data;
                    requestParams.put(attribute.getName(), attribute.getValue());
                }
            }
        }
        return requestParams;
    }
    
    /**
     * 获取文件上传参数
     */
    public static FileUpload getFileUpload(HttpRequest request){
        // 处理POST请求
        if (request.method() == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
            List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();
            for(InterfaceHttpData data : postData){
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                    return (FileUpload) data;
                }
            }
        }
        return null;
    }

    /**
     * 获取请求Uri
     */
    public static String getUri(HttpRequest request){
        return new QueryStringDecoder(request.uri()).path();
    }

}