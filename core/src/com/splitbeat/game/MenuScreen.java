package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.SnapshotArray;

public class MenuScreen extends AbstractGameScreen {
	
	private Stage mStage;
	private Stack mStack;
	private Skin mSkin;
	
	private Table mBackgroundLayer;
	private Table mButtonLayer;
	
	private TextButton mPlayButton;
	private TextButton mSyncButton;
	private TextButton mOptionsButton;
	private TextButton.TextButtonStyle mHoverButtonStyle;
	private TextButton.TextButtonStyle mNormalButtonStyle;
	
	private int mButtonIndex;
	
	MenuScreen(Game game){
		super(game);
	}
	
	private void init(){
		mSkin = new Skin(
		Gdx.files.internal(Constants.GUI_SKIN),
		new TextureAtlas(Constants.TEXTURE_ATLAS_GUI));
		mHoverButtonStyle = new TextButton("", mSkin, "hover").getStyle();
		mNormalButtonStyle = new TextButton("", mSkin, "default").getStyle();
		mButtonIndex = -1;
		
		//TODO bg layer
		mBackgroundLayer = new Table();
		
		mButtonLayer = buildButtonLayer();
		
		mStage.clear();
		mStack = new Stack();
		mStage.addActor(mStack);
		mStack.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mStack.add(mBackgroundLayer);
		mStack.add(mButtonLayer);
		mStage.setKeyboardFocus(mPlayButton);
		
		mButtonLayer.addListener(new InputListener(){
			
			@Override
			public boolean keyDown(InputEvent event, int keycode){
				switch(keycode){
				case(Keys.DOWN):
					mButtonIndex = (mButtonIndex + 1) % mButtonLayer.getChildren().size;
					selectButton(mButtonIndex);
					break;
				case(Keys.UP):
					mButtonIndex = (mButtonIndex + mButtonLayer.getChildren().size - 1) % mButtonLayer.getChildren().size;
					selectButton(mButtonIndex);
					break;
				case(Keys.ENTER):
					if (mButtonIndex < 0) break;
					InputEvent touchDownEvent = new InputEvent();
					touchDownEvent.setType(Type.touchDown);
					mButtonLayer.getChildren().get(mButtonIndex).fire(touchDownEvent);
				}
				return true;
			}
		});
	}
	
	private Table buildButtonLayer(){
		
		Table layer = new Table();
		layer.center();
		
		mPlayButton = new TextButton("Play", mSkin, "default");		
		mPlayButton.addListener(new ClickListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				onPlayClicked();
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button){
				mPlayButton.setStyle(mNormalButtonStyle);
			}
			
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
				deselectAllButtons();
				mPlayButton.setStyle(mHoverButtonStyle);
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
				mPlayButton.setStyle(mNormalButtonStyle);
			}
			
		});	
		
		layer.add(mPlayButton).padBottom(Constants.BUTTON_PADDING);
		layer.row();
		
		mSyncButton = new TextButton("Sync", mSkin, "default");
		mSyncButton.addListener(new ClickListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				onSyncClicked();
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button){
				mSyncButton.setStyle(mNormalButtonStyle);
			}
			
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
				deselectAllButtons();
				mSyncButton.setStyle(mHoverButtonStyle);
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
				mSyncButton.setStyle(mNormalButtonStyle);
			}
		});
		layer.add(mSyncButton).padBottom(Constants.BUTTON_PADDING);
		layer.row();
		
		mOptionsButton = new TextButton("Options", mSkin, "default");
		mOptionsButton.addListener(new ClickListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				onOptionsClicked();
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button){
				mOptionsButton.setStyle(mNormalButtonStyle);
			}
			
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
				deselectAllButtons();
				mOptionsButton.setStyle(mHoverButtonStyle);
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
				mOptionsButton.setStyle(mNormalButtonStyle);
			}
		});
		layer.add(mOptionsButton);
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
	
	private void selectButton(int index){
		
		InputEvent enterEvent = new InputEvent();
		enterEvent.setType(Type.enter);
		mButtonLayer.getChildren().get(index).fire(enterEvent);
	}
	
	private void deselectAllButtons(){
		
		InputEvent exitEvent = new InputEvent();
		exitEvent.setType(Type.exit);
		InputEvent upEvent = new InputEvent();
		upEvent.setType(Type.touchUp);
		for (int i = 0; i < mButtonLayer.getChildren().size; ++i){
			mButtonLayer.getChildren().get(i).fire(exitEvent);
			mButtonLayer.getChildren().get(i).fire(upEvent);
		}
	}
	
	private void onPlayClicked(){
		game.setScreen(new GameScreen(game));
	}
	
	private void onSyncClicked(){
		return;
	}
	
	private void onOptionsClicked(){
		return;
	}
}
