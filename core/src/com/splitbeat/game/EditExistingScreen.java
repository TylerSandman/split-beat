package com.splitbeat.game;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EditExistingScreen extends AbstractGameScreen {
	
	private Stage mStage;
	private Skin mSkin;
	private Table mLayoutTable;
	
	private ScrollPane mSongsPane;
	private Table mSongsTable;
	private Label mDifficultyLabel;
	
	private int mSelectedIndex;
	private Difficulty mSelectedDifficulty;
	private ArrayList<SongData> mSongDataArr;

	EditExistingScreen(Game game) {
		super(game);
		mSelectedIndex = -1;
		mSongDataArr = new ArrayList<SongData>();
	}
	
	private void init(){
		
		mSkin = new Skin(
			Gdx.files.internal(Constants.GUI_SKIN),
			new TextureAtlas(Constants.TEXTURE_ATLAS_GUI));
		
		mStage.addActor(mLayoutTable);
		mStage.setKeyboardFocus(mSongsTable);
		
		//Keyboard navigation
		/*
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
					Difficulty prevSelect = mSelectedDifficulty.prev();
					while (mSongDataArr.get(mSelectedIndex).getLeftMap(prevSelect) == null)
						prevSelect = prevSelect.prev();
					selectDifficulty(prevSelect);
					break;
				case(Keys.RIGHT):
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
				mSelectedIndex = row;
				highlightSong(row);	
				return true;
			}
		});
		*/
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

}
