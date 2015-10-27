package com.example.bajian.tcpdemo.bean;

/**
 * Created by bajian on 2015/10/27.
 * email 313066164@qq.com
 */
public class MsgBean {
    private String name;//user name
    private String msg;//data
    private String create_time;
    private int msg_type;//消息类型

    @Override
    public String toString() {
        return "MsgBean{" +
                "name='" + name + '\'' +
                ", msg='" + msg + '\'' +
                ", create_time='" + create_time + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        name = mName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String mMsg) {
        msg = mMsg;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String mCreate_time) {
        create_time = mCreate_time;
    }
}
