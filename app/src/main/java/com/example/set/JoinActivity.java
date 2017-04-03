package com.example.set;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class JoinActivity extends AppCompatActivity {
    private TextView clientIP;
    private EditText serverIP;
    private Handler myhandler;
    private Socket socket;
    private String str = "";
    boolean running = false;
    private Button btnStart;
    private StartThread st;
    private ReceiveThread rt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        clientIP = (TextView) findViewById(R.id.clientIP);
        clientIP.setText(getLocalIpAddress());
        serverIP = (EditText) findViewById(R.id.serverIP);

        btnStart = (Button) findViewById(R.id.btnStart);
        setButtonOnStartState(true);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                st = new StartThread();
                st.start();
                setButtonOnStartState(false);
            }
        });

        myhandler = new MyHandler();//实例化Handler，用于进程间的通信
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference Ip", ex.toString());
        }
        return null;
    }



    private class StartThread extends Thread{
        @Override
        public void run() {
            try {
                socket = new Socket(serverIP.getText().toString(),40012);
                rt = new ReceiveThread(socket);
                rt.start();
                running = true;
                System.out.println(socket.isConnected());
                if(socket.isConnected()){
                    Message msg0 = myhandler.obtainMessage();
                    msg0.what=0;
                    myhandler.sendMessage(msg0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //for input from server to client
    private class ReceiveThread extends Thread{
        private InputStream is;

        public ReceiveThread(Socket socket) throws IOException {
            is = socket.getInputStream();
        }
        @Override
        public void run() {
            while (running) {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                try {
                    str = br.readLine();
                } catch (NullPointerException e) {
                    running = false;//防止服务器端关闭导致客户端读到空指针而导致程序崩溃
                    Message msg2 = myhandler.obtainMessage();
                    msg2.what = 2;
                    myhandler.sendMessage(msg2);//发送信息通知用户客户端已关闭
                    e.printStackTrace();
                    break;

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //用Handler把读取到的信息发到主线程
                Message msg = myhandler.obtainMessage();
                msg.what = 1;
                msg.obj = str;
                myhandler.sendMessage(msg);
                try {
                    sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            Message msg2 = myhandler.obtainMessage();
            msg2.what = 2;
            myhandler.sendMessage(msg2);//发送信息通知用户客户端已关闭

        }
    }

    private void displayToast(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void setButtonOnStartState(boolean flag){
        btnStart.setEnabled(flag);
        serverIP.setEnabled(flag);
    }

    class MyHandler extends Handler {//在主线程处理Handler传回来的message

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String str = (String) msg.obj;
                    if(str.equals("start_game")) {
                        ViewGroup layout = (ViewGroup) btnStart.getParent();
                        layout.removeView(clientIP);
                        layout.removeView(serverIP);
                        layout.removeView(btnStart);
                    }
                    break;
                case 0:
                    displayToast("CONNEXION SUCCESS");
                    break;
                case 2:
                    displayToast("SERVER INTERRUPTED");
                    setButtonOnStartState(true);
                    break;

            }

        }
    }
}
