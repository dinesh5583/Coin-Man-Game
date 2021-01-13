package com.mygame.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch; //How to display image in screen
	Texture background;   //Display image in screen
	Texture[] man;
	int manState=0;
	int pause=0;
	float gravity=0.2f;
    float velocity=0;
    int manY=0;
    Rectangle manRectangle;
	Texture dizzy;
    int score=0;
    int gameState=0;
    BitmapFont font; //Display text on screen

    ArrayList<Integer> coinXs=new ArrayList<>();
	ArrayList<Integer> coinYs=new ArrayList<>();
	ArrayList<Rectangle> coinRectangle=new ArrayList<Rectangle>();
	Texture coin;
	int coinCount;


	ArrayList<Integer> bombXs=new ArrayList<>();
	ArrayList<Integer> bombYs=new ArrayList<>();
	ArrayList<Rectangle> bombRectangle=new ArrayList<Rectangle>();
	Texture bomb;


	int bombCount;
	Random random;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background=new Texture("bg.png");
		man=new Texture[4];
		man[0]=new Texture("frame-1.png");
		man[1]=new Texture("frame-2.png");
		man[2]=new Texture("frame-3.png");
		man[3]=new Texture("frame-4.png");
		manY=Gdx.graphics.getHeight()/2;

		dizzy=new Texture("dizzy-1.png");

		coin=new Texture("coin.png");
		bomb=new Texture("bomb.png");
		random=new Random();

		font=new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);  //Size of text
	}

	public void makeCoin()   //Display Coin on screen
	{
		float Height=random.nextFloat()*Gdx.graphics.getHeight();
		coinYs.add((int)Height);
		coinXs.add(Gdx.graphics.getWidth());
	}
	public void makeBomb()   //Display Bomb on screen
	{
		float Height=random.nextFloat()*Gdx.graphics.getHeight();
		bombYs.add((int)Height);
		bombXs.add(Gdx.graphics.getWidth());
	}


	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState==1){
			//Game is live

			//For Bomb
			if(bombCount<250){  //Bomb will comes slowly on screen after 100 times
				bombCount++;
			}else {
				bombCount=0;
				makeBomb();
			}
			bombRectangle.clear();
			for(int i=0;i<bombXs.size();i++){  //bombs will appear
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i,bombXs.get(i)-10);
				bombRectangle.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));

			}

			//For Coin
			if(coinCount<100){  //Coin will comes slowly on screen after 100 times
				coinCount++;
			}else {
				coinCount=0;
				makeCoin();
			}
			coinRectangle.clear();
			for(int i=0;i<coinXs.size();i++){  //coins will appear
				batch.draw(coin,coinXs.get(i),coinYs.get(i));
				coinXs.set(i,coinXs.get(i)-4);
				coinRectangle.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}
			if(Gdx.input.justTouched()){    //it enables the user to touch the screen and the man will jump
				velocity=-10;
			}

			if(pause<8)   // slow the running speed of man
			{
				pause++;
			}
			else {
				pause = 0;
				if (manState < 3) {   // change the images
					manState++;
				} else {
					manState = 0;
				}
			}
			velocity+=gravity;   // man will fall into bottom of the screen
			manY-=velocity;

			if(manY<=0)  // man will cross the bottom screen and stop  at the bottom screen
			{
				manY=0;
			}

		}else if(gameState==0){
			//Waiting to Start
			if(Gdx.input.justTouched()){
				gameState=1;
			}
		}else if(gameState==2){
			//game over
			if(Gdx.input.justTouched()){
				gameState=1;
				manY=Gdx.graphics.getHeight()/2;
				score=0;
				velocity=0;
				coinXs.clear();
				coinYs.clear();
				coinRectangle.clear();
				coinCount=0;
				bombXs.clear();
				bombYs.clear();
				bombRectangle.clear();
				bombCount=0;
			}
		}


		if (gameState==2){ //if game is over then this will show dizzy images
				batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}else {
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}
		manRectangle= new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2,manY,man[manState].getWidth(),man[manState].getHeight());

		for(int i=0;i<coinRectangle.size();i++){
			if(Intersector.overlaps(manRectangle,coinRectangle.get(i))){ //True when man and coin collide
				score++;
				coinRectangle.remove(i);  //remove coin when man collide with coin
				coinXs.clear();
				coinYs.clear();
				break;
			}
		}
		for(int i=0;i<bombRectangle.size();i++){
			if(Intersector.overlaps(manRectangle,bombRectangle.get(i))){
				Gdx.app.log("bomb","collison");
				gameState=2;
			}
		}
		font.draw(batch,String.valueOf(score),100,200);

		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
