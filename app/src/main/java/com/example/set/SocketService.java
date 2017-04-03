package com.example.set;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.Log;

import java.net.Socket;

import static android.content.ContentValues.TAG;

/**
 * Created by wang on 4/3/17.
 */

public class SocketService extends Service {
    public static Socket socket;

    @Override
    public void onCreate(){
        Log.v(TAG, " socket onCreate");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void initSocket( String s){
        try{
            Log.v(TAG, "initSocket");
            socket = new Socket(s, 40012);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

