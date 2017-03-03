package com.example.set;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.LinkedList;

public class Card {
	private int number;
	private int color;
	private int filling;
	private int shape;
	private int value; // value = number + 3 * (color + 3 * (filling + 3 * shape))

	Card(int value) {
		this.value = value;
		// compute the other attributes from value
		number = value%3;
		color = (value/3)%3;
		filling = (value/9)%3;
		shape = (value/27)%3;
	}

    public static LinkedList<Integer> getSET(int[] forCard){
        LinkedList<Integer> result = new LinkedList<>();
        int i=0;
        while(i<forCard.length-2){
            int j=i+1;
            while(j<forCard.length-1){
                int k=j+1;
                while(k<forCard.length){
                    if(isSet(new Card(forCard[i]), new Card(forCard[j]), new Card(forCard[k]))){
                        result.add(i);
                        result.add(j);
                        result.add(k);
                        return result;
                    }
                    k++;
                }
                j++;
            }
            i++;
        }
        return result;
    }


	public int getValue(){
		return this.value;
	}

	public static boolean SETexist(int[] forCard){
        if(forCard.length==3)
            return isSet(new Card(forCard[0]), new Card(forCard[1]), new Card(forCard[2]));
        else{
            int[] forCard1 = new int[forCard.length/2];
            int[] forCard2 = new int[forCard.length/2];
            for(int i=0;i<forCard.length/2;i++){
                forCard1[i] = forCard[i];
                forCard2[i] = forCard[forCard.length/2+i];
            }
            if(SETexist(forCard1))
                return true;
            else if(SETexist(forCard2))
                return true;
            else{
                for(int c : forCard1){
                    for(int i=0;i<forCard2.length-1;i++)
                        for(int j=i+1;j<forCard2.length;j++){
                            if(isSet(new Card(c), new Card(forCard2[i]), new Card(forCard2[j])))
                                return true;
                        }
                }
                for(int c : forCard2){
                    for(int i=0;i<forCard1.length-1;i++)
                        for(int j=i+1;j<forCard1.length;j++){
                            if(isSet(new Card(c), new Card(forCard1[i]), new Card(forCard1[j])))
                                return true;
                        }
                }
                return false;
            }
        }
    }

	public static boolean isSet(Card card1, Card card2, Card card3){
		return isSetNumber(card1.number, card2.number, card3.number) && isSetColor(card1.color, card2.color, card3.color) && isSetFilling(card1.filling, card2.filling, card3.filling) && isSetShape(card1.shape, card2.shape, card3.shape);
	}

	public static boolean isSetNumber(int number1, int number2, int number3){
		if(number1==number2 && number2==number3)
			return true;
		if(number1!=number2 && number2!=number3 && number3!=number1)
			return true;
		return false;
	}

	public static boolean isSetColor(int color1, int color2, int color3){
		if(color1==color2 && color2==color3)
			return true;
		if(color1!=color2 && color2!=color3 && color3!=color1)
			return true;
		return false;
	}

	public static boolean isSetFilling(int filling1, int filling2, int filling3){
		if(filling1==filling2 && filling2==filling3)
			return true;
		if(filling1!=filling2 && filling2!=filling3 && filling3!=filling1)
			return true;
		return false;
	}

	public static boolean isSetShape(int shape1, int shape2, int shape3){
		if(shape1==shape2 && shape2==shape3)
			return true;
		if(shape1!=shape2 && shape2!=shape3 && shape3!=shape1)
			return true;
		return false;
	}

	// equality test on SetCards
	public boolean equals(Object o) {
		Card c = (Card) o;
		return (value == c.value);
	}

	// return the characteristics of the card
	int[] characteristics() {
		return new int[] {number, color, filling, shape};
	}

	// draw the card on the Canvas c
	// different copies of the same shape are drawn, according to this.number
	void draw(Canvas c, int width, int height) {
		if(value!=-1){ //for erase the picture drawn
			// set the default paint style
			Paint p = new Paint();
			setColor(p);
			// computes the topmost point
			int startY = (36 - 16*number)*height/96;
			// draw as many shapes as this.number
			for (int i = 0; i <= number; i++) {
				RectF r = new RectF(width/8, startY + i * height/3, width*7/8, startY + i * height/3 + height/4);
				drawFilledShape(c, p, r);
			}
		}
	}

	// set the color of the Paint p according to this.color
	private void setColor(Paint p) {
		switch (color) {
		case 0: p.setColor(Color.RED); break;
		case 1: p.setColor(Color.BLUE); break;
		case 2: p.setColor(Color.GREEN); break;
		default: new Error("invalid color");
		}
	}

	// draw the desired shape (on the Canvas c, with the Paint p) according to this.shape
	private void drawShape(Canvas c, Paint p, RectF r) {
		switch (shape) {
		case 0: c.drawOval(r, p); break;
		case 1: c.drawRect(r, p); break;
		case 2: c.drawPath(diamond(r), p); break;
		default: new Error("invalid shape");
		}
	}

	// draw the desired shape (on the Canvas c, with the Paint p) with the correct filling according to this.filling
	private void drawFilledShape(Canvas c, Paint p, RectF r) {
		switch (filling) {
		case 0: p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(2); drawShape(c, p, r); break;
		case 1:
			// in case of intermediate filling, we draw concentric copies of the same shape
			p.setStyle(Paint.Style.STROKE);
            for (int i = 0; i < r.width()/2; i+=6) {
				drawShape(c, p, new RectF(r.left + i, r.top + i * r.height()/r.width(), r.right - i, r.bottom - i * r.height()/r.width()));
			}
			break;
		case 2: p.setStyle(Paint.Style.FILL); drawShape(c, p, r); break;
		default: new Error("invalid filling");
		}
	}
	
	// creates a diamond in the rectangle r
	private Path diamond(RectF r) {
		Path p = new Path();
		p.moveTo(r.left, r.centerY());
		p.lineTo(r.centerX(), r.top);
		p.lineTo(r.right, r.centerY());
		p.lineTo(r.centerX(), r.bottom);
		p.lineTo(r.left, r.centerY());
		return p;
	}
}
