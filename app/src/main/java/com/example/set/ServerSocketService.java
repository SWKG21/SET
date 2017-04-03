package com.example.set;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

import static android.content.ContentValues.TAG;

/**
 * Created by wang on 4/3/17.
 */

public class ServerSocketService extends Service {
    public static ServerSocket serversocket;
    public static Socket socket;
    public static ReceiveThread mReceiveThread;
    public boolean running = false;
    public Handler mHandler = new MyHandler();

    @Override
    public void onCreate(){
        Log.v(TAG, "serversocket onCreate");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void initServerSocket(){
        try{
            Log.v(TAG, "initServerSocket");
            serversocket = new ServerSocket(40012);
            socket = serversocket.accept();
            Message msg = mHandler.obtainMessage();
            msg.what = 0;
            msg.obj = socket.getInetAddress().getHostAddress();//获取客户端IP地址
            mHandler.sendMessage(msg);
            mReceiveThread = new ReceiveThread(socket);
            mReceiveThread.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //定义接收数据的线程
    private class ReceiveThread extends Thread{
        private InputStream is = null;
        private String read;
        //建立构造函数来获取socket对象的输入流
        public ReceiveThread(Socket sk){
            try {
                is = sk.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            while (running) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    //读服务器端发来的数据，阻塞直到收到结束符\n或\r
                    read = br.readLine();
                    System.out.println(read);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    running = false;//防止服务器端关闭导致客户端读到空指针而导致程序崩溃
                    Message msg2 = mHandler.obtainMessage();
                    msg2.what = 2;
                    mHandler.sendMessage(msg2);//发送信息通知用户客户端已关闭
                    e.printStackTrace();
                    break;
                }
                //用Handler把读取到的信息发到主线程
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                msg.obj = read;
                mHandler.sendMessage(msg);

            }
        }
    }

    private void displayToast(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    class MyHandler extends Handler {//在主线程处理Handler传回来的message
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String str = (String) msg.obj;
                    break;
                case 0:
                    //clientIP.setText("CLIENT "+msg.obj+" CONNECTED");
                    displayToast("CONNEXION SUCCESS");
                    break;
                case 2:
                    displayToast("CLIENT INTERRUPTED");
                    //清空TextView
                    //clientIP.setText(null);
                    try {
                        socket.close();
                        serversocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //btnAcept.setEnabled(true);
                    //btnStart.setEnabled(false);
                    break;
            }
        }
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);//清空消息队列，防止Handler强引用导致内存泄漏
    }*/
}
