package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

public class MenuScreen extends AbstractGameScreen {
	
	private Stage mStage;
	private Skin mSkin;
	
	private TextButton mPlayButton;
	private TextButton mSyncButton;
	
	MenuScreen(Game game){
		super(game);
	}
	
	private void init(){
		mSkin = new Skin(
		Gdx.files.internal(Constants.GUI_SKIN),
		new TextureAtlas(Constants.TEXTURE_ATLAS_GUI));
		
		//TODO bg layer
		Table background = new Table();
		
		Table buttonLayer = buildButtonLayer();
		
		mStage.clear();
		Stack stack = new Stack();
		mStage.addActor(stack);
		stack.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stack.add(background);
		stack.add(buttonLayer);
		
		
	}
	
	private Table buildButtonLayer(){
		
		Table layer = new Table();
		layer.center();
		
		mPlayButton = new TextButton("Play", mSkin, "default");		
		mPlayButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
			onPlayClicked();
			}
		});		
		layer.add(mPlayButton).pad(Constants.BUTTON_PADDING);
		layer.row();
		
		mSyncButton = new TextButton("Sync", mSkin, "default");
		mSyncButton.addListener(new ChangeListener(){
			@Override
			public void changed (ChangeEvent event, Actor actor){
				onSyncClicked();
			}
		});
		layer.add(mSyncButton);
		return layer;
	}

	@Override
	public void render(float delta) {
		
		mStage.act(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mStage.draw();
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {
		mStage = new Stage();
		init();
		Gdx.input.setInputProcessor(mStage);
	}

	@Override
	public void hide() {
		mStage.dispose();
		mSkin.dispose();
	}

	@Override
	public void pause() {}
	
	private void onPlayClicked(){
		game.setScreen(new GameScreen(game));
	}
	
	private void onSyncClicked(){
		return;
	}
}
