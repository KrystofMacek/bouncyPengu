package com.akakumo.bouncypengu;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import java.util.Random;



public class bouncyPengu extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private Texture leftLog;
    private Texture rightLog;
    private Texture btnBackground;
    private Player mPlayer;

    private Texture playerLeft;
    private Texture playerRight;
    private Texture playerCenter;
    private Texture playerDead;

    final private Random rng = new Random();
    final private int numOfBlocks = 4;

    private int scoreBlock = 0;
    private float blockDistance;
    private float gdxWidth;
    private float gdxHeight;

    private int gameState;
    private float blockOffset[] = new float[4];
    private float blockY[] = new float[4];

    private int score;

    private BitmapFont mBitmapFont;
//    private ShapeRenderer shapeRenderer;
    private Rectangle[] leftBlockRectangles;
    private Rectangle[] rightBlockRectangles;

    private Label.LabelStyle labelStyle;

    private Preferences save;

    private Sound crash;
    private Sound pointScored;


    @Override
    public void create() {


        save = Gdx.app.getPreferences("saved");
        if (!save.contains("bestScore")) {
            save.putInteger("bestScore", 0);
            save.flush();
        }

        batch = new SpriteBatch();
//        shapeRenderer = new ShapeRenderer();

        background = new Texture("iceBackground2.png");
        leftLog = new Texture("ice_left.png");
        rightLog = new Texture("ice_right.png");
        mPlayer = new Player();

        //init sounds
        crash = Gdx.audio.newSound(Gdx.files.internal("crash.wav"));
        pointScored = Gdx.audio.newSound(Gdx.files.internal("pointScored2.wav"));



        gdxWidth = Gdx.graphics.getWidth();
        gdxHeight = Gdx.graphics.getHeight();
        blockDistance = 680;

        gameState = 0;
        score = 0;

        mBitmapFont = new BitmapFont(Gdx.files.internal("font1.fnt"));
        mBitmapFont.setColor(34, 85, 203, .8f);

        leftBlockRectangles = new Rectangle[4];
        rightBlockRectangles = new Rectangle[4];


        labelStyle = new Label.LabelStyle();
        labelStyle.font = mBitmapFont;


        for (int i = 0; i < numOfBlocks; i++) {

            blockY[i] = (gdxHeight / 2) + (blockDistance * (i + 1));
            blockOffset[i] = (rng.nextFloat() - 0.5f) * gdxWidth * 0.8f;
            leftBlockRectangles[i] = new Rectangle();
            rightBlockRectangles[i] = new Rectangle();
        }

        btnBackground = new Texture("btn_2.png");

        playerLeft = new Texture("penguin_left.png");
        playerRight = new Texture("penguin_right.png");
        playerCenter = new Texture("penguin_norm.png");
        playerDead = new Texture("penguin_dead.png");

    }

    private void resetGame() {
        mPlayer.reset();
        score = 0;
        gameState = 0;
        scoreBlock = 0;
        mPlayer.setTexture(playerCenter);
        mPlayer.hitSoundPlayed = 0;

        for (int i = 0; i < numOfBlocks; i++) {
            blockY[i] = (gdxHeight / 2) + (blockDistance * (i + 1));
            blockOffset[i] = (rng.nextFloat() - 0.5f) * gdxWidth * 0.8f;
            leftBlockRectangles[i] = new Rectangle();
            rightBlockRectangles[i] = new Rectangle();
        }
    }

    @Override
    public void render() {

        batch.begin();



        //Draw background
        batch.draw(background, 0, 0, gdxWidth, gdxHeight);

        if (Gdx.input.justTouched() && gameState == 0) {
            gameState = 1;
        }
        if (Gdx.input.justTouched() && gameState == -1) {
            float y = gdxHeight - Gdx.input.getY();
            if (y > gdxHeight - (gdxHeight / 3)) {
                resetGame();
            }
        }

        // Ready to play State
        if (gameState == 0) {

            if (!batch.isDrawing()) {
                batch.begin();
            }

            Label jumpLabel = new Label("Tap to Jump !", labelStyle);
            jumpLabel.setFontScale(.5f);
            jumpLabel.setSize(gdxWidth / 2, gdxHeight / 3);
            jumpLabel.setPosition((gdxWidth / 2) - (jumpLabel.getWidth() / 2), (gdxHeight / 2) - (jumpLabel.getHeight() / 2));
            jumpLabel.setAlignment(Align.center);
            jumpLabel.draw(batch, 1);

            mBitmapFont.draw(batch, Integer.toString(score), gdxWidth / 2 - 20, gdxHeight - 200);

        }
        //Game running State
        else if (gameState == 1) {

            for (int i = 0; i < numOfBlocks; i++) {

                float blockWidth = gdxWidth;

                batch.draw(leftLog, -gdxWidth / 2 + blockOffset[i] - 120, blockY[i], blockWidth, leftLog.getHeight());
                batch.draw(rightLog, gdxWidth / 2 + blockOffset[i] + 120, blockY[i], blockWidth, rightLog.getHeight());

                leftBlockRectangles[i] = new Rectangle(-gdxWidth / 2.5f + blockOffset[i] - gdxWidth / 4 - 10, blockY[i], blockWidth, leftLog.getHeight());
                rightBlockRectangles[i] = new Rectangle(gdxWidth / 2.5f + blockOffset[i] + gdxWidth / 4 + 10, blockY[i], blockWidth, rightLog.getHeight());

                float blockVelocity = 4;
                blockY[i] -= blockVelocity;

                if (blockY[scoreBlock] + leftLog.getHeight() < mPlayer.position[1]) {
                    score++;
                    pointScored.play();
                    if (scoreBlock < numOfBlocks - 1) {
                        scoreBlock++;
                    } else {
                        scoreBlock = 0;
                    }
                }

                mBitmapFont.draw(batch, Integer.toString(score), gdxWidth / 2 - 20, gdxHeight - 200);

                if (blockY[i] < -leftLog.getHeight() * 3) {
                    blockY[i] += numOfBlocks * blockDistance;

                    blockOffset[i] = (rng.nextFloat() - 0.5f) * gdxWidth * 0.8f;
                }

            }

            //Move player
            if (mPlayer.position[1] > -2 * mPlayer.height || mPlayer.yVelocity < 0) {
                float gravity = 2;

                mPlayer.yVelocity += gravity;
                mPlayer.position[1] -= mPlayer.yVelocity;
                mPlayer.position[0] -= mPlayer.xVelocity;
            }

            // Jump
            if (Gdx.input.justTouched()) {
                mPlayer.jump();
            }
        }
        //Game over State
        else if (gameState == -1) {
            // Setting Scores
            int currentBest = save.getInteger("bestScore");
            if (score > currentBest) {
                save.putInteger("bestScore", score);
                save.flush();
            }
            String stringScore = Integer.toString(score);
            String StringBestScore = Integer.toString(save.getInteger("bestScore"));

            // Game over Labels
            Label scoreLabel = new Label("Best: " + StringBestScore + " \n"
                    + "Score: " + stringScore, labelStyle);
            scoreLabel.setSize(gdxWidth / 2, gdxHeight / 3);
            scoreLabel.setPosition((gdxWidth / 2) - (scoreLabel.getWidth() / 2), (gdxHeight / 2) - (scoreLabel.getHeight() / 2));
            scoreLabel.setAlignment(Align.center);
            scoreLabel.draw(batch, 1);

            batch.draw(btnBackground, (gdxWidth / 2) - (btnBackground.getWidth() / 2), gdxHeight - btnBackground.getHeight() * 1.25f, btnBackground.getWidth() ,btnBackground.getHeight());

            // Dead animation
            mPlayer.hitSound();
            mPlayer.setTexture(playerDead);


            if (mPlayer.position[1] > mPlayer.height / 2 || mPlayer.yVelocity < 0) {
                mPlayer.yVelocity += 1;
                mPlayer.position[1] -= mPlayer.yVelocity;
            }

        }

        // Draw player
        mPlayer.chickCircle.set(mPlayer.position[0] + mPlayer.width / 2, mPlayer.position[1] + mPlayer.height / 2, mPlayer.width / 2f);
        batch.draw(mPlayer.texture, mPlayer.position[0], mPlayer.position[1]);

        // Checking for game over conditions
        if (mPlayer.position[0] < -mPlayer.width * 2 || mPlayer.position[0] > gdxWidth + mPlayer.width) {
            gameState = -1;
        }
        if (mPlayer.position[1] < -mPlayer.height * 2) {
            gameState = -1;
        }

        batch.end();


        for (int i = 0; i < numOfBlocks; i++) {

            if (Intersector.overlaps(mPlayer.chickCircle, leftBlockRectangles[i]) || Intersector.overlaps(mPlayer.chickCircle, rightBlockRectangles[i])) {
                gameState = -1;
            }
        }

    }


    @Override
    public void dispose() {
    }

    // Player Class
    private class Player {

        private Texture texture;
        int[] position;
        int yVelocity;
        int xVelocity;
        int jumpState = 0;
        int hitSoundPlayed;

        private int width;
        private int height;
        Circle chickCircle;

        Player() {
            texture = new Texture("penguin_norm.png");
            position = new int[2];
            yVelocity = 0;
            xVelocity = 0;
            chickCircle = new Circle();

            width = texture.getWidth();
            height = texture.getHeight();

            position[0] = Gdx.graphics.getWidth() / 2 - texture.getWidth() * 2;
            position[1] = 100;
            hitSoundPlayed = 0;
        }

        private void jump(){
//            flap.play();
            this.yVelocity = -21;
            if (this.jumpState == 0) {
                this.xVelocity = -10;
                this.jumpState = 1;
                this.setTexture(playerRight);

            } else if (this.jumpState == 1) {
                this.xVelocity = 10;
                this.jumpState = 0;
                this.setTexture(playerLeft);
            }
        }

        private void reset() {
            position[0] = Gdx.graphics.getWidth() / 2 - texture.getWidth() * 2;
            position[1] = 100;
            jumpState = 0;
        }

        private void hitSound() {
            if(hitSoundPlayed == 0) {
                crash.play();
                hitSoundPlayed = 1;
            }
        }

        private void setTexture(Texture texture) {
            this.texture = texture;
        }
    }

}
