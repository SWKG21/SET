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

import java.text.DecimalFormat;
import java.util.LinkedList;

public class ModifiedSingleActivity extends AppCompatActivity {

    LinkedList<Integer> CardHeap = new LinkedList<>();
    int[] forCard = new int[12];
    private int selectedCount = 0;
    private CardPanel[] imgs = new CardPanel[15];
    LinkedList<CardPanel> imgsSelected = new LinkedList<>();
    private TextView textTime;
    private int recLen = 0;
    private TextView textScore;
    private int score = 0;
    Canvas[] cs = new Canvas[15];
    LinearLayout tips;
    LinkedList<Integer> result = new LinkedList<>();
    LinkedList<CardPanel> forTips = new LinkedList<>();
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modified_single);

        //generate 81 cards with the random order
        generateCardHeap();

        //show out the first 12 cards
        show12Cards();

        //set the click function for the 12 cards
        for(int i=0;i<12;i++)
            imgs[i].setOnClickListener(new ModifiedSingleActivity.cardClick());

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
        tips.setOnClickListener(new ModifiedSingleActivity.tipsClick());

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
                        checkSET();
                    }
                }
                else
                    v.setClickable(false);
            }
        }
    }

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
            imgs[i].setOnClickListener(new ModifiedSingleActivity.cardClick());
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

    //check whether the combination of the 3 cards is SET
    private void checkSET(){
        if(Card.isSet(imgsSelected.get(0).card, imgsSelected.get(1).card, imgsSelected.get(2).card)){
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
                    UNfreezeAll();

                    //clear the list of selected card
                    imgsSelected.clear();

                    //recover the tips function
                    tips.setOnClickListener(new ModifiedSingleActivity.tipsClick());
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
                    UNfreezeAll();

                    //recover the status of selected cards and clear the list of selected card
                    imgsSelected.get(0).setSelected(false);
                    imgsSelected.get(1).setSelected(false);
                    imgsSelected.get(2).setSelected(false);
                    imgsSelected.clear();

                    //recover the tips function
                    tips.setOnClickListener(new ModifiedSingleActivity.tipsClick());
                }
            }, 1500);
        }
    }

    private void UNfreezeAll(){
        tips.setClickable(true);
        for(int i=0;i<12;i++)
            imgs[i].setClickable(true);
        if(imgs[12]!=null){
            for(int i=12;i<15;i++)
                imgs[i].setClickable(true);
        }
    }

    //12(3 new) or 12(3 new)+3(new)=15 or 15-3(selected)=12 or 15-3(selected)+3(new)=15
    private void showThreeNewCards(){
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
