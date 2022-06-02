package com.love.chat.util;

public enum MsgTypeEnum {

    ONE2ONE(1,"私聊"),
    ONE2ALL(2,"公屏");

    private int code;
    private String name;

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private MsgTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MsgTypeEnum getMsgTypeEnum(int code) {
        for(MsgTypeEnum item : MsgTypeEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}