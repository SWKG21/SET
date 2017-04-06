package com.example.set;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JoinActivity extends AppCompatActivity {

    private LinkedList<Integer> CardHeap = new LinkedList<>();
    int[] forCard = new int[12];
    private int selectedCount = 0;
    private CardPanel[] imgs = new CardPanel[15];
    private LinkedList<CardPanel> imgsSelected = new LinkedList<>();
    private LinkedList<Integer> forImgsSelected = new LinkedList<>();
    private TextView textTime;
    private int recLen = 0;
    private TextView textScore;
    private int score = 0;
    Canvas[] cs = new Canvas[15];
    LinearLayout tips;
    LinkedList<Integer> result = new LinkedList<>();
    LinkedList<CardPanel> forTips = new LinkedList<>();
    Handler handler = new Handler();

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

    private class StartThread extends Thread{
        @Override
        public void run() {
            try {
                socket = new Socket(serverIP.getText().toString(),40012);
                rt = new ReceiveThread(socket);
                rt.start();
                running = true;
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

    class MyHandler extends Handler {//在主线程处理Handler传回来的message

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String str = (String) msg.obj;
                    if(str.equals("start game")) {
                        ViewGroup layout = (ViewGroup) btnStart.getParent();
                        layout.removeView(clientIP);
                        layout.removeView(serverIP);
                        layout.removeView(btnStart);
                    }
                    else if(str.substring(0,8).equals("CardHeap")){
                        //receive CardHeap
                        str = str.substring(9, str.length()-1);
                        String[] strs  = str.split(", ");
                        for(int i=0;i<strs.length;i++)
                            CardHeap.add(Integer.valueOf(strs[i]));

                        //show out the first 12 cards
                        show12Cards();

                        //set the click function for the 12 cards
                        for(int i=0;i<12;i++)
                            imgs[i].setOnClickListener(new JoinActivity.cardClick());

                        //whether need to add 3 more cards
                        if(!Card.SETexist(forCard))
                            ThreeMore();
                        else{
                            forTips.clear();
                            result = Card.getSET(forCard);
                            for(int i=0;i<12;i++)
                                if(result.contains(i))
                                    forTips.add(imgs[i]);
                        }

                        //add result panel
                        tips = (LinearLayout) findViewById(R.id.tips);
                        tips.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorCard, null));
                        tips.setOnClickListener(new JoinActivity.tipsClick());

                        //add time
                        textTime = (TextView) findViewById(R.id.textTime);
                        textTime.setTextColor(Color.BLACK);

                        //add score
                        textScore = (TextView) findViewById(R.id.textScore);
                        textScore.setTextColor(Color.BLACK);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recLen++;
                                //int hour = recLen/3600;
                                int minuit = (recLen%3600)/60;
                                int second = recLen%60;
                                DecimalFormat df = new DecimalFormat("00");
                                //String hourF = df.format(hour);
                                String minuitF = df.format(minuit);
                                String secondF = df.format(second);
                                //textTime.setText(hourF+" : "+minuitF+" : "+secondF);
                                textTime.setText(minuitF+" : "+secondF);
                                String s = "Score : "+score;
                                textScore.setText(s);
                                handler.postDelayed(this, 1000);
                            }
                        }, 1000);
                    }
                    else if(str.substring(0,4).equals("true")){
                        imgsSelected.clear();
                        forImgsSelected.clear();
                        String[] strs  = str.substring(19,str.length()).split(", ");
                        for(int i=0;i<strs.length;i++)
                            forImgsSelected.add(Integer.valueOf(strs[i]));
                        //add to imgsSelected
                        ConvertForImgsSelected();
                        if(str.substring(5,18).equals(getLocalIpAddress()))
                            selfSETeffect();
                        else
                            SETeffect();
                    }
                    else if(str.substring(0,5).equals("false")){
                        imgsSelected.clear();
                        forImgsSelected.clear();
                        String[] strs  = str.substring(20,str.length()).split(", ");
                        for(int i=0;i<strs.length;i++)
                            forImgsSelected.add(Integer.valueOf(strs[i]));
                        //add to imgsSelected
                        ConvertForImgsSelected();
                        if(str.substring(6,19).equals(getLocalIpAddress()))
                            NOTSETeffect();
                        else {
                            imgsSelected.clear();
                            forImgsSelected.clear();
                        }
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

    public void ClientSend(String s){
        try {
            OutputStream os = socket.getOutputStream();
            os.write((s+"\n").getBytes("utf-8"));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
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

    public void ConvertImgsSelected(){
        for(CardPanel c : imgsSelected){
            if(c.getId()==R.id.img0)
                forImgsSelected.add(0);
            if(c.getId()==R.id.img1)
                forImgsSelected.add(1);
            if(c.getId()==R.id.img2)
                forImgsSelected.add(2);
            if(c.getId()==R.id.img3)
                forImgsSelected.add(3);
            if(c.getId()==R.id.img4)
                forImgsSelected.add(4);
            if(c.getId()==R.id.img5)
                forImgsSelected.add(5);
            if(c.getId()==R.id.img6)
                forImgsSelected.add(6);
            if(c.getId()==R.id.img7)
                forImgsSelected.add(7);
            if(c.getId()==R.id.img8)
                forImgsSelected.add(8);
            if(c.getId()==R.id.img9)
                forImgsSelected.add(9);
            if(c.getId()==R.id.img10)
                forImgsSelected.add(10);
            if(c.getId()==R.id.img11)
                forImgsSelected.add(11);
            if(c.getId()==R.id.img12)
                forImgsSelected.add(12);
            if(c.getId()==R.id.img13)
                forImgsSelected.add(13);
            if(c.getId()==R.id.img14)
                forImgsSelected.add(14);
        }
    }

    public void ConvertForImgsSelected(){
        for(int i : forImgsSelected)
            imgsSelected.add(imgs[i]);
    }

    //the tips function
    class tipsClick implements View.OnClickListener{
        @Override
        public void onClick(View v){
            if(imgs[12]==null && !forTips.isEmpty()){
                CardPanel tmp = forTips.poll();
                if(tmp.isSelected())
                    tmp = forTips.poll();
                tmp.setSelected(true);
                tmp.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selected, null));
                selectedCount++;
                tmp.setClickable(true);
                if(score>0)
                    score--;
                v.setClickable(false);
            }
        }
    }

    //the function for click
    class cardClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.isSelected()){
                v.setSelected(false);
                v.setBackgroundColor(Color.WHITE);
                selectedCount--;
                v.setClickable(true);
            }
            else{
                if(selectedCount<3){
                    v.setSelected(true);
                    v.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selected, null));
                    selectedCount++;
                    v.setClickable(true);
                    if(selectedCount==3){
                        addSelected();
                        ConvertImgsSelected();
                        ClientSend(getLocalIpAddress()+" "+forImgsSelected.toString());
                    }
                }
                else
                    v.setClickable(false);
            }
        }
    }

    //show out the first 12 cards
    private void show12Cards(){
        for(int i=0;i<12;i++)
            forCard[i] = CardHeap.poll();

        imgs[0] = (CardPanel) findViewById(R.id.img0);
        imgs[1] = (CardPanel) findViewById(R.id.img1);
        imgs[2] = (CardPanel) findViewById(R.id.img2);
        imgs[3] = (CardPanel) findViewById(R.id.img3);
        imgs[4] = (CardPanel) findViewById(R.id.img4);
        imgs[5] = (CardPanel) findViewById(R.id.img5);
        imgs[6] = (CardPanel) findViewById(R.id.img6);
        imgs[7] = (CardPanel) findViewById(R.id.img7);
        imgs[8] = (CardPanel) findViewById(R.id.img8);
        imgs[9] = (CardPanel) findViewById(R.id.img9);
        imgs[10] = (CardPanel) findViewById(R.id.img10);
        imgs[11] = (CardPanel) findViewById(R.id.img11);

        for(int i=0;i<12;i++){
            imgs[i].setBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorCard, null));
            imgs[i].card = new Card(forCard[i]);
            cs[i] = new Canvas();
            imgs[i].card.draw(cs[i],imgs[i].getWidth(),imgs[i].getHeight());
        }
    }

    private void ThreeMore(){
        imgs[12] = (CardPanel) findViewById(R.id.img12);
        imgs[13] = (CardPanel) findViewById(R.id.img13);
        imgs[14] = (CardPanel) findViewById(R.id.img14);

        for(int i=12;i<15;i++){
            imgs[i].setBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorCard, null));
            imgs[i].card = new Card(CardHeap.poll());
            cs[i] = new Canvas();
            imgs[i].card.draw(cs[i],imgs[i].getWidth(),imgs[i].getHeight());
            imgs[i].setOnClickListener(new JoinActivity.cardClick());
        }
    }

    //capture 3 selected cards
    private void addSelected(){
        for(int i=0;i<12;i++){
            if(imgs[i].isSelected()) {
                imgsSelected.add(imgs[i]);
            }
        }
        if(imgs[12]!=null){
            for(int i=12;i<15;i++)
                if(imgs[i].isSelected())
                    imgsSelected.add(imgs[i]);
        }
    }

    private void selfSETeffect(){
        //change to green if true
        imgsSelected.get(0).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.set, null));
        imgsSelected.get(1).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.set, null));
        imgsSelected.get(2).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.set, null));

        //translate the 3 cards to the corner
        CardPanel img_show0 = (CardPanel) findViewById(R.id.img_show0);
        img_show0.card = imgsSelected.get(0).card;
        Canvas c_show0 = new Canvas();
        img_show0.card.draw(c_show0,img_show0.getWidth(),img_show0.getHeight());

        CardPanel img_show1 = (CardPanel) findViewById(R.id.img_show1);
        img_show1.card = imgsSelected.get(1).card;
        Canvas c_show1 = new Canvas();
        img_show1.card.draw(c_show1,img_show1.getWidth(),img_show1.getHeight());

        CardPanel img_show2 = (CardPanel) findViewById(R.id.img_show2);
        img_show2.card = imgsSelected.get(2).card;
        Canvas c_show2 = new Canvas();
        img_show2.card.draw(c_show2,img_show2.getWidth(),img_show2.getHeight());

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_set);
        img_show0.startAnimation(animation);
        img_show1.startAnimation(animation);
        img_show2.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //add 2 point
                score=score+2;

                //recover the color of 3 cards
                imgsSelected.get(0).setBackgroundColor(Color.WHITE);
                imgsSelected.get(1).setBackgroundColor(Color.WHITE);
                imgsSelected.get(2).setBackgroundColor(Color.WHITE);

                //recover the status of selected cards
                imgsSelected.get(0).setSelected(false);
                imgsSelected.get(1).setSelected(false);
                imgsSelected.get(2).setSelected(false);

                //12(3 new) or 12(3 new)+3(new)=15 or 15-3(selected)=12 or 15-3(selected)+3(new)=15
                showThreeNewCards();

                //reclickable all 12 cards
                selectedCount = 0;

                //clear the list of selected card
                imgsSelected.clear();
                forImgsSelected.clear();

                //recover the tips function
                tips.setOnClickListener(new JoinActivity.tipsClick());
            }
        }, 1500);
    }

    private void SETeffect(){
        //change to green if true
        imgsSelected.get(0).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.set, null));
        imgsSelected.get(1).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.set, null));
        imgsSelected.get(2).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.set, null));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //recover the color and status of all 12 cards rather than 3, because this 3 may be different with the 3 that server chooses
                /*//recover the color of 3 cards
                imgsSelected.get(0).setBackgroundColor(Color.WHITE);
                imgsSelected.get(1).setBackgroundColor(Color.WHITE);
                imgsSelected.get(2).setBackgroundColor(Color.WHITE);
                //recover the status of selected cards
                imgsSelected.get(0).setSelected(false);
                imgsSelected.get(1).setSelected(false);
                imgsSelected.get(2).setSelected(false);*/
                for(int i=0;i<12;i++) {
                    imgs[i].setBackgroundColor(Color.WHITE);
                    imgs[i].setSelected(false);
                }

                //12(3 new) or 12(3 new)+3(new)=15 or 15-3(selected)=12 or 15-3(selected)+3(new)=15
                showThreeNewCards();

                //reclickable all 12 cards
                selectedCount = 0;

                //clear the list of selected card
                imgsSelected.clear();
                forImgsSelected.clear();

                //recover the tips function
                tips.setOnClickListener(new JoinActivity.tipsClick());
            }
        }, 1500);

        UNfreezeAll();
    }

    private void NOTSETeffect(){
        //change to red if false
        imgsSelected.get(0).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.notset, null));
        imgsSelected.get(1).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.notset, null));
        imgsSelected.get(2).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.notset, null));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //reduce 2 point
                score=score-2;
                if(score<0)
                    score=0;

                //recover the color of 3 cards
                imgsSelected.get(0).setBackgroundColor(Color.WHITE);
                imgsSelected.get(1).setBackgroundColor(Color.WHITE);
                imgsSelected.get(2).setBackgroundColor(Color.WHITE);

                //reclickable all 12 cards
                selectedCount = 0;

                //recover the status of selected cards and clear the list of selected card
                imgsSelected.get(0).setSelected(false);
                imgsSelected.get(1).setSelected(false);
                imgsSelected.get(2).setSelected(false);
                imgsSelected.clear();
                forImgsSelected.clear();

                //recover the tips function
                //tips.setOnClickListener(new JoinActivity.tipsClick());
            }
        }, 1500);

        freezeAll();
    }

    private void freezeAll(){
        tips.setClickable(false);
        for(int i=0;i<12;i++)
            imgs[i].setClickable(false);
        if(imgs[12]!=null){
            for(int i=12;i<15;i++)
                imgs[i].setClickable(false);
        }
    }

    private void UNfreezeAll(){
        tips.setOnClickListener(new JoinActivity.tipsClick());
        for(int i=0;i<12;i++)
            imgs[i].setClickable(true);
        if(imgs[12]!=null){
            for(int i=12;i<15;i++)
                imgs[i].setClickable(true);
        }
    }

    //12(3 new) or 12(3 new)+3(new)=15 or 15-3(selected)=12 or 15-3(selected)+3(new)=15
    private void showThreeNewCards(){
        if(CardHeap.size()<3){
            freezeAll();
        }
        if(imgs[12]==null){
            if(imgsSelected.get(0).getId()==R.id.img0 || imgsSelected.get(1).getId()==R.id.img0 || imgsSelected.get(2).getId()==R.id.img0){
                imgs[0] = (CardPanel) findViewById(R.id.img0);
                forCard[0] = CardHeap.poll();
                imgs[0].card = new Card(forCard[0]);
                cs[0] = new Canvas();
                imgs[0].card.draw(cs[0],imgs[0].getWidth(),imgs[0].getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img1 || imgsSelected.get(1).getId()==R.id.img1 || imgsSelected.get(2).getId()==R.id.img1){
                imgs[1] = (CardPanel) findViewById(R.id.img1);
                forCard[1] = CardHeap.poll();
                imgs[1].card = new Card(forCard[1]);
                cs[1] = new Canvas();
                imgs[1].card.draw(cs[1],imgs[1].getWidth(),imgs[1].getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img2 || imgsSelected.get(1).getId()==R.id.img2 || imgsSelected.get(2).getId()==R.id.img2){
                imgs[2] = (CardPanel) findViewById(R.id.img2);
                forCard[2] = CardHeap.poll();
                imgs[2].card = new Card(forCard[2]);
                cs[2] = new Canvas();
                imgs[2].card.draw(cs[2],imgs[2].getWidth(),imgs[2].getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img3 || imgsSelected.get(1).getId()==R.id.img3 || imgsSelected.get(2).getId()==R.id.img3){
                imgs[3] = (CardPanel) findViewById(R.id.img3);
                forCard[3] = CardHeap.poll();
                imgs[3].card = new Card(forCard[3]);
                cs[3] = new Canvas();
                imgs[3].card.draw(cs[3],imgs[3].getWidth(),imgs[3].getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img4 || imgsSelected.get(1).getId()==R.id.img4 || imgsSelected.get(2).getId()==R.id.img4){
                imgs[4] = (CardPanel) findViewById(R.id.img4);
                forCard[4] = CardHeap.poll();
                imgs[4].card = new Card(forCard[4]);
                cs[4]= new Canvas();
                imgs[4].card.draw(cs[4],imgs[4].getWidth(),imgs[4].getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img5 || imgsSelected.get(1).getId()==R.id.img5 || imgsSelected.get(2).getId()==R.id.img5){
                imgs[5] = (CardPanel) findViewById(R.id.img5);
                forCard[5] = CardHeap.poll();
                imgs[5].card = new Card(forCard[5]);
                cs[5] = new Canvas();
                imgs[5].card.draw(cs[5],imgs[5].getWidth(),imgs[5].getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img6 || imgsSelected.get(1).getId()==R.id.img6 || imgsSelected.get(2).getId()==R.id.img6){
                imgs[6] = (CardPanel) findViewById(R.id.img6);
                forCard[6] = CardHeap.poll();
                imgs[6].card = new Card(forCard[6]);
                cs[6]= new Canvas();
                imgs[6].card.draw(cs[6],imgs[6].getWidth(),imgs[6].getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img7 || imgsSelected.get(1).getId()==R.id.img7 || imgsSelected.get(2).getId()==R.id.img7){
                imgs[7] = (CardPanel) findViewById(R.id.img7);
                forCard[7] = CardHeap.poll();
                imgs[7].card = new Card(forCard[7]);
                cs[7] = new Canvas();
                imgs[7].card.draw(cs[7],imgs[7].getWidth(),imgs[7].getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img8 || imgsSelected.get(1).getId()==R.id.img8 || imgsSelected.get(2).getId()==R.id.img8){
                imgs[8] = (CardPanel) findViewById(R.id.img8);
                forCard[8] = CardHeap.poll();
                imgs[8].card = new Card(forCard[8]);
                cs[8] = new Canvas();
                imgs[8].card.draw(cs[8],imgs[8].getWidth(),imgs[8].getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img9 || imgsSelected.get(1).getId()==R.id.img9 || imgsSelected.get(2).getId()==R.id.img9){
                imgs[9] = (CardPanel) findViewById(R.id.img9);
                forCard[9] = CardHeap.poll();
                imgs[9].card = new Card(forCard[9]);
                cs[9] = new Canvas();
                imgs[9].card.draw(cs[9],imgs[9].getWidth(),imgs[9].getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img10 || imgsSelected.get(1).getId()==R.id.img10 || imgsSelected.get(2).getId()==R.id.img10){
                imgs[10] = (CardPanel) findViewById(R.id.img10);
                forCard[10] = CardHeap.poll();
                imgs[10].card = new Card(forCard[10]);
                cs[10] = new Canvas();
                imgs[10].card.draw(cs[10],imgs[10].getWidth(),imgs[10].getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img11 || imgsSelected.get(1).getId()==R.id.img11 || imgsSelected.get(2).getId()==R.id.img11){
                imgs[11] = (CardPanel) findViewById(R.id.img11);
                forCard[11] = CardHeap.poll();
                imgs[11].card = new Card(forCard[11]);
                cs[11] = new Canvas();
                imgs[11].card.draw(cs[11],imgs[11].getWidth(),imgs[11].getHeight());
            }
        }
        else{ //for the 3 more cards
            LinkedList<Integer> tmp1 = new LinkedList<>();
            LinkedList<Integer> tmp2 = new LinkedList<>();
            for(CardPanel c : imgsSelected){
                if(c.getId()==R.id.img0)
                    tmp1.add(0);
                if(c.getId()==R.id.img1)
                    tmp1.add(1);
                if(c.getId()==R.id.img2)
                    tmp1.add(2);
                if(c.getId()==R.id.img3)
                    tmp1.add(3);
                if(c.getId()==R.id.img4)
                    tmp1.add(4);
                if(c.getId()==R.id.img5)
                    tmp1.add(5);
                if(c.getId()==R.id.img6)
                    tmp1.add(6);
                if(c.getId()==R.id.img7)
                    tmp1.add(7);
                if(c.getId()==R.id.img8)
                    tmp1.add(8);
                if(c.getId()==R.id.img9)
                    tmp1.add(9);
                if(c.getId()==R.id.img10)
                    tmp1.add(10);
                if(c.getId()==R.id.img11)
                    tmp1.add(11);
                if(c.getId()==R.id.img12)
                    tmp2.add(12);
                if(c.getId()==R.id.img13)
                    tmp2.add(13);
                if(c.getId()==R.id.img14)
                    tmp2.add(14);
            }
            for(int i=0;i<12;i++){
                if(!tmp1.isEmpty() && tmp1.contains(i)){
                    for(int j=12;j<15;j++){
                        if(!tmp2.contains(j)){
                            forCard[i] = imgs[j].card.getValue();
                            imgs[i].card = new Card(forCard[i]);
                            cs[i] =new Canvas();
                            imgs[i].card.draw(cs[i],imgs[i].getWidth(),imgs[i].getHeight());
                            tmp2.add(j);
                        }
                    }
                    tmp1.remove((Integer)i);
                }
            }

            //erase the 3 more cards
            for(int i=12;i<15;i++){
                cs[i] = new Canvas();
                imgs[i].card = new Card(-1);
                imgs[i].card.draw(cs[i], imgs[i].getWidth(), imgs[i].getHeight());
                imgs[i].setBackgroundColor(Color.TRANSPARENT);
                imgs[i].setClickable(false);
                imgs[i]=null;
            }
        }

        //add 12 to 15
        if(!Card.SETexist(forCard))
            ThreeMore();
        else{
            forTips.clear();
            result = Card.getSET(forCard);
            for(int i=0;i<12;i++)
                if(result.contains(i))
                    forTips.add(imgs[i]);
        }
    }
}
