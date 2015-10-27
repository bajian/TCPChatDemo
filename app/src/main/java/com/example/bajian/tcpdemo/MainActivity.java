package com.example.bajian.tcpdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bajian.tcpdemo.bean.MsgBean;
import com.example.bajian.tcpdemo.service.TcpCheckIntentService;
import com.example.bajian.tcpdemo.service.TcpIntentService;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements TcpCheckIntentService.TCPCheckCallBack, TcpIntentService.TCPCallBack{

    private static final String TAG="MainActivity";
    private static final String TCP_HOST="120.24.76.242";
    private static final int TCP_PORT=9501;

    public static Socket s=null;
    private ObjectOutputStream out;
    private ReadThread mReadThread;
    private InputStream in;
    private TextView tv;
    private EditText et_msg;
    private EditText et_name;
    private MsgBean mMsg=new MsgBean();
    private static Gson gson=new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        connetTcp();

    }

    private void connetTcp(){
        mReadThread=new ReadThread();
        mReadThread.start();
    }

    private void initViews() {
        tv=(TextView)findViewById(R.id.tv_receive);
        et_msg=(EditText)findViewById(R.id.et_msg);
        et_name=(EditText)findViewById(R.id.et_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TcpIntentService.setTCPCallBackListener(MainActivity.this);
        TcpCheckIntentService.setTCPCheckCallBackListener(MainActivity.this);

    }

    public void send(View view) throws IOException {
        String str=et_msg.getText().toString().trim();
        String name=et_name.getText().toString().trim();
        et_msg.setText("");
        mMsg.setMsg(str);
        mMsg.setName(name);
        mMsg.setCreate_time(System.currentTimeMillis()/1000+"");
        if (!"".equals(str))
            sendMsg(gson.toJson(mMsg));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (s!=null) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mReadThread.exit();
        mReadThread.interrupt();
    }

    private void sendMsg(String msg) throws IOException {
        if (s!=null){
            OutputStream os = s.getOutputStream();
            PrintWriter output = new PrintWriter(os, true);//true auto fulsh
            output.println(msg);
            Log.d(TAG,"send->"+msg);
        }
    }

    @Override
    public void onReceive(final String msg) {
        Log.d(TAG, "onReceive" + Thread.currentThread().toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MsgBean obj = gson.fromJson(msg, MsgBean.class);
                tv.append(obj.getName() + ":" + obj.getMsg() + "\n\r");
            }
        });

    }

    @Override
    public void onUnregister() {
        Log.d(TAG, "onUnregister!");
        new ReadThread().start();
    }

    @Override
    public void onNotConnect() {
        Log.d(TAG, "onNotConnect!");
        new ReadThread().start();
    }


    class ReadThread extends Thread{
        boolean runFlag=true;

        public void run(){
            connect();
        }

        public void exit(){
            runFlag=false;
        }

    }

    private void connect() {
        try {
            s = new Socket(TCP_HOST, TCP_PORT);
            Log.d(TAG, "connect!");
        } catch (IOException e) {
            e.printStackTrace();
            TcpCheckIntentService.setTCPState(TcpCheckIntentService.STATE_NOT_CONNECT);
        }

        if (MainActivity.s!=null){
            startService(new Intent(MainActivity.this,TcpIntentService.class));
        }
        startService(new Intent(MainActivity.this,TcpCheckIntentService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
