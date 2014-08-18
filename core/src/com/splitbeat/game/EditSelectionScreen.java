package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class EditSelectionScreen extends AbstractGameScreen {
	
	private Stage mStage;
	private Stack mStack;
	private Skin mSkin;
	
	private Table mBackgroundLayer;
	private Table mButtonLayer;
	
	private TextButton mNewButton;
	private TextButton mImportButton;
	private TextButton mEditButton;
	private TextButton.TextButtonStyle mHoverButtonStyle;
	private TextButton.TextButtonStyle mNormalButtonStyle;
	
	private int mButtonIndex;

	EditSelectionScreen(Game game) {
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
		mStage.setKeyboardFocus(mNewButton);
		
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
		
		mNewButton = new TextButton("New", mSkin, "default");		
		mNewButton.addListener(new ClickListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				onNewClicked();
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button){
				mNewButton.setStyle(mNormalButtonStyle);
			}
			
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
				deselectAllButtons();
				mNewButton.setStyle(mHoverButtonStyle);
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
				mNewButton.setStyle(mNormalButtonStyle);
			}
			
		});	
		
		layer.add(mNewButton).padBottom(Constants.CELL_PADDING);
		layer.row();
		
		mImportButton = new TextButton("Import", mSkin, "default");
		mImportButton.addListener(new ClickListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				onImportClicked();
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button){
				mImportButton.setStyle(mNormalButtonStyle);
			}
			
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
				deselectAllButtons();
				mImportButton.setStyle(mHoverButtonStyle);
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
				mImportButton.setStyle(mNormalButtonStyle);
			}
		});
		layer.add(mImportButton).padBottom(Constants.CELL_PADDING);
		layer.row();
		
		mEditButton = new TextButton("Edit Existing", mSkin, "default");
		mEditButton.addListener(new ClickListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				onEditClicked();
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button){
				mEditButton.setStyle(mNormalButtonStyle);
			}
			
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
				deselectAllButtons();
				mEditButton.setStyle(mHoverButtonStyle);
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
				mEditButton.setStyle(mNormalButtonStyle);
			}
		});
		layer.add(mEditButton).padBottom(Constants.CELL_PADDING);
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
	
	private void onNewClicked(){}
	
	private void onImportClicked(){}
	
	private void onEditClicked(){}

}
