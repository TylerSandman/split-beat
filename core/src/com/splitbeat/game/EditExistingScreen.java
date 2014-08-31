package com.splitbeat.game;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class EditExistingScreen extends AbstractGameScreen {
	
	private Stage mStage;
	private Skin mSkin;
	private Table mLayoutTable;
	
	private ScrollPane mSongsPane;
	private Table mSongsTable;
	private Table mDifficultyTable;
	private Label mDifficultyLabel;
	private ImageButton mLeftDifficultyButton;
	private ImageButton mRightDifficultyButton;
	private TextButton mEditButton;
	
	private int mSelectedIndex;
	private Difficulty mSelectedDifficulty;
	private ArrayList<SongData> mSongDataArr;

	EditExistingScreen(Game game) {
		super(game);
		mSelectedIndex = -1;
		mSongDataArr = new ArrayList<SongData>();
		mSelectedDifficulty = Difficulty.Easy;
	}
	
	private void init(){
		
		mSkin = new Skin(
			Gdx.files.internal(Constants.GUI_SKIN),
			new TextureAtlas(Constants.TEXTURE_ATLAS_GUI));
		
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
					selectDifficulty(mSelectedDifficulty.prev());
					break;
				case(Keys.RIGHT):
					selectDifficulty(mSelectedDifficulty.next());
					break;
				case(Keys.ENTER):
					if (mSelectedIndex < 0) break;
					onEditClicked();
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
				
		if (mSongDataArr.get(mSelectedIndex).getLeftMap(mSelectedDifficulty) == null){
			mEditButton.setText("New");
		}
		else{
			mEditButton.setText("Open");
		}
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
	
	private void selectDifficulty(Difficulty difficulty){
		
		mSelectedDifficulty = difficulty;
		
		switch(difficulty){
		case Easy:
			mDifficultyLabel.setText("Easy");
			break;
		case Medium:
			mDifficultyLabel.setText("Medium");
			break;
		case Hard:
			mDifficultyLabel.setText("Hard");
			break;
		}
		
		if (mSelectedIndex < 0) return;
		if (mSongDataArr.get(mSelectedIndex).getLeftMap(mSelectedDifficulty) == null){
			mEditButton.setText("New");
		}
		else{
			mEditButton.setText("Open");
		}	
	}
	
	private void buildButtons(){
		
		mEditButton = new TextButton("Open", mSkin, "default");
		mEditButton.addListener(new ClickListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				onEditClicked();
				return true;
			}		
		});	
	}
	
	private void buildSongPane(){
		
		mSongsTable = new Table();
		
		for (Map.Entry<String, Music> entry : Assets.instance.music.musicMap.entrySet()){
			
			SongData data = Assets.instance.maps.dataMap.get(entry.getKey());
			mSongDataArr.add(data);
			//Make labels based on song data
			Table scrollTable = new Table();
			Stack stack = new Stack();
			
			Label nameLabel = new Label(data.getTitle(), mSkin, "black");
			nameLabel.setAlignment(Align.center);
			Label artistLabel = new Label(data.getArtist(), mSkin, "black");
			artistLabel.setAlignment(Align.center);
			
			//Make each column fixed size for alignment
			float colWidth = Gdx.graphics.getWidth() / 4.f;
			scrollTable.add(nameLabel).width(colWidth);
			scrollTable.add(artistLabel).width(colWidth);
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
		
		mDifficultyLabel = new Label("Easy", mSkin);
		mDifficultyLabel.setAlignment(Align.center);
		mLeftDifficultyButton = new ImageButton(
				new TextureRegionDrawable(Assets.instance.gui.greySliderLeft));
		mRightDifficultyButton = new ImageButton(
				new TextureRegionDrawable(Assets.instance.gui.greySliderRight));
		
		mLeftDifficultyButton.addListener(new ClickListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				selectDifficulty(mSelectedDifficulty.prev());
				return true;
			}		
		});
		
		mRightDifficultyButton.addListener(new ClickListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				selectDifficulty(mSelectedDifficulty.next());
				return true;
			}		
		});
		
		float colWidth = Gdx.graphics.getWidth() / 6.f;
		mDifficultyTable.add(mLeftDifficultyButton).width(colWidth);
		mDifficultyTable.add(mDifficultyLabel).width(colWidth);
		mDifficultyTable.add(mRightDifficultyButton).width(colWidth);
	}

	private void buildLayoutTable(){
		
		mLayoutTable = new Table();
		mLayoutTable.setFillParent(true);
		mLayoutTable.add(mSongsPane).row();
		mLayoutTable.add(mDifficultyTable).pad(4 * Constants.CELL_PADDING).fillX().row();
		mLayoutTable.add(mEditButton);
		//mLayoutTable.debug();
	}
	
	private void onEditClicked(){
		if (mSelectedIndex < 0) return;
		game.setScreen(new SongEditScreen(game, mSongDataArr.get(mSelectedIndex).getName(), mSelectedDifficulty));
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
