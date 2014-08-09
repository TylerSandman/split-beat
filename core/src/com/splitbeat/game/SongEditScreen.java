package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
public class SongEditScreen extends AbstractGameScreen {
	
	private TmxMapBuilder mBuilder;
	
	SongEditScreen(Game game){
		super(game);
	}
	
	private void init(){
		mBuilder = new TmxMapBuilder();
		mBuilder.create("test");
	}
	
	@Override
	public void show() {
		init();
	}

	@Override
	public void hide() {}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

}