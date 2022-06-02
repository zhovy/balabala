package com.love.chat.util;

/**
 * @author laochunyu
 */
public class Constants {

    public static String DEFAULT_HOST = "localhost";
    public static int DEFAULT_PORT = 9688;
    public static String WEBSOCKET_URL = "ws://localhost:8099/websocket";
    //websocket的自定义id
    public static final String SOCKET_ID = "userId";
    //controller的路径
    public static final String CONTROLLER_PATH = "com.wode.http.controller";
    //文件上传请求uri
    public static final String FILE_UPLOAD_URL = "/upload";
    //外部访问本地文件映射路径
    public static final String FILE_UPLOAD_MAPPING_URL_PREFIX = "http://localhost:8090/file/";
    //文件上传本地绝对路径前缀
    public static final String FILE_UPLOAD_ABS_PATH_PREFIX = "E:/tmp/";
}
