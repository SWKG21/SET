package com.example.set;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class CreateActivity extends AppCompatActivity {
    private TextView serverIP = null;
    private TextView clientIP = null;
    private Button btnAcept = null;
    private Button btnStart = null;
    private Socket socket;
    private ServerSocket mServerSocket = null;
    private boolean running = false;
    private AcceptThread mAcceptThread;
    private ReceiveThread mReceiveThread;
    private Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        serverIP = (TextView) findViewById(R.id.serverIP);
        clientIP = (TextView) findViewById(R.id.clientIP);
        btnAcept = (Button) findViewById(R.id.btnAccept);
        btnStart = (Button) findViewById(R.id.btnStart);
        mHandler = new MyHandler();
        btnStart.setEnabled(false);

        //button for creating the server
        btnAcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAcceptThread = new AcceptThread();
                running = true;
                mAcceptThread.start();

                //here is a problem about IP address to fix !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                serverIP.setText(getLocalIpAddress());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println(InetAddress.getLocalHost().getHostAddress());
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                clientIP.setText("WAITING FOR CLIENT...");
                btnAcept.setEnabled(false);
                btnStart.setEnabled(true);
            }
        });

        //button for start the game
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OutputStream os = socket.getOutputStream();
                    os.write("start_game".getBytes("utf-8"));
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent it = new Intent(CreateActivity.this, PlayAsServerActivity.class);
                startActivity(it);
            }
        });
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference Ip", ex.toString());
        }
        return null;
    }

    //定义监听客户端连接的线程
    private class AcceptThread extends Thread{
        @Override
        public void run() {
//            while (running) {
            try {
                mServerSocket = new ServerSocket(40012);//建立一个ServerSocket服务器端
                socket = mServerSocket.accept();//阻塞直到有socket客户端连接
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = socket.getInetAddress().getHostAddress();//获取客户端IP地址
                mHandler.sendMessage(msg);//返回连接成功的信息
                //开启mReceiveThread线程接收数据
                mReceiveThread = new ReceiveThread(socket);
                mReceiveThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //            }
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

    class MyHandler extends Handler{//在主线程处理Handler传回来的message
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String str = (String) msg.obj;
                    break;
                case 0:
                    clientIP.setText("CLIENT "+msg.obj+" CONNECTED");
                    displayToast("CONNEXION SUCCESS");
                    break;
                case 2:
                    displayToast("CLIENT INTERRUPTED");
                    //清空TextView
                    clientIP.setText(null);
                    try {
                        socket.close();
                        mServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btnAcept.setEnabled(true);
                    btnStart.setEnabled(false);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);//清空消息队列，防止Handler强引用导致内存泄漏
    }

}
