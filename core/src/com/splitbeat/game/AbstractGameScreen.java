package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

public abstract class AbstractGameScreen implements Screen {

	protected Game game;
	
	AbstractGameScreen(Game game){
		this.game = game;
	}
	@Override
	public abstract void render(float delta);

	@Override
	public abstract void resize(int width, int height);

	@Override
	public abstract void show();

	@Override
	public abstract void hide();

	@Override
	public abstract void pause();
	
	@Override
	public void resume() {}

	@Override
	public void dispose() {}
}
