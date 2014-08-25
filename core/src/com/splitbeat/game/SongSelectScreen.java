package com.splitbeat.game;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

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
	private ArrayList<SongData> mSongDataArr;
	
	SongSelectScreen(Game game) {
		super(game);
		mSelectedIndex = -1;
		mSongDataArr = new ArrayList<SongData>();
	}
	
	private void init(){
		
		mSkin = new Skin(
				Gdx.files.internal(Constants.GUI_SKIN),
				new TextureAtlas(Constants.TEXTURE_ATLAS_GUI));
		
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
					newIndex = (mSelectedIndex + 1) % Assets.instance.maps.dataMap.size();
					highlightSong(newIndex);
					break;
				case(Keys.UP):
					dehighlightSong(mSelectedIndex);
					newIndex = 
						(mSelectedIndex + Assets.instance.maps.dataMap.size() - 1) % 
						Assets.instance.maps.dataMap.size();
					highlightSong(newIndex);
					break;
				case(Keys.LEFT):
					if (mSelectedIndex < 0) break;
					Difficulty prevSelect = mSelectedDifficulty.prev();
					while (mSongDataArr.get(mSelectedIndex).getLeftMap(prevSelect) == null)
						prevSelect = prevSelect.prev();
					selectDifficulty(prevSelect);
					break;
				case(Keys.RIGHT):
					if (mSelectedIndex < 0) break;
					Difficulty nextSelect = mSelectedDifficulty.next();
					while (mSongDataArr.get(mSelectedIndex).getLeftMap(nextSelect) == null)
						nextSelect = nextSelect.next();
					selectDifficulty(nextSelect);
					break;
				case(Keys.ENTER):
					if (mSelectedIndex < 0) break;
					game.setScreen(new GameScreen(game, mSongDataArr.get(mSelectedIndex).getName(), mSelectedDifficulty));
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
				if (row >= mSongDataArr.size()) return true;
				mSelectedIndex = row;
				highlightSong(row);	
				return true;
			}
		});
	}
	
	private void dehighlightSong(int index){
		if (mSelectedIndex < 0) return;
		Stack songStack = (Stack) mSongsTable.getCells().get(mSelectedIndex).getActor();
		Table bgTable = (Table) songStack.getChildren().get(0);
		((Image) bgTable.getChildren().get(0)).setDrawable(
				new TextureRegionDrawable(Assets.instance.gui.redPanelLeft));;
		((Image) bgTable.getChildren().get(1)).setDrawable(
				new TextureRegionDrawable(Assets.instance.gui.redPanelRepeat));;			
		((Image) bgTable.getChildren().get(2)).setDrawable(
				new TextureRegionDrawable(Assets.instance.gui.redPanelRight));;
	}
	
	private void highlightSong(int index){
		
		mSelectedIndex = index;
		
		//Change background
		Stack songStack = (Stack) mSongsTable.getCells().get(mSelectedIndex).getActor();
		Table bgTable = (Table) songStack.getChildren().get(0);
		((Image) bgTable.getChildren().get(0)).setDrawable(
				new TextureRegionDrawable(Assets.instance.gui.highlightedPanelLeft));;
		((Image) bgTable.getChildren().get(1)).setDrawable(
				new TextureRegionDrawable(Assets.instance.gui.highlightedPanelRepeat));;			
		((Image) bgTable.getChildren().get(2)).setDrawable(
				new TextureRegionDrawable(Assets.instance.gui.highlightedPanelRight));;
		
		//Update scores
		SongScore scores = Options.instance.getScores(mSongDataArr.get(mSelectedIndex).getName());
		String easyScore = String.format("%.2f", scores.getEasy());
		String mediumScore = String.format("%.2f", scores.getMedium());
		String hardScore = String.format("%.2f", scores.getHard());
		
		//Update difficulty table based on available difficulties
		mEasyScoreLabel.setColor(Color.WHITE);
		mMediumScoreLabel.setColor(Color.WHITE);
		mHardScoreLabel.setColor(Color.WHITE);
		
		if (mSongDataArr.get(mSelectedIndex).getLeftMap(Difficulty.Easy) == null)
			mEasyScoreLabel.setColor(Color.GRAY);
		if (mSongDataArr.get(mSelectedIndex).getLeftMap(Difficulty.Medium) == null)
			mMediumScoreLabel.setColor(Color.GRAY);
		if (mSongDataArr.get(mSelectedIndex).getLeftMap(Difficulty.Hard) == null)
			mHardScoreLabel.setColor(Color.GRAY);

		mEasyScoreLabel.setText(easyScore);
		mMediumScoreLabel.setText(mediumScore);
		mHardScoreLabel.setText(hardScore);
		
		//Move scroll pane accordingly
		mSongsPane.scrollTo(songStack.getX(), songStack.getY(), songStack.getWidth(), songStack.getHeight());
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
		
		mSongsTable = new Table();
		
		for (Map.Entry<String, SongData> entry : Assets.instance.maps.dataMap.entrySet()){
			
			SongData data = entry.getValue();
			mSongDataArr.add(data);
			//Make labels based on song data
			Table scrollTable = new Table();
			Stack stack = new Stack();
			
			Label nameLabel = new Label(data.getTitle(), mSkin, "black");
			nameLabel.setAlignment(Align.center);
			Label artistLabel = new Label(data.getArtist(), mSkin, "black");
			artistLabel.setAlignment(Align.center);
			String timeStr = Integer.toString(((int) data.getLength()) / 60);
			timeStr += ":";
			timeStr += Integer.toString(((int) data.getLength()) % 60);
			Label timeLabel = new Label(timeStr, mSkin, "black");
			timeLabel.setAlignment(Align.center);
			String bpmStr = String.format("%.1f", data.getBpm());
			Label bpmLabel = new Label(bpmStr, mSkin, "black");
			bpmLabel.setAlignment(Align.center);
			
			//Make each column fixed size for alignment
			float colWidth = Gdx.graphics.getWidth() / 4.f;
			scrollTable.add(nameLabel).width(colWidth);
			scrollTable.add(artistLabel).width(colWidth);
			scrollTable.add(timeLabel).width(colWidth);
			scrollTable.add(bpmLabel).width(colWidth);
			scrollTable.setTouchable(Touchable.enabled);
			//scrollTable.debug();
			
			Table bgTable = new Table();
			bgTable.add(new Image(Assets.instance.gui.redPanelLeft));
			bgTable.add(new Image(Assets.instance.gui.redPanelRepeat)).fillX().expandX();
			bgTable.add(new Image(Assets.instance.gui.redPanelRight));
			stack.add(bgTable);
			stack.add(scrollTable);	
			mSongsTable.add(stack);
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
		
		
		String easyScoreStr = "0.00";
		String mediumScoreStr = "0.00";
		String hardScoreStr = "0.00";
		if (mSelectedIndex >= 0){
			SongScore scores = Options.instance.getScores(mSongDataArr.get(mSelectedIndex).getName());
			easyScoreStr = String.format("%.2f", scores.getEasy());
			mediumScoreStr = String.format("%.2f", scores.getMedium());
			hardScoreStr = String.format("%.2f", scores.getHard());
		}
		mEasyScoreLabel = new Label(easyScoreStr, mSkin);
		mMediumScoreLabel = new Label(mediumScoreStr, mSkin);
		mHardScoreLabel = new Label(hardScoreStr, mSkin);
		
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
				if (mSelectedIndex < 0) return true;
				selectDifficulty(Difficulty.Easy);
				return true;
			}
		});
		
		mediumTable.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				if (mSelectedIndex < 0) return true;
				selectDifficulty(Difficulty.Medium);
				return true;
			}
		});
		
		hardTable.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				if (mSelectedIndex < 0) return true;
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
		game.setScreen(new GameScreen(game, mSongDataArr.get(mSelectedIndex).getName(), mSelectedDifficulty));
	}
	
	private void buildLayoutTable(){
		
		mLayoutTable = new Table();
		mLayoutTable.setFillParent(true);
		mLayoutTable.add(mSelectLabel).padBottom(Constants.CELL_PADDING).row();
		mLayoutTable.add(mHeadersTable).fillX().expandX().row();
		//mHeadersTable.debug();
		mLayoutTable.add(mSongsPane).fillX().expandX().row();
		mLayoutTable.add(mDifficultyTable).fillX().expandX().pad(Constants.CELL_PADDING).row();
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
