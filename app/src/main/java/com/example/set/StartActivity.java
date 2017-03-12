package com.example.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by wang on 3/12/17.
 */

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button btnSingle = (Button) findViewById(R.id.btnSingle);
        Button btnMulti = (Button) findViewById(R.id.btnMulti);
        Button btnRule = (Button) findViewById(R.id.btnRule);

        btnSingle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent(StartActivity.this, SingleActivity.class);
                startActivity(it);
            }
        });

        btnRule.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent(StartActivity.this, RuleActivity.class);
                startActivity(it);
            }
        });

        btnMulti.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent(StartActivity.this, MultiActivity.class);
                startActivity(it);

                new Thread(){
                    @Override
                    public void run(){
                        try{
                            acceptServer();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            private void acceptServer() throws IOException {
                Socket socket = new Socket("127.0.0.1", 12345);
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);

                InetAddress address = InetAddress.getLocalHost();
                String ip = address.getHostAddress();
                pw.write(ip+"access in!");
                pw.flush();
                socket.shutdownOutput();
                socket.close();
            }
        });


    }
}
