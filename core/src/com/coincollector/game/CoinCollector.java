package com.coincollector.game;

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

public class CoinCollector extends ApplicationAdapter {
	SpriteBatch batch;
	//Texture img;

	Texture background, coin, bomb, dizzyCharacter;
	Texture[] character;
	int characterState = 0, pause = 0, yPos, coinCount = 0, bombCount = 0, score = 0, gameState = 0;
	float gravity = 0.6f, velocity = 0;

	ArrayList<Integer> coinXPos = new ArrayList<Integer>();
	ArrayList<Integer> coinYPos = new ArrayList<>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	ArrayList<Integer> bombXPos = new ArrayList<Integer>();
	ArrayList<Integer> bombYPos = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();

	Rectangle characterRectangle;

	Random objRandom;

	BitmapFont scoreBmpFont, gameOverBmpFont, stateBmpFont;

	@Override
	public void create ()
	{	//Runs the 1st time application is launched.
		batch = new SpriteBatch();
		//img = new Texture("badlogic.jpg");

		background = new Texture("bg.png");
		character = new Texture[4];
		character[0] = new Texture("frame-1.png");
		character[1] = new Texture("frame-2.png");
		character[2] = new Texture("frame-3.png");
		character[3] = new Texture("frame-4.png");

		yPos = Gdx.graphics.getHeight()/2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		objRandom = new Random();

		dizzyCharacter = new Texture("dizzy-1.png");

		scoreBmpFont = new BitmapFont();
		scoreBmpFont.setColor(Color.WHITE);
		scoreBmpFont.getData().setScale(4);

		gameOverBmpFont = new BitmapFont();
		gameOverBmpFont.setColor(Color.RED);
		gameOverBmpFont.getData().setScale(6);

		stateBmpFont = new BitmapFont();
		stateBmpFont.setColor(Color.BLUE);
		stateBmpFont.getData().setScale(3);

	}

	public void makeCoin()
	{
		//Using a random object, we'll get different heights for coins to be displayed. nextFloat will return values b/w 0 & 1.
		float height = objRandom.nextFloat() * Gdx.graphics.getHeight();
		coinYPos.add((int)height);
		//We're simply beginning the coin to display outside the right of the screen.
		coinXPos.add(Gdx.graphics.getWidth());
	}

	public void makeBomb()
	{
		float height = objRandom.nextFloat() * Gdx.graphics.getHeight();
		bombYPos.add((int)height);
		bombXPos.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render ()
	{	//Keeps running over & over again until the app is stopped.
		/*Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();*/

		//Any thing to be displayed over the screen has to be written between batch.begin() & batch.end()
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState==0)	//TOUCH TO START
		{
			stateBmpFont.draw(batch, "Touch To Start!", 150, 300);
			if(Gdx.input.justTouched())
				gameState = 1;
		}
		else if(gameState==1)	//GAME ON
		{
			//Displaying coin at a gap of 100 render cycles, in the else block & then resetting the cycle back to 0.
			if(coinCount < 100)
				coinCount++;
			else
			{
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();
			//To keep displaying coins on the screen till the app runs. Coins will keep shifting their position towards left of the screen.
			for(int i=0; i<coinXPos.size(); i++)
			{
				batch.draw(coin, coinXPos.get(i), coinYPos.get(i));
				coinXPos.set(i, coinXPos.get(i)-5);
				coinRectangles.add(new Rectangle(coinXPos.get(i), coinYPos.get(i), coin.getWidth(), coin.getHeight()));
			}

			if(bombCount < 350)
				bombCount++;
			else
			{
				bombCount = 0;
				makeBomb();
			}

			bombRectangles.clear();
			for(int i=0; i<bombXPos.size(); i++)
			{
				batch.draw(bomb, bombXPos.get(i), bombYPos.get(i));
				bombXPos.set(i, bombXPos.get(i)-7);
				bombRectangles.add(new Rectangle(bombXPos.get(i), bombYPos.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			//As the user touches the screen, the character will jump, by adding some value to its height.
			if(Gdx.input.justTouched())
				velocity = -20f;

			//Reducing the character's running appearance by limiting the number of render cycles needed to display the character, by introducing a pause variable.
			if(pause < 8)
				pause++;
			else
			{
				pause = 0;
				if(characterState < 3)
					characterState++;
				else
					characterState = 0;
			}

			//Rate of falling down to the screen is determined by velocity & gravity.
			//Also, if the character tends to falls down from the screen, that'll not happen.
			velocity += gravity;
			yPos -= velocity;
			if(yPos <= 0)
				yPos = 0;

		}
		else if(gameState==2)	//GAME OVER
		{
			gameOverBmpFont.draw(batch, "GAME OVER!", 70, Gdx.graphics.getHeight() - 200);
			stateBmpFont.draw(batch, "Touch To ReStart!", 150, 300);

			if(Gdx.input.justTouched())
			{
				gameState = 1;

				yPos = Gdx.graphics.getHeight()/2;
				score = 0;
				characterState = 0;
				pause = 0;
				gameState = 0;
				velocity = 0;
				coinXPos.clear();
				coinYPos.clear();
				coinRectangles.clear();
				coinCount = 0;
				bombXPos.clear();
				bombYPos.clear();
				bombRectangles.clear();
				bombCount = 0;
			}
		}

		for(int i=0; i<coinRectangles.size(); i++)
		{
			if(Intersector.overlaps(characterRectangle, coinRectangles.get(i)))
			{
				Gdx.app.log("COIN", "Collected!");
				score++;

				coinRectangles.remove(i);
				coinXPos.remove(i);
				coinYPos.remove(i);
				break;
			}
		}

		for(int i=0; i<bombRectangles.size(); i++)
		{
			if(Intersector.overlaps(characterRectangle, bombRectangles.get(i)))
			{
				Gdx.app.log("BOMB", "Blast!");

				bombRectangles.remove(i);
				bombXPos.remove(i);
				bombYPos.remove(i);

				gameState = 2;
			}
		}

		//batch.draw(character[characterState], Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/2);
		if (gameState == 2)
			batch.draw(dizzyCharacter, Gdx.graphics.getWidth()/4, yPos, Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/4);
		else
		{
			batch.draw(character[characterState], Gdx.graphics.getWidth()/4, yPos, Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/4);
			characterRectangle = new Rectangle(Gdx.graphics.getWidth()/4, yPos, character[characterState].getWidth()/4, character[characterState].getHeight()/4);
		}

		scoreBmpFont.draw(batch, String.valueOf(score), 20,70);

		batch.end();
	}
	
	@Override
	public void dispose ()
	{	//Runs when the app is stopped.
		batch.dispose();
		//img.dispose();
	}
}
