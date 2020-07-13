package com.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class MyCoinMan extends ApplicationAdapter
{
	SpriteBatch batch;
	Texture background, dizzy, coin, bomb;
	Texture[] man;

	int manState = 0, pause = 0, manY = 0, score = 0, gameState = 0, bombCount = 0, coinCount = 0;
	float gravity = 0.2f, velocity = 0f;

	Rectangle manRectangle;
	BitmapFont font;

	Random random;

	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<>();

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles =  new ArrayList<Rectangle>();

	private void initiateInitialisation()
	{
		background = new Texture("bg.png");
		man = new Texture[4];

		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		dizzy = new Texture("dizzy-1.png");

		random = new Random();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	@Override
	public void create ()
	{
		batch = new SpriteBatch();

		initiateInitialisation();
	}

	public void makeCoin()
	{
		float height = random.nextFloat() * (Gdx.graphics.getHeight()-20);

		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb()
	{
		float height = random.nextFloat() * (Gdx.graphics.getHeight()-20);

		// Height can be variable.
		bombYs.add((int)height);

		// Remember that bomb will always start from the right side.
		bombXs.add(Gdx.graphics.getWidth());
	}

	private void bombInitialization()
	{
		if(bombCount < 250)
		{
			bombCount++;
		}
		else
		{
			bombCount = 0;

			makeBomb();
		}

		bombRectangles.clear();

		for(int i=0; i<bombXs.size(); i++)
		{
			batch.draw(bomb, bombXs.get(i), bombYs.get(i));
			bombXs.set(i, bombXs.get(i) - 18);

			bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
		}
	}

	private void coinInitialization()
	{
		if(coinCount < 40)
		{
			coinCount++;
		}
		else
		{
			coinCount = 0;

			makeCoin();
		}

		coinRectangles.clear();

		for(int i=0; i<coinXs.size(); i++)
		{
			batch.draw(coin, coinXs.get(i), coinYs.get(i));
			coinXs.set(i, coinXs.get(i) - 10);

			coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
		}
	}

	private void controlCharacterMovement()
	{
		if(Gdx.input.justTouched())
		{
			velocity = -12;
		}

		if(pause < 8)
		{
			pause++;
		}
		else
		{
			pause = 0;

			if(manState < 3)
			{
				manState++;
			}
			else
			{
				manState = 0;
			}
		}

		velocity += gravity;
		manY -= velocity;

		if(manY <= 0)
		{
			manY = 0;
		}

		if(manY >= Gdx.graphics.getHeight()-20)
		{
			manY = Gdx.graphics.getHeight()-20;
		}
	}

	// render() function will run continuously.
	@Override
	public void render ()
	{
		batch.begin();

		// background will start from (0, 0) and it will take the whole width and height.
		batch.draw(background,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1)
		{
			bombInitialization();
			coinInitialization();

			controlCharacterMovement();
		}
		else if(gameState == 0)
		{
			// Waiting to start. Game will start when the player touched the screen for the first time.
			if(Gdx.input.justTouched())
			{
				gameState = 1;
			}
		}
		else
		{
			if(Gdx.input.justTouched())
			{
				gameState = 1;
				manY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount = 0;
			}
		}

		if(gameState == 2)
		{
			// batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);

			batch.draw(dizzy, 10, manY);
		}
		else
		{
			// batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);

			batch.draw(man[manState], 10, manY);
		}

//		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY,
//					man[manState].getWidth(), man[manState].getHeight()
//		);

		manRectangle = new Rectangle(10, manY, man[manState].getWidth(), man[manState].getHeight());

		for(int i=0; i < coinRectangles.size();i++)
		{
			if(Intersector.overlaps(manRectangle, coinRectangles.get(i)))
			{
				score++;

				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);

				break;
			}
		}

		for(int i=0; i < bombRectangles.size();i++)
		{
			if(Intersector.overlaps(manRectangle, bombRectangles.get(i)))
			{
				// Gdx.app.log("Bomb!", "Collision!");
				gameState = 2;
			}
		}

		font.draw(batch, String.valueOf(score),100,200);

		batch.end();
	}

	@Override
	public void dispose ()
	{
		batch.dispose();
	}
}
