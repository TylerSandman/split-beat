package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen extends AbstractGameScreen {
	
	protected boolean mPaused;
	protected World mWorld;
	private int mSongIndex;
	
	GameScreen(Game game, int songIndex){
		super(game);
		mPaused = false;
		mSongIndex = songIndex;
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
		mWorld = new World(game, mSongIndex);
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
