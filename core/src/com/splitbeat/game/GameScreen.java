package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen extends AbstractGameScreen {
	
	protected boolean mPaused;
	protected World mWorld;
	protected String mSongName;
	protected Difficulty mDifficulty;
	
	GameScreen(Game game, String songName, Difficulty difficulty){
		super(game);
		mPaused = false;
		mSongName = songName;
		mDifficulty = difficulty;
	}

	@Override
	public void render(float delta) {
		if (!mPaused)
			mWorld.update(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mWorld.render();
	}

	@Override
	public void resize(int width, int height) {
		mWorld.resize(width, height);
	}

	@Override
	public void show() {
		mWorld = new World(game, mSongName, mDifficulty);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide() {
		mWorld.dispose();
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void pause() {
		mPaused = true;
	}

}
