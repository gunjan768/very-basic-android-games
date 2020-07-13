package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter
{
	SpriteBatch batch;

	Texture background, gameOver, topTube, bottomTube;
	Texture[] birds;

	//ShapeRenderer shapeRenderer;

	int flapState = 0, score = 0, scoringTube = 0, gameState = 0, numberOfTubes = 4;
	float birdY = 0f, velocity = 0f, gravity = 0.2f, gap = 400, maxTubeOffset, tubeVelocity = 10f, distanceBetweenTubes;

	float[] tubeX = new float[numberOfTubes], tubeOffset = new float[numberOfTubes];

	Circle birdCircle;
	Rectangle[] topTubeRectangles = new Rectangle[numberOfTubes], bottomTubeRectangles = new Rectangle[numberOfTubes];

	BitmapFont font;
	Random randomGenerator;

	private void initialFormation()
	{
		background = new Texture("bg.png");
		gameOver = new Texture("gameover.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		//shapeRenderer = new ShapeRenderer();

		birdCircle = new Circle();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;
	}

	@Override
	public void create ()
	{
		batch = new SpriteBatch();

		initialFormation();
		startGame();
	}

	public void startGame()
	{
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

		for(int i = 0; i<numberOfTubes; i++)
		{
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render ()
	{
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1)
		{
			if(tubeX[scoringTube] + topTube.getWidth() <= Gdx.graphics.getWidth() / 2)
			{
				score++;

				// Gdx.app.log("Score", String.valueOf(score));

				if(scoringTube < numberOfTubes - 1)
				{
					scoringTube++;
				}
				else
				{
					scoringTube = 0;
				}
			}

			if(Gdx.input.justTouched())
			{
				velocity = -8;
			}

			for(int i = 0; i<numberOfTubes; i++)
			{
				if(tubeX[i] < - topTube.getWidth())
				{
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				}
				else
				{
					tubeX[i] = tubeX[i] - tubeVelocity;
				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());

				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],
						bottomTube.getWidth(), bottomTube.getHeight()
				);
			}

			velocity += gravity;
			birdY -= velocity;

			// Gdx.app.log("Scoreeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", String.valueOf(velocity));

			if(birdY <= 0)
			birdY = 0;

			if(birdY >= Gdx.graphics.getHeight())
			birdY = Gdx.graphics.getHeight();
		}
		else if(gameState == 0)
		{
			if(Gdx.input.justTouched())
			{
				gameState = 1;
			}
		}
		else
		{
			batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);

			if(Gdx.input.justTouched())
			{
				gameState = 1;
				score = 0;
				scoringTube = 0;
				velocity = 0;

				startGame();
			}
		}

		flapState ^= 1;

		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
		font.draw(batch, String.valueOf(score), 100, 200);
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for(int i = 0; i<numberOfTubes; i++)
		{
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i]))
			{
				gameState = 2;
			}
		}

		batch.end();

		//shapeRenderer.end();
	}
}
