package com.example.bajian.tcpdemo.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.bajian.tcpdemo.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * TcpIntentService to receivce the msg
 * Created by bajian on 2015/10/27.
 * email 313066164@qq.com
 */
public class TcpIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private static String TAG="TcpIntentService";
    private static TCPCallBack mTCPCallBack;
    private boolean runFlag=true;//是否继续循环

    public TcpIntentService() {
        super(TAG);
    }

    //声明一个接口
    public interface TCPCallBack {
        void onReceive(String msg);
    }

    public static void setTCPCallBackListener(TCPCallBack cb){
        mTCPCallBack=cb;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        loop();
    }


    /**
     * 获取消息
     */
    private void loop(){

        InputStream in=null;
        try {
            in= MainActivity.s.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Log.d(TAG, "loop");
        while(runFlag){
            if(MainActivity.s.isClosed() || in==null){
                Log.d(TAG,"return");
                break;
            }
            Log.d(TAG,"loop2");
            String msg = receiveMSG(in);
            if (msg==null){
                Log.d(TAG,"null");
                break;
            }

            if (!TextUtils.isEmpty(msg) && mTCPCallBack!=null)
                mTCPCallBack.onReceive(msg);
        }
    }


    private String receiveMSG(InputStream in){
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(in));
            String message = input.readLine();
                Log.d(TAG,"receive:"+message);
                return message;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }








}
