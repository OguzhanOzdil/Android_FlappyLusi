package com.ozzyozdil.flappylusi;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

import java.util.Random;

public class FlappyLusi extends ApplicationAdapter {

	// ------------------  Game  -----------------------
	SpriteBatch batch;
	Texture background;
	int backgroundOffset = 0;
	Texture gameOver;

	String gameState = "Not start";  // Oyun durumu

	// ShapeRenderer shapeRenderer;  // Circle ı gözle görebilmek için

	Preferences prefs; // max score u hafızaya atmak için preferneces kullandık

	int score = 0;
	int scoredEnemy = 0;
	int maxScore;

	BitmapFont scoreFont;


	private Sound jumpSound;
	private Sound dieSound;
	private Music music;

	// ------------------  Bird  -----------------------
	Texture bird;

	float bird_X = 0;
	float bird_Y = 0;

	float velocity = 0;     // Y hızı
	float gravity = 0.4f;   // yer çekimi

	Circle birdCircle;  // çarpışma alanı

	// ------------------  Bee  -----------------------
	Texture bee;
	Random random;

	int numberOfEnemies = 4;  // Düşman Sayısı
	float [] enemy_X = new float[numberOfEnemies];  // Düşman X i
	float [] enemyOffSet_1 = new float[numberOfEnemies];
	float [] enemyOffSet_2 = new float[numberOfEnemies];
	float [] enemyOffSet_3 = new float[numberOfEnemies];

	float distance = 0;   // Düşman x uzaklığı
	float enemyVelocity = 3.5f;  // Düşman X Hızı

	Circle[] enemyCircles_1;  // Düşman çarpışma alanı
	Circle[] enemyCircles_2;
	Circle[] enemyCircles_3;

	// Oyun başladığında...
	@Override
	public void create () {

		batch = new SpriteBatch();
		background = new Texture("background.png");
		gameOver = new Texture("gameover.png");
		bird = new Texture("lusi.png");
		bee = new Texture("spike.png");

		bird_X = Gdx.graphics.getWidth() / 5;
		bird_Y = Gdx.graphics.getHeight() / 3;

		birdCircle = new Circle();
		enemyCircles_1 = new Circle[numberOfEnemies];
		enemyCircles_2 = new Circle[numberOfEnemies];
		enemyCircles_3 = new Circle[numberOfEnemies];

		// shapeRenderer = new ShapeRenderer();

		distance = Gdx.graphics.getWidth() / 2;

		random = new Random();

		scoreFont = new BitmapFont();
		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().setScale(5);

		for (int i = 0; i < numberOfEnemies; i++){  // düşman set sayısı ve random Y ler

			enemyOffSet_1[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
			enemyOffSet_2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
			enemyOffSet_3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);

			enemy_X[i] = Gdx.graphics.getHeight() - bee.getWidth() / 2 + i * distance;

			enemyCircles_1[i] = new Circle();
			enemyCircles_2[i] = new Circle();
			enemyCircles_3[i] = new Circle();

			// Burada kaydedilen max score çağırıyoruz
			prefs = Gdx.app.getPreferences("Flappy Lusi");
			maxScore = prefs.getInteger("highscore");
		}

		// Music
		music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

		music.setVolume(0.1f);
		music.setLooping(true);
		music.play();

		// Jump Sound
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("water.mp3"));
		// Die Sound
		dieSound = Gdx.audio.newSound(Gdx.files.internal("die.mp3"));

	}


	// Oyun devam ederken...
	@Override
	public void render () {

		batch.begin();  // batch başlat

		backgroundOffset += 2;

		if (backgroundOffset % Gdx.graphics.getWidth() == 0){
			backgroundOffset = 0;
		}

		batch.draw(background, -backgroundOffset, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.draw(background, -backgroundOffset + Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState.equals("Start")){

			if (enemy_X[scoredEnemy] < bird_X){
				score++;

				if (scoredEnemy < (numberOfEnemies - 1)){
					scoredEnemy++;
				}
				else{
					scoredEnemy = 0;
				}
			}

			if (Gdx.input.justTouched()){

				// Ses ekledim normalde MediaPlayer ile yapıyorduk ama libgdx de böyle
				long id = jumpSound.play(1.0f);
				jumpSound.setPitch(id, 1);
				jumpSound.setLooping(id, false);

				// Dokununca kuş yükselsin
				velocity = -12;

			}

			for (int i = 0; i < numberOfEnemies; i++){

				if (enemy_X[i] + 800 < 0){

					enemy_X[i] = enemy_X[i] + numberOfEnemies * distance;

					enemyOffSet_1[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);  // Rastgele düşman Y si
					enemyOffSet_2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
					enemyOffSet_3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);

				}
				else{
					enemy_X[i] -= enemyVelocity;
				}

				enemy_X[i] -= enemyVelocity;

				batch.draw(bee, enemy_X[i] + 800, Gdx.graphics.getHeight() / 2 + enemyOffSet_1[i], Gdx.graphics.getWidth() / 23, Gdx.graphics.getHeight() / 11);
				batch.draw(bee, enemy_X[i] + 800, Gdx.graphics.getHeight() / 2 + enemyOffSet_2[i], Gdx.graphics.getWidth() / 23, Gdx.graphics.getHeight() / 11);
				batch.draw(bee, enemy_X[i] + 800, Gdx.graphics.getHeight() / 2 + enemyOffSet_3[i], Gdx.graphics.getWidth() / 23, Gdx.graphics.getHeight() / 11);

				enemyCircles_1[i] = new Circle((enemy_X[i] + 800) + (Gdx.graphics.getWidth() / 45), (Gdx.graphics.getHeight() / 2 + enemyOffSet_1[i]) + (Gdx.graphics.getHeight() / 20), Gdx.graphics.getWidth() / 45);
				enemyCircles_2[i] = new Circle((enemy_X[i] + 800) + (Gdx.graphics.getWidth() / 45), (Gdx.graphics.getHeight() / 2 + enemyOffSet_2[i]) + (Gdx.graphics.getHeight() / 20), Gdx.graphics.getWidth() / 45);
				enemyCircles_3[i] = new Circle((enemy_X[i] + 800) + (Gdx.graphics.getWidth() / 45), (Gdx.graphics.getHeight() / 2 + enemyOffSet_3[i]) + (Gdx.graphics.getHeight() / 20), Gdx.graphics.getWidth() / 45);


			}


			if (bird_Y > 0){

				velocity += gravity;
				bird_Y -= velocity;  // Kuş aşağıya düşüyor

				if (bird_Y >= Gdx.graphics.getHeight()){
					gameState = "Game Over";
				}

			}
			else{
				gameState = "Game Over";
			}

		}
		else if (gameState.equals("Not start")){
			if (Gdx.input.justTouched()){  // Dokununca oyun başlasın

				gameState = "Start";
			}
		}
		else if (gameState.equals("Game Over")) {

			long id = dieSound.play(1.0f);
			dieSound.setPitch(id, 1);
			dieSound.setLooping(id, false);
			dieSound.dispose();

			// Game Over resmi
			batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - 200, Gdx.graphics.getHeight() / 2 - 50);

			// Burada max score u cihaza kaydediyoruz
			if (score >= maxScore){
				maxScore = score;
				prefs.putInteger("highscore", maxScore);
				prefs.flush();
			}

			scoreFont.draw(batch, String.valueOf("High Score: " + maxScore), Gdx.graphics.getWidth() / 2 - 220, Gdx.graphics.getHeight() / 2 - 80);


			if (Gdx.input.justTouched()) {

				dieSound = Gdx.audio.newSound(Gdx.files.internal("die.mp3"));

				gameState = "Start";

				bird_Y = Gdx.graphics.getHeight() / 3;

				for (int i = 0; i < numberOfEnemies; i++){  // düşman set sayısı ve random Y ler

					enemyOffSet_1[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
					enemyOffSet_2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
					enemyOffSet_3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);

					enemy_X[i] = Gdx.graphics.getHeight() - bee.getWidth() / 2 + i * distance;

					enemyCircles_1[i] = new Circle();
					enemyCircles_2[i] = new Circle();
					enemyCircles_3[i] = new Circle();

				}

				velocity = 0;
				scoredEnemy = 0;
				score = 0;

			}
		}

		batch.draw(bird, bird_X, bird_Y, Gdx.graphics.getWidth() / 23, Gdx.graphics.getHeight() / 11);

		scoreFont.draw(batch, String.valueOf(score), 100, 200);

		batch.end();	// batch sonlandır

		birdCircle.set(bird_X + Gdx.graphics.getWidth() / 45, bird_Y + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 45); // 40 bird in genişliğinin yarıçapı

		/*
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
		 */

		for (int i = 0; i<numberOfEnemies; i++){

			/*
			shapeRenderer.setColor(Color.RED);

			shapeRenderer.circle((enemy_X[i]) + (Gdx.graphics.getWidth() / 40), (Gdx.graphics.getHeight() / 2 + enemyOffSet_1[i]) + (Gdx.graphics.getHeight() / 20), Gdx.graphics.getWidth() / 40);
			shapeRenderer.circle((enemy_X[i]) + (Gdx.graphics.getWidth() / 40), (Gdx.graphics.getHeight() / 2 + enemyOffSet_2[i]) + (Gdx.graphics.getHeight() / 20), Gdx.graphics.getWidth() / 40);
			shapeRenderer.circle((enemy_X[i]) + (Gdx.graphics.getWidth() / 40), (Gdx.graphics.getHeight() / 2 + enemyOffSet_3[i]) + (Gdx.graphics.getHeight() / 20), Gdx.graphics.getWidth() / 40);
			 */

			// Çarpışmalar
			if (Intersector.overlaps(birdCircle, enemyCircles_1[i]) || Intersector.overlaps(birdCircle, enemyCircles_2[i]) || Intersector.overlaps(birdCircle, enemyCircles_3[i])){
				gameState = "Game Over";
			}

		}

		// shapeRenderer.end();
	}


	// Oyun sonlandığında...
	@Override
	public void dispose () {
		jumpSound.dispose();
		dieSound.dispose();
		music.dispose();
	}
}
