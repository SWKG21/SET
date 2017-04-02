package com.example.set;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class PlayAsServerActivity extends AppCompatActivity {

    LinkedList<Integer> CardHeap = new LinkedList<>();
    int [] forCard = new int[12];
    private int selectedCount = 0;
    private LinkedList<CardPanel> imgs = new LinkedList<>();
    LinkedList<CardPanel> imgsSelected = new LinkedList<>();
    private TextView textTime;
    private int recLen = 0;
    private TextView textScore;
    private int score = 0;
    CardPanel img0;
    CardPanel img1;
    CardPanel img2;
    CardPanel img3;
    CardPanel img4;
    CardPanel img5;
    CardPanel img6;
    CardPanel img7;
    CardPanel img8;
    CardPanel img9;
    CardPanel img10;
    CardPanel img11;
    CardPanel img12;
    CardPanel img13;
    CardPanel img14;
    Canvas c0 = new Canvas();
    Canvas c1 = new Canvas();
    Canvas c2 = new Canvas();
    Canvas c3 = new Canvas();
    Canvas c4 = new Canvas();
    Canvas c5 = new Canvas();
    Canvas c6 = new Canvas();
    Canvas c7 = new Canvas();
    Canvas c8 = new Canvas();
    Canvas c9 = new Canvas();
    Canvas c10 = new Canvas();
    Canvas c11 = new Canvas();
    Canvas c12 = new Canvas();
    Canvas c13 = new Canvas();
    Canvas c14 = new Canvas();
    LinearLayout tips;
    LinkedList<Integer> result = new LinkedList<>();
    LinkedList<CardPanel> forTips = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_as_server);

        /*//this way doesn't work
        ImageView img0 = (ImageView) findViewById(R.id.img0);
        int valueOfCard0 = Cards.valueOf(1,1,1,1);
        CardDrawable cd0 = new CardDrawable(valueOfCard0);
        img0.setImageDrawable(cd0);
        Canvas c = new Canvas();
        cd0.draw(c);*/

        //generate 81 cards with the random order
        generateCardHeap();

        //send CardHeap to client
        /*try {
            String s0 = CardHeap.toString();
            OutputStream os = socket.getOutputStream();
            os.write(s0.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //show out the first 12 cards
        show12Cards();

        //add 12 CardPanels to imgs
        addTOimgs();

        //set the click function for the 12 cards
        for(CardPanel img : imgs)
            img.setOnClickListener(new PlayAsServerActivity.cardClick());

        //whether need to add 3 more cards
        if(!Card.SETexist(forCard))
            ThreeMore();
        else{
            forTips.clear();
            result = Card.getSET(forCard);
            if(result.contains(0))
                forTips.add(img0);
            if(result.contains(1))
                forTips.add(img1);
            if(result.contains(2))
                forTips.add(img2);
            if(result.contains(3))
                forTips.add(img3);
            if(result.contains(4))
                forTips.add(img4);
            if(result.contains(5))
                forTips.add(img5);
            if(result.contains(6))
                forTips.add(img6);
            if(result.contains(7))
                forTips.add(img7);
            if(result.contains(8))
                forTips.add(img8);
            if(result.contains(9))
                forTips.add(img9);
            if(result.contains(10))
                forTips.add(img10);
            if(result.contains(11))
                forTips.add(img11);
        }

        //add result panel
        tips = (LinearLayout) findViewById(R.id.tips);
        tips.setOnClickListener(new PlayAsServerActivity.tipsClick());

        //add time
        textTime = (TextView) findViewById(R.id.textTime);
        textTime.setTextColor(Color.BLACK);
        handler.postDelayed(runnable, 1000);

        //add score
        textScore = (TextView) findViewById(R.id.textScore);
        textScore.setTextColor(Color.BLACK);


    }

    //the tips function
    class tipsClick implements View.OnClickListener{
        @Override
        public void onClick(View v){
            if(img12==null && !forTips.isEmpty()){
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
                        checkSET();
                    }
                }
                else
                    v.setClickable(false);
            }
        }
    }

    //add time and score
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
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
    };

    //generate 81 cards with the random order
    private void generateCardHeap(){
        for(int i=0;i<81;i++){
            int size = CardHeap.size();
            int index = (int)(Math.random()*size);
            CardHeap.add(index,i);
        }
    }

    //show out the first 12 cards
    private void show12Cards(){
        for(int i=0;i<12;i++)
            forCard[i] = CardHeap.poll();

        img0 = (CardPanel) findViewById(R.id.img0);
        img0.card = new Card(forCard[0]);
        c0 = new Canvas();
        img0.card.draw(c0,img0.getWidth(),img0.getHeight());

        img1 = (CardPanel) findViewById(R.id.img1);
        img1.card = new Card(forCard[1]);
        c1 = new Canvas();
        img1.card.draw(c1,img1.getWidth(),img1.getHeight());

        img2 = (CardPanel) findViewById(R.id.img2);
        img2.card = new Card(forCard[2]);
        c2 = new Canvas();
        img2.card.draw(c2,img2.getWidth(),img2.getHeight());

        img3 = (CardPanel) findViewById(R.id.img3);
        img3.card = new Card(forCard[3]);
        c3 = new Canvas();
        img3.card.draw(c3,img3.getWidth(),img3.getHeight());

        img4 = (CardPanel) findViewById(R.id.img4);
        img4.card = new Card(forCard[4]);
        c4= new Canvas();
        img4.card.draw(c4,img4.getWidth(),img4.getHeight());

        img5 = (CardPanel) findViewById(R.id.img5);
        img5.card = new Card(forCard[5]);
        c5 = new Canvas();
        img5.card.draw(c5,img5.getWidth(),img5.getHeight());

        img6 = (CardPanel) findViewById(R.id.img6);
        img6.card = new Card(forCard[6]);
        c6= new Canvas();
        img6.card.draw(c6,img6.getWidth(),img6.getHeight());

        img7 = (CardPanel) findViewById(R.id.img7);
        img7.card = new Card(forCard[7]);
        c7 = new Canvas();
        img7.card.draw(c7,img7.getWidth(),img7.getHeight());

        img8 = (CardPanel) findViewById(R.id.img8);
        img8.card = new Card(forCard[8]);
        c8 = new Canvas();
        img8.card.draw(c8,img8.getWidth(),img8.getHeight());

        img9 = (CardPanel) findViewById(R.id.img9);
        img9.card = new Card(forCard[9]);
        c9 = new Canvas();
        img9.card.draw(c9,img9.getWidth(),img9.getHeight());

        img10 = (CardPanel) findViewById(R.id.img10);
        img10.card = new Card(forCard[10]);c10 = new Canvas();
        img10.card.draw(c10,img10.getWidth(),img10.getHeight());

        img11 = (CardPanel) findViewById(R.id.img11);
        img11.card = new Card(forCard[11]);
        c11 = new Canvas();
        img11.card.draw(c11,img11.getWidth(),img11.getHeight());
    }

    //add 12 CardPanels to imgs
    private void addTOimgs(){
        imgs.add(img0);
        imgs.add(img1);
        imgs.add(img2);
        imgs.add(img3);
        imgs.add(img4);
        imgs.add(img5);
        imgs.add(img6);
        imgs.add(img7);
        imgs.add(img8);
        imgs.add(img9);
        imgs.add(img10);
        imgs.add(img11);
    }

    private void ThreeMore(){
        img12 = (CardPanel) findViewById(R.id.img12);
        img12.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorCard, null));
        img12.card = new Card(CardHeap.poll());
        c12 = new Canvas();
        img12.card.draw(c12,img12.getWidth(),img12.getHeight());

        img13 = (CardPanel) findViewById(R.id.img13);
        img13.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorCard, null));
        img13.card = new Card(CardHeap.poll());
        c13 = new Canvas();
        img13.card.draw(c13,img13.getWidth(),img13.getHeight());

        img14 = (CardPanel) findViewById(R.id.img14);
        img14.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorCard, null));
        img14.card = new Card(CardHeap.poll());
        c14 = new Canvas();
        img14.card.draw(c14,img14.getWidth(),img14.getHeight());

        img12.setOnClickListener(new PlayAsServerActivity.cardClick());
        img13.setOnClickListener(new PlayAsServerActivity.cardClick());
        img14.setOnClickListener(new PlayAsServerActivity.cardClick());
    }

    //capture 3 selected cards
    private void addSelected(){
        for(CardPanel img : imgs){
            if(img.isSelected()) {
                imgsSelected.add(img);
            }
        }
        if(img12!=null){
            if(img12.isSelected())
                imgsSelected.add(img12);
            if(img13.isSelected())
                imgsSelected.add(img13);
            if(img14.isSelected())
                imgsSelected.add(img14);
        }
    }

    //check whether the combination of the 3 cards is SET
    private void checkSET(){
        if(Card.isSet(imgsSelected.get(0).card, imgsSelected.get(1).card, imgsSelected.get(2).card)){
            //send result of server to client
           /* try {
                String s0 = "server true";
                OutputStream os = socket.getOutputStream();
                os.write(s0.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }*/

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

                    //recover the tips function
                    tips.setOnClickListener(new PlayAsServerActivity.tipsClick());
                }
            }, 1500);

        }
        else{
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

                    //recover the tips function
                    tips.setOnClickListener(new PlayAsServerActivity.tipsClick());
                }
            }, 1500);
        }
    }

    //12(3 new) or 12(3 new)+3(new)=15 or 15-3(selected)=12 or 15-3(selected)+3(new)=15
    private void showThreeNewCards(){
        if(img12==null){
            if(imgsSelected.get(0).getId()==R.id.img0 || imgsSelected.get(1).getId()==R.id.img0 || imgsSelected.get(2).getId()==R.id.img0){
                img0 = (CardPanel) findViewById(R.id.img0);
                forCard[0] = CardHeap.poll();
                img0.card = new Card(forCard[0]);
                c0 = new Canvas();
                img0.card.draw(c0,img0.getWidth(),img0.getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img1 || imgsSelected.get(1).getId()==R.id.img1 || imgsSelected.get(2).getId()==R.id.img1){
                img1 = (CardPanel) findViewById(R.id.img1);
                forCard[1] = CardHeap.poll();
                img1.card = new Card(forCard[1]);
                c1 = new Canvas();
                img1.card.draw(c1,img1.getWidth(),img1.getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img2 || imgsSelected.get(1).getId()==R.id.img2 || imgsSelected.get(2).getId()==R.id.img2){
                img2 = (CardPanel) findViewById(R.id.img2);
                forCard[2] = CardHeap.poll();
                img2.card = new Card(forCard[2]);
                c2 = new Canvas();
                img2.card.draw(c2,img2.getWidth(),img2.getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img3 || imgsSelected.get(1).getId()==R.id.img3 || imgsSelected.get(2).getId()==R.id.img3){
                img3 = (CardPanel) findViewById(R.id.img3);
                forCard[3] = CardHeap.poll();
                img3.card = new Card(forCard[3]);
                c3 = new Canvas();
                img3.card.draw(c3,img3.getWidth(),img3.getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img4 || imgsSelected.get(1).getId()==R.id.img4 || imgsSelected.get(2).getId()==R.id.img4){
                img4 = (CardPanel) findViewById(R.id.img4);
                forCard[4] = CardHeap.poll();
                img4.card = new Card(forCard[4]);
                c4= new Canvas();
                img4.card.draw(c4,img4.getWidth(),img4.getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img5 || imgsSelected.get(1).getId()==R.id.img5 || imgsSelected.get(2).getId()==R.id.img5){
                img5 = (CardPanel) findViewById(R.id.img5);
                forCard[5] = CardHeap.poll();
                img5.card = new Card(forCard[5]);
                c5 = new Canvas();
                img5.card.draw(c5,img5.getWidth(),img5.getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img6 || imgsSelected.get(1).getId()==R.id.img6 || imgsSelected.get(2).getId()==R.id.img6){
                img6 = (CardPanel) findViewById(R.id.img6);
                forCard[6] = CardHeap.poll();
                img6.card = new Card(forCard[6]);
                c6= new Canvas();
                img6.card.draw(c6,img6.getWidth(),img6.getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img7 || imgsSelected.get(1).getId()==R.id.img7 || imgsSelected.get(2).getId()==R.id.img7){
                img7 = (CardPanel) findViewById(R.id.img7);
                forCard[7] = CardHeap.poll();
                img7.card = new Card(forCard[7]);
                c7 = new Canvas();
                img7.card.draw(c7,img7.getWidth(),img7.getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img8 || imgsSelected.get(1).getId()==R.id.img8 || imgsSelected.get(2).getId()==R.id.img8){
                img8 = (CardPanel) findViewById(R.id.img8);
                forCard[8] = CardHeap.poll();
                img8.card = new Card(forCard[8]);
                c8 = new Canvas();
                img8.card.draw(c8,img8.getWidth(),img8.getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img9 || imgsSelected.get(1).getId()==R.id.img9 || imgsSelected.get(2).getId()==R.id.img9){
                img9 = (CardPanel) findViewById(R.id.img9);
                forCard[9] = CardHeap.poll();
                img9.card = new Card(forCard[9]);
                c9 = new Canvas();
                img9.card.draw(c9,img9.getWidth(),img9.getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img10 || imgsSelected.get(1).getId()==R.id.img10 || imgsSelected.get(2).getId()==R.id.img10){
                img10 = (CardPanel) findViewById(R.id.img10);
                forCard[10] = CardHeap.poll();
                img10.card = new Card(forCard[10]);
                c10 = new Canvas();
                img10.card.draw(c10,img10.getWidth(),img10.getHeight());
            }
            if(imgsSelected.get(0).getId()==R.id.img11 || imgsSelected.get(1).getId()==R.id.img11 || imgsSelected.get(2).getId()==R.id.img11){
                img11 = (CardPanel) findViewById(R.id.img11);
                forCard[11] = CardHeap.poll();
                img11.card = new Card(forCard[11]);
                c11 = new Canvas();
                img11.card.draw(c11,img11.getWidth(),img11.getHeight());
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
            if(!tmp1.isEmpty() && tmp1.contains(0)){
                if(!tmp2.contains(12)){
                    img0 = (CardPanel) findViewById(R.id.img0);
                    forCard[0] = img12.card.getValue();
                    img0.card = new Card(forCard[0]);
                    c0 = new Canvas();
                    img0.card.draw(c0,img0.getWidth(),img0.getHeight());
                    tmp2.add(12);
                }
                else if(!tmp2.contains(13)){
                    img0 = (CardPanel) findViewById(R.id.img0);
                    forCard[0] = img13.card.getValue();
                    img0.card = new Card(forCard[0]);
                    c0 = new Canvas();
                    img0.card.draw(c0,img0.getWidth(),img0.getHeight());
                    tmp2.add(13);
                }
                else{
                    img0 = (CardPanel) findViewById(R.id.img0);
                    forCard[0] = img14.card.getValue();
                    img0.card = new Card(forCard[0]);
                    c0 = new Canvas();
                    img0.card.draw(c0,img0.getWidth(),img0.getHeight());
                    tmp2.add(14);
                }
                tmp1.remove((Integer)0);
            }
            if(!tmp1.isEmpty() && tmp1.contains(1)){
                if(!tmp2.contains(12)){
                    img1 = (CardPanel) findViewById(R.id.img1);
                    forCard[1] = img12.card.getValue();
                    img1.card = new Card(forCard[1]);
                    c1 = new Canvas();
                    img1.card.draw(c1,img0.getWidth(),img1.getHeight());
                    tmp2.add(12);
                }
                else if(!tmp2.contains(13)){
                    img1 = (CardPanel) findViewById(R.id.img1);
                    forCard[1] = img13.card.getValue();
                    img1.card = new Card(forCard[1]);
                    c1 = new Canvas();
                    img1.card.draw(c1,img0.getWidth(),img1.getHeight());
                    tmp2.add(13);
                }
                else{
                    img1 = (CardPanel) findViewById(R.id.img1);
                    forCard[1] = img14.card.getValue();
                    img1.card = new Card(forCard[1]);
                    c1 = new Canvas();
                    img1.card.draw(c1,img0.getWidth(),img1.getHeight());
                    tmp2.add(14);
                }
                tmp1.remove((Integer)1);
            }
            if(!tmp1.isEmpty() && tmp1.contains(2)){
                if(!tmp2.contains(12)){
                    img2 = (CardPanel) findViewById(R.id.img2);
                    forCard[2] = img12.card.getValue();
                    img2.card = new Card(forCard[2]);
                    c2 = new Canvas();
                    img2.card.draw(c2,img2.getWidth(),img2.getHeight());
                    tmp2.add(12);
                }
                else if(!tmp2.contains(13)){
                    img2 = (CardPanel) findViewById(R.id.img2);
                    forCard[2] = img13.card.getValue();
                    img2.card = new Card(forCard[2]);
                    c2 = new Canvas();
                    img2.card.draw(c2,img2.getWidth(),img2.getHeight());
                    tmp2.add(13);
                }
                else{
                    img2 = (CardPanel) findViewById(R.id.img2);
                    forCard[2] = img14.card.getValue();
                    img2.card = new Card(forCard[2]);
                    c2 = new Canvas();
                    img2.card.draw(c2,img2.getWidth(),img2.getHeight());
                    tmp2.add(14);
                }
                tmp1.remove((Integer)2);
            }
            if(!tmp1.isEmpty() && tmp1.contains(3)){
                if(!tmp2.contains(12)){
                    img3 = (CardPanel) findViewById(R.id.img3);
                    forCard[3] = img12.card.getValue();
                    img3.card = new Card(forCard[3]);
                    c3 = new Canvas();
                    img3.card.draw(c3,img3.getWidth(),img3.getHeight());
                    tmp2.add(12);
                }
                else if(!tmp2.contains(13)){
                    img3 = (CardPanel) findViewById(R.id.img3);
                    forCard[3] = img13.card.getValue();
                    img3.card = new Card(forCard[3]);
                    c3 = new Canvas();
                    img3.card.draw(c3,img3.getWidth(),img3.getHeight());
                    tmp2.add(13);
                }
                else{
                    img3 = (CardPanel) findViewById(R.id.img3);
                    forCard[3] = img14.card.getValue();
                    img3.card = new Card(forCard[3]);
                    c3 = new Canvas();
                    img3.card.draw(c3,img3.getWidth(),img3.getHeight());
                    tmp2.add(14);
                }
                tmp1.remove((Integer)3);
            }
            if(!tmp1.isEmpty() && tmp1.contains(4)){
                if(!tmp2.contains(12)){
                    img4 = (CardPanel) findViewById(R.id.img4);
                    forCard[4] = img12.card.getValue();
                    img4.card = new Card(forCard[4]);
                    c4= new Canvas();
                    img4.card.draw(c4,img4.getWidth(),img4.getHeight());
                    tmp2.add(12);
                }
                else if(!tmp2.contains(13)){
                    img4 = (CardPanel) findViewById(R.id.img4);
                    forCard[4] = img13.card.getValue();
                    img4.card = new Card(forCard[4]);
                    c4= new Canvas();
                    img4.card.draw(c4,img4.getWidth(),img4.getHeight());
                    tmp2.add(13);
                }
                else{
                    img4 = (CardPanel) findViewById(R.id.img4);
                    forCard[4] = img14.card.getValue();
                    img4.card = new Card(forCard[4]);
                    c4= new Canvas();
                    img4.card.draw(c4,img4.getWidth(),img4.getHeight());
                    tmp2.add(14);
                }
                tmp1.remove((Integer)4);
            }
            if(!tmp1.isEmpty() && tmp1.contains(5)){
                if(!tmp2.contains(12)){
                    img5 = (CardPanel) findViewById(R.id.img5);
                    forCard[5] = img12.card.getValue();
                    img5.card = new Card(forCard[5]);
                    c5 = new Canvas();
                    img5.card.draw(c5,img5.getWidth(),img5.getHeight());
                    tmp2.add(12);
                }
                else if(!tmp2.contains(13)){
                    img5 = (CardPanel) findViewById(R.id.img5);
                    forCard[5] = img13.card.getValue();
                    img5.card = new Card(forCard[5]);
                    c5 = new Canvas();
                    img5.card.draw(c5,img5.getWidth(),img5.getHeight());
                    tmp2.add(13);
                }
                else{
                    img5 = (CardPanel) findViewById(R.id.img5);
                    forCard[5] = img14.card.getValue();
                    img5.card = new Card(forCard[5]);
                    c5 = new Canvas();
                    img5.card.draw(c5,img5.getWidth(),img5.getHeight());
                    tmp2.add(14);
                }
                tmp1.remove((Integer)5);
            }
            if(!tmp1.isEmpty() && tmp1.contains(6)){
                if(!tmp2.contains(12)){
                    img6 = (CardPanel) findViewById(R.id.img6);
                    forCard[6] = img12.card.getValue();
                    img6.card = new Card(forCard[6]);
                    c6= new Canvas();
                    img6.card.draw(c6,img6.getWidth(),img6.getHeight());
                    tmp2.add(12);
                }
                else if(!tmp2.contains(13)){
                    img6 = (CardPanel) findViewById(R.id.img6);
                    forCard[6] = img13.card.getValue();
                    img6.card = new Card(forCard[6]);
                    c6= new Canvas();
                    img6.card.draw(c6,img6.getWidth(),img6.getHeight());
                    tmp2.add(13);
                }
                else{
                    img6 = (CardPanel) findViewById(R.id.img6);
                    forCard[6] = img14.card.getValue();
                    img6.card = new Card(forCard[6]);
                    c6= new Canvas();
                    img6.card.draw(c6,img6.getWidth(),img6.getHeight());
                    tmp2.add(14);
                }
                tmp1.remove((Integer)6);
            }
            if(!tmp1.isEmpty() && tmp1.contains(7)){
                if(!tmp2.contains(12)){
                    img7 = (CardPanel) findViewById(R.id.img7);
                    forCard[7] = img12.card.getValue();
                    img7.card = new Card(forCard[7]);
                    c7 = new Canvas();
                    img7.card.draw(c7,img7.getWidth(),img7.getHeight());
                    tmp2.add(12);
                }
                else if(!tmp2.contains(13)){
                    img7 = (CardPanel) findViewById(R.id.img7);
                    forCard[7] = img13.card.getValue();
                    img7.card = new Card(forCard[7]);
                    c7 = new Canvas();
                    img7.card.draw(c7,img7.getWidth(),img7.getHeight());
                    tmp2.add(13);
                }
                else{
                    img7 = (CardPanel) findViewById(R.id.img7);
                    forCard[7] = img14.card.getValue();
                    img7.card = new Card(forCard[7]);
                    c7 = new Canvas();
                    img7.card.draw(c7,img7.getWidth(),img7.getHeight());
                    tmp2.add(14);
                }
                tmp1.remove((Integer)7);
            }
            if(!tmp1.isEmpty() && tmp1.contains(8)){
                if(!tmp2.contains(12)){
                    img8 = (CardPanel) findViewById(R.id.img8);
                    forCard[8] = img12.card.getValue();
                    img8.card = new Card(forCard[8]);
                    c8 = new Canvas();
                    img8.card.draw(c8,img8.getWidth(),img8.getHeight());
                    tmp2.add(12);
                }
                else if(!tmp2.contains(13)){
                    img8 = (CardPanel) findViewById(R.id.img8);
                    forCard[8] = img13.card.getValue();
                    img8.card = new Card(forCard[8]);
                    c8 = new Canvas();
                    img8.card.draw(c8,img8.getWidth(),img8.getHeight());
                    tmp2.add(13);
                }
                else{
                    img8 = (CardPanel) findViewById(R.id.img8);
                    forCard[8] = img14.card.getValue();
                    img8.card = new Card(forCard[8]);
                    c8 = new Canvas();
                    img8.card.draw(c8,img8.getWidth(),img8.getHeight());
                    tmp2.add(14);
                }
                tmp1.remove((Integer)8);
            }
            if(!tmp1.isEmpty() && tmp1.contains(9)){
                if(!tmp2.contains(12)){
                    img9 = (CardPanel) findViewById(R.id.img9);
                    forCard[9] = img12.card.getValue();
                    img9.card = new Card(forCard[9]);
                    c9 = new Canvas();
                    img9.card.draw(c9,img9.getWidth(),img9.getHeight());
                    tmp2.add(12);
                }
                else if(!tmp2.contains(13)){
                    img9 = (CardPanel) findViewById(R.id.img9);
                    forCard[9] = img13.card.getValue();
                    img9.card = new Card(forCard[9]);
                    c9 = new Canvas();
                    img9.card.draw(c9,img9.getWidth(),img9.getHeight());
                    tmp2.add(13);
                }
                else{
                    img9 = (CardPanel) findViewById(R.id.img9);
                    forCard[9] = img14.card.getValue();
                    img9.card = new Card(forCard[9]);
                    c9 = new Canvas();
                    img9.card.draw(c9,img9.getWidth(),img9.getHeight());
                    tmp2.add(14);
                }
                tmp1.remove((Integer)9);
            }
            if(!tmp1.isEmpty() && tmp1.contains(10)){
                if(!tmp2.contains(12)){
                    img10 = (CardPanel) findViewById(R.id.img10);
                    forCard[10] = img12.card.getValue();
                    img10.card = new Card(forCard[10]);
                    c10 = new Canvas();
                    img10.card.draw(c10,img10.getWidth(),img10.getHeight());
                    tmp2.add(12);
                }
                else if(!tmp2.contains(13)){
                    img10 = (CardPanel) findViewById(R.id.img10);
                    forCard[10] = img13.card.getValue();
                    img10.card = new Card(forCard[10]);
                    c10 = new Canvas();
                    img10.card.draw(c10,img10.getWidth(),img10.getHeight());
                    tmp2.add(13);
                }
                else{
                    img10 = (CardPanel) findViewById(R.id.img10);
                    forCard[10] = img14.card.getValue();
                    img10.card = new Card(forCard[10]);
                    c10 = new Canvas();
                    img10.card.draw(c10,img10.getWidth(),img10.getHeight());
                    tmp2.add(14);
                }
                tmp1.remove((Integer)10);
            }
            if(!tmp1.isEmpty() && tmp1.contains(11)){
                if(!tmp2.contains(12)){
                    img11 = (CardPanel) findViewById(R.id.img11);
                    forCard[11] = img12.card.getValue();
                    img11.card = new Card(forCard[11]);
                    c11 = new Canvas();
                    img11.card.draw(c11,img11.getWidth(),img11.getHeight());
                    tmp2.add(12);
                }
                else if(!tmp2.contains(13)){
                    img11 = (CardPanel) findViewById(R.id.img11);
                    forCard[11] = img13.card.getValue();
                    img11.card = new Card(forCard[11]);
                    c11 = new Canvas();
                    img11.card.draw(c11,img11.getWidth(),img11.getHeight());
                    tmp2.add(13);
                }
                else{
                    img11 = (CardPanel) findViewById(R.id.img11);
                    forCard[11] = img14.card.getValue();
                    img11.card = new Card(forCard[11]);
                    c11 = new Canvas();
                    img11.card.draw(c11,img11.getWidth(),img11.getHeight());
                    tmp2.add(14);
                }
                tmp1.remove((Integer)11);
            }

            //erase the 3 more cards
            c12 = new Canvas();
            img12.card = new Card(-1);
            img12.card.draw(c12, img12.getWidth(), img12.getHeight());
            img12.setBackgroundColor(Color.TRANSPARENT);
            img12.setClickable(false);
            img12=null;

            c13 = new Canvas();
            img13.card = new Card(-1);
            img13.card.draw(c13, img13.getWidth(), img13.getHeight());
            img13.setBackgroundColor(Color.TRANSPARENT);
            img13.setClickable(false);
            img13=null;

            c14 = new Canvas();
            img14.card = new Card(-1);
            img14.card.draw(c14, img14.getWidth(), img14.getHeight());
            img14.setBackgroundColor(Color.TRANSPARENT);
            img14.setClickable(false);
            img14=null;
        }

        //add 12 to 15
        if(!Card.SETexist(forCard))
            ThreeMore();
        else{
            forTips.clear();
            result = Card.getSET(forCard);
            if(result.contains(0))
                forTips.add(img0);
            if(result.contains(1))
                forTips.add(img1);
            if(result.contains(2))
                forTips.add(img2);
            if(result.contains(3))
                forTips.add(img3);
            if(result.contains(4))
                forTips.add(img4);
            if(result.contains(5))
                forTips.add(img5);
            if(result.contains(6))
                forTips.add(img6);
            if(result.contains(7))
                forTips.add(img7);
            if(result.contains(8))
                forTips.add(img8);
            if(result.contains(9))
                forTips.add(img9);
            if(result.contains(10))
                forTips.add(img10);
            if(result.contains(11))
                forTips.add(img11);
        }
    }

}
