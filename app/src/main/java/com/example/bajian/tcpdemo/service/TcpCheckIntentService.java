package com.example.bajian.tcpdemo.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.bajian.tcpdemo.MainActivity;

/**
 * to check is socket connected
 * Created by bajian on 2015/10/27.
 * email 313066164@qq.com
 */
public class TcpCheckIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private static final String TAG="TcpCheckIntentService";
    public static final int STATE_NOT_CONNECT=0;
    public static final int STATE_CONNECTING=1;
    public static final int STATE_CONNECTED=2;

    private static int state=STATE_NOT_CONNECT;

    private static TCPCheckCallBack mTCPCheckCallBack;
    private boolean runFlag=true;//是否继续循环

    public TcpCheckIntentService() {
        super(TAG);
    }

    //声明一个接口
    public interface TCPCheckCallBack {
        void onUnregister();//没初始化成功
        void onNotConnect();//断线
    }

    public static void setTCPCheckCallBackListener(TCPCheckCallBack cb){
        mTCPCheckCallBack=cb;
    }

    public static void setTCPState(int tcp_state){
        state=tcp_state;
    }

    public void setRunFlag(boolean isRunning){
        runFlag=isRunning;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        loop();
    }


    /**
     * 判断是否需要断线重连,5s重连一次
     */
    private void loop(){
        Log.d(TAG,"loop1");
        if (MainActivity.s==null){//可能出现原因，没开网络
            if (mTCPCheckCallBack!=null){
                sleep(2000);
                mTCPCheckCallBack.onUnregister();
            }
            return;
        }

        while(runFlag){
            Log.d(TAG,"loop2");
            if (isConnected()){//链接着
                Log.d(TAG,"isConnected");
                setTCPState(STATE_CONNECTED);
            }else if (state==STATE_NOT_CONNECT ||state==STATE_CONNECTED){
                Log.d(TAG,"state==STATE_NOT_CONNECT ||state==STATE_CONNECTED");
                setTCPState(STATE_CONNECTING);
                sleep(2000);
                mTCPCheckCallBack.onNotConnect();
                runFlag=false;
                break;

            }

            sleep(5000);


        }
    }

    private  void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isConnected(){
        try{
            MainActivity.s.sendUrgentData(0xFF);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
