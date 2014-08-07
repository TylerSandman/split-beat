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
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class SongSelectScreen extends AbstractGameScreen {
	
	private Stage mStage;
	private Skin mSkin;	
	private Table mLayoutTable;
	
	private ScrollPane mSongsPane;
	private Table mSongsTable;
	private Table mDifficultyTable;
	private Table mHeadersTable;
	private Label mSelectLabel;
	private TextButton mPlayButton;
	
	private Label mEasyScoreLabel;
	private Label mMediumScoreLabel;
	private Label mHardScoreLabel;
	
	private Image mEasySlider;
	private Image mMediumSlider;
	private Image mHardSlider;
	
	private int mSelectedIndex;
	private Difficulty mSelectedDifficulty;
	
	private Sprite mGradientSprite;
	private Sprite mGradientHighlightSprite;
	private SpriteDrawable mGradientDrawable;
	private SpriteDrawable mGradientHighlightDrawable;
	
	private enum Difficulty{
		Easy,
		Medium,
		Hard;
	}
	

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
		mGradientSprite.setSize(mGradientSprite.getWidth(), Gdx.graphics.getHeight() / Constants.CELL_PADDING);
		mGradientDrawable = new SpriteDrawable(mGradientSprite);
		
		TextureRegion gradientRegionHighlight = new TextureRegion(Assets.instance.gui.repeatGradientHighlight);
		mGradientHighlightSprite = new Sprite(gradientRegionHighlight);
		mGradientHighlightSprite.setSize(mGradientHighlightSprite.getWidth(), Gdx.graphics.getHeight() / Constants.CELL_PADDING);
		mGradientHighlightDrawable = new SpriteDrawable(mGradientHighlightSprite);
		
		//Build our GUI
		buildLabels();
		buildHeadersTable();
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
				int newIndex;
				Difficulty newDifficulty;
				switch(keycode){
				case(Keys.DOWN):
					if (mSelectedIndex >= 0)
						dehighlightSong(mSelectedIndex);
					newIndex = (mSelectedIndex + 1) % Options.instance.songsData.size();
					highlightSong(newIndex);
					break;
				case(Keys.UP):
					dehighlightSong(mSelectedIndex);
					newIndex = 
						(mSelectedIndex + Options.instance.songsData.size() - 1) % 
						Options.instance.songsData.size();
					highlightSong(newIndex);
					break;
				case(Keys.LEFT):
					newDifficulty =
						Difficulty.values()[(mSelectedDifficulty.ordinal() + Difficulty.values().length - 1) %
						                    Difficulty.values().length];
				selectDifficulty(newDifficulty);
					break;
				case(Keys.RIGHT):
					newDifficulty = 
						Difficulty.values()[(mSelectedDifficulty.ordinal() + 1) % 
						                    Difficulty.values().length];
					selectDifficulty(newDifficulty);
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
	}
	
	private void dehighlightSong(int index){
		if (mSelectedIndex < 0) return;
		Table songTable = (Table) mSongsTable.getCells().get(mSelectedIndex).getActor();
		songTable.setBackground(mGradientDrawable);
	}
	
	private void highlightSong(int index){
		
		mSelectedIndex = index;
		
		//Change background
		Table songTable = (Table) mSongsTable.getCells().get(mSelectedIndex).getActor();
		songTable.setBackground(mGradientHighlightDrawable);	
		
		//Update scores
		String easyScore = String.format("%.2f", Options.instance.songsData.get(index).getEasyScore());
		String mediumScore = String.format("%.2f", Options.instance.songsData.get(index).getMediumScore());
		String hardScore = String.format("%.2f", Options.instance.songsData.get(index).getHardScore());
		mEasyScoreLabel.setText(easyScore);
		mMediumScoreLabel.setText(mediumScore);
		mHardScoreLabel.setText(hardScore);
		
		//Move scroll pane accordingly
		mSongsPane.scrollTo(songTable.getX(), songTable.getY(), songTable.getWidth(), songTable.getHeight());
	}
	
	private void selectDifficulty(Difficulty difficulty){
		
		mSelectedDifficulty = difficulty;
		
		//Hide all sliders first
		mEasySlider.setVisible(false);
		mMediumSlider.setVisible(false);
		mHardSlider.setVisible(false);
		
		switch(difficulty){
		case Easy:
			mEasySlider.setVisible(true);
			break;
		case Medium:
			mMediumSlider.setVisible(true);
			break;
		case Hard:
			mHardSlider.setVisible(true);
			break;
		}
	}
	
	private void buildLabels(){
		
		mSelectLabel = new Label("Select Song", mSkin);
		mSelectLabel.setAlignment(Align.center);
		mSelectLabel.setWrap(true);
	}
	
	private void buildHeadersTable(){
		
		mHeadersTable = new Table();
		Label nameLabel = new Label("Song", mSkin);
		nameLabel.setAlignment(Align.center);
		Label artistLabel = new Label("Artist", mSkin);
		artistLabel.setAlignment(Align.center);
		Label lengthLabel = new Label("Length", mSkin);
		lengthLabel.setAlignment(Align.center);
		Label bpmLabel = new Label("BPM", mSkin);
		bpmLabel.setAlignment(Align.center);
		
		//Make each column fixed size for alignment
		float colWidth = Gdx.graphics.getWidth() / 4.f;
		mHeadersTable.add(nameLabel).width(colWidth);
		mHeadersTable.add(artistLabel).width(colWidth);
		mHeadersTable.add(lengthLabel).width(colWidth);
		mHeadersTable.add(bpmLabel).width(colWidth);
	}
	
	private void buildSongPane(){
		
		ArrayList<SongData> songs = Options.instance.songsData;
		mSongsTable = new Table();
		for (SongData data : songs){
			
			//Make labels based on song data
			Table scrollTable = new Table();
			Label nameLabel = new Label(data.getName(), mSkin, "black");
			nameLabel.setAlignment(Align.center);
			Label artistLabel = new Label(data.getArtist(), mSkin, "black");
			artistLabel.setAlignment(Align.center);
			String timeStr = Integer.toString(((int) data.getLength()) / 60);
			timeStr += ":";
			timeStr += Integer.toString(((int) data.getLength()) % 60);
			Label timeLabel = new Label(timeStr, mSkin, "black");
			timeLabel.setAlignment(Align.center);
			String bpmStr = String.format("%.1f", data.getBPM());
			Label bpmLabel = new Label(bpmStr, mSkin, "black");
			bpmLabel.setAlignment(Align.center);
			
			//Make each column fixed size for alignment
			float colWidth = Gdx.graphics.getWidth() / 4.f;
			scrollTable.add(nameLabel).width(colWidth);
			scrollTable.add(artistLabel).width(colWidth);
			scrollTable.add(timeLabel).width(colWidth);
			scrollTable.add(bpmLabel).width(colWidth);
			scrollTable.setBackground(mGradientDrawable);
			scrollTable.setTouchable(Touchable.enabled);
			//scrollTable.debug();
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
		
		mEasySlider = new Image(Assets.instance.gui.greenSlider);
		mMediumSlider = new Image(Assets.instance.gui.yellowSlider);
		mMediumSlider.setVisible(false);
		mHardSlider = new Image(Assets.instance.gui.redSlider);
		mHardSlider.setVisible(false);
		mSelectedDifficulty = Difficulty.Easy;
		
		mEasyScoreLabel = new Label("0.00", mSkin);
		mMediumScoreLabel = new Label("0.00", mSkin);
		mHardScoreLabel = new Label("0.00", mSkin);
		
		easyLabel.setAlignment(Align.center);
		mediumLabel.setAlignment(Align.center);
		hardLabel.setAlignment(Align.center);
		
		mEasyScoreLabel.setAlignment(Align.center);
		mMediumScoreLabel.setAlignment(Align.center);
		mHardScoreLabel.setAlignment(Align.center);
		
		//Fill empty table cells with spaces for proper alignment
		float spaceWidth = mEasySlider.getWidth() + Constants.CELL_PADDING;
		
		Table easyTable = new Table();
		easyTable.add(mEasySlider).padRight(Constants.CELL_PADDING);
		easyTable.add(easyLabel);
		easyTable.add().width(spaceWidth).row();
		easyTable.add().width(spaceWidth);
		easyTable.add(mEasyScoreLabel);
		easyTable.add().width(spaceWidth);
		easyTable.setTouchable(Touchable.enabled);
		
		Table mediumTable = new Table();
		mediumTable.add(mMediumSlider).padRight(Constants.CELL_PADDING);
		mediumTable.add(mediumLabel);
		mediumTable.add().width(spaceWidth).row();
		mediumTable.add().width(spaceWidth);
		mediumTable.add(mMediumScoreLabel);
		mediumTable.add().width(spaceWidth);
		mediumTable.setTouchable(Touchable.enabled);
		
		Table hardTable = new Table();
		hardTable.add(mHardSlider).padRight(Constants.CELL_PADDING);
		hardTable.add(hardLabel);
		hardTable.add().width(spaceWidth).row();
		hardTable.add().width(spaceWidth);
		hardTable.add(mHardScoreLabel);
		hardTable.add().width(spaceWidth);
		hardTable.setTouchable(Touchable.enabled);
		
		//Make each column fixed size for alignment
		float colWidth = Gdx.graphics.getWidth() / 3.f;
		mDifficultyTable.add(easyTable).width(colWidth).align(Align.center);
		mDifficultyTable.add(mediumTable).width(colWidth).align(Align.center);
		mDifficultyTable.add(hardTable).width(colWidth).align(Align.center);				
		
		//Touch support for difficulty switching
		easyTable.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				selectDifficulty(Difficulty.Easy);
				return true;
			}
		});
		
		mediumTable.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				selectDifficulty(Difficulty.Medium);
				return true;
			}
		});
		
		hardTable.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				selectDifficulty(Difficulty.Hard);
				return true;
			}
		});
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
		mLayoutTable.add(mSelectLabel).padBottom(Constants.CELL_PADDING).row();
		mLayoutTable.add(mHeadersTable).fill().expand().row();
		//mHeadersTable.debug();
		mLayoutTable.add(mSongsPane).fillX().row();
		mLayoutTable.add(mDifficultyTable).fill().expandX().pad(Constants.CELL_PADDING).row();
		//mDifficultyTable.debug();
		mLayoutTable.add(mPlayButton).expandX().pad(Constants.CELL_PADDING);
		//mLayoutTable.debug();
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
