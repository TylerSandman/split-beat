package com.splitbeat.game;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.splitbeat.game.Assets.AssetData.SongData;

public class SongSelectScreen extends AbstractGameScreen {
	
	private Stage mStage;
	private Skin mSkin;	
	private Table mLayoutTable;
	
	private ScrollPane mSongsPane;
	private Table mSongsTable;
	private Table mDifficultyTable;
	private Label mSelectLabel;
	private TextButton mPlayButton;
	
	private int mSelectedIndex;
	private int mNumSongs;
	
	private Sprite mGradientSprite;
	private Sprite mGradientHighlightSprite;
	private SpriteDrawable mGradientDrawable;
	private SpriteDrawable mGradientHighlightDrawable;
	

	SongSelectScreen(Game game) {
		super(game);
		mSelectedIndex = -1;
	}
	
	private void init(){
		
		mSkin = new Skin(
				Gdx.files.internal(Constants.GUI_SKIN),
				new TextureAtlas(Constants.TEXTURE_ATLAS_GUI));
		
		//Set up our song table textures
		Assets.instance.gui.repeatGradient.getTexture().setFilter(
				TextureFilter.Linear, TextureFilter.Linear);
		Assets.instance.gui.repeatGradientHighlight.getTexture().setFilter(
				TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion gradientRegion = new TextureRegion(Assets.instance.gui.repeatGradient);
		mGradientSprite = new Sprite(gradientRegion);
		mGradientSprite.setSize(mGradientSprite.getWidth(), Gdx.graphics.getHeight() / 10.f);
		mGradientDrawable = new SpriteDrawable(mGradientSprite);
		
		TextureRegion gradientRegionHighlight = new TextureRegion(Assets.instance.gui.repeatGradientHighlight);
		mGradientHighlightSprite = new Sprite(gradientRegionHighlight);
		mGradientHighlightSprite.setSize(mGradientHighlightSprite.getWidth(), Gdx.graphics.getHeight() / 10.f);
		mGradientHighlightDrawable = new SpriteDrawable(mGradientHighlightSprite);
		
		//Build our GUI
		buildLabels();
		buildSongPane();	
		buildDifficultyTable();
		buildButtons();
		buildLayoutTable();	
		mStage.addActor(mLayoutTable);
		mStage.setKeyboardFocus(mSongsTable);
		
		//Keyboard navigation
		mSongsPane.addListener(new InputListener(){
			
			@Override
			public boolean keyDown(InputEvent event, int keycode){
				switch(keycode){
				case(Keys.DOWN):
					if (mSelectedIndex >= 0)
						dehighlightSong(mSelectedIndex);
					mSelectedIndex = (mSelectedIndex + 1) % Assets.instance.data.songs.size();
					highlightSong(mSelectedIndex);
					break;
				case(Keys.UP):
					dehighlightSong(mSelectedIndex);
					mSelectedIndex = (mSelectedIndex + Assets.instance.data.songs.size() - 1) % Assets.instance.data.songs.size();
					highlightSong(mSelectedIndex);
					break;
				case(Keys.ENTER):
					if (mSelectedIndex < 0) break;
					game.setScreen(new GameScreen(game, mSelectedIndex));
					break;
				case(Keys.ESCAPE):
					game.setScreen(new MenuScreen(game));
					break;
				}
				return true;
			}
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if (mSelectedIndex >= 0)
					dehighlightSong(mSelectedIndex);
				float rowPos = mSongsTable.getHeight() - (mSongsPane.getHeight() - y + mSongsPane.getScrollY());
				int row = mSongsTable.getRow(rowPos);
				mSelectedIndex = row;
				highlightSong(row);	
				return true;
			}
		});
		
		mStage.addListener(new InputListener(){
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				Actor touched = mStage.hit(x, y, true);	
				if (touched != null && touched.hasParent()){
					Actor parent = touched.getParent();
					if (parent.isDescendantOf(mSongsPane))
						return false;
					InputEvent touchDownEvent = new InputEvent();
					touchDownEvent.setType(Type.touchDown);
					//parent.fire(touchDownEvent);
				}
				return true;
			}
		});
	}
	
	private void dehighlightSong(int index){
		
		Table songTable = (Table) mSongsTable.getCells().get(mSelectedIndex).getActor();
		songTable.setBackground(mGradientDrawable);
	}
	
	private void highlightSong(int index){
		
		Table songTable = (Table) mSongsTable.getCells().get(mSelectedIndex).getActor();
		songTable.setBackground(mGradientHighlightDrawable);	
		mSongsPane.scrollTo(songTable.getX(), songTable.getY(), songTable.getWidth(), songTable.getHeight());
	}
	
	private void buildLabels(){
		
		mSelectLabel = new Label("Select Song", mSkin);
		mSelectLabel.setAlignment(Align.center);
		mSelectLabel.setWrap(true);
	}
	
	private void buildSongPane(){
		
		ArrayList<SongData> songs = Assets.instance.data.songs;
		mSongsTable = new Table();
		for (SongData data : songs){
			Table scrollTable = new Table();
			Label nameLabel = new Label(data.name, mSkin, "black");
			nameLabel.setAlignment(Align.center);
			//nameLabel.setTouchable(Touchable.disabled);
			Label artistLabel = new Label(data.artist, mSkin, "black");
			artistLabel.setAlignment(Align.center);
			//artistLabel.setTouchable(Touchable.disabled);
			String timeStr = Integer.toString((int) data.lengthSeconds);
			Label timeLabel = new Label(timeStr, mSkin, "black");
			//timeLabel.setTouchable(Touchable.disabled);
			String bpmStr = String.format("%.1f", data.bpm);
			Label bpmLabel = new Label(bpmStr, mSkin, "black");
			//bpmLabel.setTouchable(Touchable.disabled);
			bpmLabel.setAlignment(Align.center);
			scrollTable.add(nameLabel).expandX();
			scrollTable.add(artistLabel).expandX();
			scrollTable.add(timeLabel).expandX();
			scrollTable.add(bpmLabel).expandX();
			scrollTable.setBackground(mGradientDrawable);
			scrollTable.setTouchable(Touchable.enabled);
			mSongsTable.add(scrollTable).fillX().expandX();
			mSongsTable.row();
		}
		mSongsPane = new ScrollPane(mSongsTable);
	}
	
	private void buildDifficultyTable(){
		
		mDifficultyTable = new Table();
		Label easyLabel = new Label("Easy", mSkin);
		Label mediumLabel = new Label("Medium", mSkin);
		Label hardLabel = new Label("Hard", mSkin);
		Label easyScoreLabel = new Label("95.00", mSkin);
		Label mediumScoreLabel = new Label("91.15", mSkin);
		Label hardScoreLabel = new Label("87.20", mSkin);
		easyLabel.setAlignment(Align.center);
		mediumLabel.setAlignment(Align.center);
		hardLabel.setAlignment(Align.center);
		easyScoreLabel.setAlignment(Align.center);
		mediumScoreLabel.setAlignment(Align.center);
		hardScoreLabel.setAlignment(Align.center);
		mDifficultyTable.add(easyLabel).fill().expandX();
		mDifficultyTable.add(mediumLabel).fill().expandX();
		mDifficultyTable.add(hardLabel).fill().expandX().row();
		mDifficultyTable.add(easyScoreLabel).fill().expandX();
		mDifficultyTable.add(mediumScoreLabel).fill().expandX();
		mDifficultyTable.add(hardScoreLabel).fill().expandX();
	}
	
	private void buildButtons(){
		mPlayButton = new TextButton("Play", mSkin, "default");
		mPlayButton.addListener(new ClickListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				onPlayClicked();
				return true;
			}		
		});	
	}
	
	private void onPlayClicked(){
		if (mSelectedIndex < 0) return;
		game.setScreen(new GameScreen(game, mSelectedIndex));
	}
	
	private void buildLayoutTable(){
		
		mLayoutTable = new Table();
		mLayoutTable.setFillParent(true);
		mLayoutTable.add(mSelectLabel).pad(10.f).row();
		mLayoutTable.add(mSongsPane).fill().expand().row();
		mLayoutTable.add(mDifficultyTable).fill().expandX().pad(10.f).row();
		mLayoutTable.add(mPlayButton).expandX().pad(10.f);
		mLayoutTable.debug();
	}

	@Override
	public void render(float delta) {
		
		mStage.act(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mStage.draw();
		Table.drawDebug(mStage);	
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {
		mStage = new Stage();
		init();
		Gdx.input.setInputProcessor(mStage);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide() {
		Gdx.input.setCatchBackKey(false);
		mStage.dispose();
		mSkin.dispose();
	}

	@Override
	public void pause() {}

}
