package com.splitbeat.game;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.splitbeat.game.Constants.NoteSlot;
import com.splitbeat.game.Constants.NoteType;
public class SongEditScreen extends AbstractGameScreen {
	
	private TmxMapBuilder mBuilder;
	private OrthographicCamera mLeftCamera;
	private OrthographicCamera mRightCamera;
	private OrthographicCamera mHUDCamera;
	private SongData mData;
	private String mName;
	private Difficulty mDifficulty;
	
	private TiledMap mLeftMap;
	private TiledMap mRightMap;
	private ArrayList<BPMMarker> mLeftMarkers;
	private ArrayList<BPMMarker> mRightMarkers;
	private ArrayList<Note> mLeftRegularNotes;
	private ArrayList<Note> mRightRegularNotes;
	private ArrayList<HoldNote> mLeftHoldNotes;
	private ArrayList<HoldNote> mRightHoldNotes;
	private ArrayList<HoldNote> mActiveHoldNotes;
	private OutlineNote[] mLeftOutlines;
	private OutlineNote[] mRightOutlines;
	private Color[] mNoteColors;
	private int[] mNoteQuantizations;
	private NoteClipboard mClipboard;
	private NoteSelection mNoteSelection;
	
	private Stage mStage;
	private InputListener mListener;
	private Skin mSkin;
	private ShapeRenderer mShapeRenderer;
	SpriteBatch mBatch;
	
	private DropDownMenu<String> mEditDropDownMenu;
	
	private Image mLeftButton;
	private Image mRightButton;
	private Image mUpButton;
	private Image mDownButton;
	
	private Table mInformationTable;
	private Image mMenuBackground;
	private Label mCurrentBeatLabel;
	private Label mCurrentSecondLabel;
	private Label mSnapToLabel;
	private Label mLengthLabel;
	
	private Dialog mBeatJumpDialog;
	private TextField mBeatJumpInput;
	
	private Dialog mSongInformationDialog;
	private TextField mNameInput;
	private TextField mArtistInput;
	private TextField mBpmInput;
	private TextField mOffsetInput;
	private SongData mPendingChangeData;
	
	private Fraction mCurrentBeatFraction;
	private float mCurrentBeat;
	private float mCurrentSecond;
	private int mColorIndex;
	private float mTrackWidth;
	private float mTrackHeight;
	
	private ScoreManager mScoreManager;
	private String[] mEditItems;
	
	SongEditScreen(Game game, String name, Difficulty difficulty){
		super(game);
		mName = name;
		mDifficulty = difficulty;
	}
	
	private void init(){
		
		mBuilder = new TmxMapBuilder(false);
		mShapeRenderer = new ShapeRenderer();
		mBatch = new SpriteBatch();
		mLeftMarkers = new ArrayList<BPMMarker>();
		mRightMarkers = new ArrayList<BPMMarker>();
		mLeftRegularNotes = new ArrayList<Note>();
		mRightRegularNotes = new ArrayList<Note>();
		mLeftHoldNotes = new ArrayList<HoldNote>();
		mRightHoldNotes = new ArrayList<HoldNote>();
		mActiveHoldNotes = new ArrayList<HoldNote>();
		mScoreManager = new ScoreManager();
		mEditItems = new String[]{
				"Steps information",
				"Play all",
				"Play from current beat",
				"Jump to beat",
				"Open/close selector",
				"Cut",
				"Copy",
				"Paste",
				"Save",
				"Exit"
		};
		mNoteColors = new Color[]{
				Color.RED,
				Color.BLUE,
				Color.YELLOW,
				Color.GREEN,
				Color.YELLOW,
				//Darker orange
				new Color(1.f, 0.5f, 0.0f, 1.f)
		};
		mNoteQuantizations = new int[]{
				1,
				2,
				3,
				4,
				6,
				8
		};
		mCurrentBeat = 0.f;
		mColorIndex = 0;
		mCurrentBeatFraction = new Fraction();
		mClipboard = new NoteClipboard();
		mNoteSelection = new NoteSelection();
		
		mSkin = new Skin(
				Gdx.files.internal(Constants.GUI_SKIN),
				new TextureAtlas(Constants.TEXTURE_ATLAS_GUI));
		
		//Configure cameras
		float w = Gdx.graphics.getWidth();
		
		//Trackwidth is as large as possible leaving room for GUI + padding
		mTrackWidth = w - 2 * Assets.instance.gui.leftArrow.getRegionWidth() - 4 * Constants.CELL_PADDING;
		
		//Same with trackheight
		mTrackHeight = Gdx.graphics.getHeight() * 0.5f;
		
		float camWidth = mTrackWidth;
		float camHeight = mTrackHeight + 4 * Assets.instance.fonts.defaultFont.getLineHeight() + 4 * Constants.CELL_PADDING;
		
		float h = Gdx.graphics.getHeight();
		mLeftCamera = new OrthographicCamera(camWidth, camHeight);
		mRightCamera = new OrthographicCamera(camWidth, camHeight);
		
		//Move cameras to bottom of screen
		float camTranslate = -Gdx.graphics.getHeight() / 2.f + mRightCamera.viewportHeight / 2.f;
		mLeftCamera.translate(0, camTranslate);		
		mRightCamera.translate(0, camTranslate);
		
		//Move up to make room for GUI + padding
		float camPadTranslate = Assets.instance.gui.downArrow.getRegionHeight() + 2 * Constants.CELL_PADDING;	
		mLeftCamera.translate(0, camPadTranslate);
		mRightCamera.translate(0, camPadTranslate);
		
		mHUDCamera = new OrthographicCamera(w, h);
		mHUDCamera.position.set(0, 0, 0);
		mHUDCamera.update();
		mLeftCamera.update();
		mRightCamera.update();
		
		mData = Assets.instance.maps.dataMap.get(mName);
		mBuilder.create(mData, mDifficulty);
		mLeftMap = mData.getLeftMap(mDifficulty);
		mRightMap = mData.getRightMap(mDifficulty);
		mPendingChangeData = new SongData(mData);
		
		buildMenu();
		buildButtons();
		buildInformation();
		buildDialogs();
		buildStage();
		
		//Parse tracks
		MapLayer noteLayer =  mRightMap.getLayers().get(0);
		MapObjects notes = noteLayer.getObjects();
		
		//Regular Notes
		for(MapObject note : notes){
			Note toAdd = NoteFactory.createNote(note, mRightMap, mScoreManager, false);
			placeNote(toAdd.beat, toAdd.slot, toAdd.type);
		}
		
		noteLayer = mRightMap.getLayers().get(1);
		notes = noteLayer.getObjects();
		
		//Hold Notes
		for(MapObject note : notes){
			HoldNote toAdd = (HoldNote) NoteFactory.createNote(note, mRightMap, mScoreManager, false);
			placeHoldNote(toAdd.beat, toAdd.getHoldDuration(), toAdd.slot, toAdd.type);
		}
		
		//BPM markers	
		if (mRightMap.getLayers().getCount() > 2){
			
			noteLayer = mRightMap.getLayers().get(2);
			notes = noteLayer.getObjects();
			
			MapLayer markerLayer = mRightMap.getLayers().get(2);
			MapObjects markers = markerLayer.getObjects();
			
			for(MapObject marker : markers){
				mRightMarkers.add(MarkerFactory.createMarker(marker, mRightMap, false));
			}
		}
		
		mListener = new InputListener(){
			
			@Override
			public boolean keyDown(InputEvent event, int keycode){
				switch(keycode){
				case(Keys.DOWN):
					onDownPress();
					break;
				case(Keys.UP):
					onUpPress();
					break;
				case(Keys.LEFT):
					onLeftPress();
					break;
				case(Keys.RIGHT):
					onRightPress();
					break;
				case(Keys.O):
					onSlotPress(NoteSlot.TOP_RIGHT);
					break;
				case(Keys.K):
					onSlotPress(NoteSlot.MIDDLE_RIGHT);
					break;
				case(Keys.M):
					onSlotPress(NoteSlot.BOTTOM_RIGHT);
					break;
				case(Keys.S):
					if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)){
						onSave();
					}
					break;
				case(Keys.ENTER):
					 //TODO SOMETHING
					break;
				case(Keys.ESCAPE):
					//TODO SOMETHING
					break;
				}
				return true;
			}
			
			@Override
			public boolean keyUp(InputEvent event, int keycode){
				switch(keycode){
				case(Keys.O):
					onSlotRelease(NoteSlot.TOP_RIGHT);
					break;
				case(Keys.K):
					onSlotRelease(NoteSlot.MIDDLE_RIGHT);
					break;
				case(Keys.M):
					onSlotRelease(NoteSlot.BOTTOM_RIGHT);
					break;
				}
				return true;
			}
		};
		
		mStage.addListener(mListener);		
		mRightOutlines = new OutlineNote[]{
				new OutlineNote(NoteSlot.TOP_LEFT, mScoreManager),
				new OutlineNote(NoteSlot.MIDDLE_LEFT, mScoreManager),
				new OutlineNote(NoteSlot.BOTTOM_LEFT, mScoreManager)
		};
		
		//Center outlines relative to track and up/down buttons
		for(OutlineNote note : mRightOutlines){
			note.setPosition(
					mRightCamera.position.x - note.getBounds().width / 2.f,
					mRightCamera.position.y - note.getBounds().height / 2.f);
		}
		
		//Move to appropriate slots
		float moveIncrement = 2 * mRightOutlines[0].getBounds().height - mRightOutlines[0].getBounds().height / 2;
		mRightOutlines[0].moveBy(0, -moveIncrement);
		mRightOutlines[2].moveBy(0, moveIncrement);
		
		//Move to starting position (beat 0)
		for(OutlineNote note : mRightOutlines){
			note.moveBy(mRightCamera.viewportWidth / 2.f - 2 * mUpButton.getWidth() / 2.f, 0);
		}
	}
	
	private void buildMenu(){

		mMenuBackground = new Image(Assets.instance.gui.redPanelRepeat);
		mMenuBackground.setWidth(Gdx.graphics.getWidth());
		mMenuBackground.setPosition(0, Gdx.graphics.getHeight() - mMenuBackground.getHeight());
		
		mEditDropDownMenu = new DropDownMenu<String>(mSkin, "default", "Edit");
		mEditDropDownMenu.setItems(mEditItems);
		
		//Edit song information
		mEditDropDownMenu.addItemListener(0, new InputListener(){
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				mNameInput.setText(mPendingChangeData.getTitle());
				mArtistInput.setText(mPendingChangeData.getArtist());
				mBpmInput.setText(Float.toString(mPendingChangeData.getBpm()));
				mOffsetInput.setText(Float.toString(mPendingChangeData.getOffset()));
				mSongInformationDialog.show(mStage);
				mStage.removeListener(mListener);
				mStage.setKeyboardFocus(mNameInput);
				mSongInformationDialog.padTop(2 * Constants.CELL_PADDING);
			}

		});
		//Jump to Beat
		mEditDropDownMenu.addItemListener(3, new InputListener(){
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				mBeatJumpDialog.show(mStage);
				mStage.removeListener(mListener);
				mStage.setKeyboardFocus(mBeatJumpInput);
				mBeatJumpDialog.padTop(2 * Constants.CELL_PADDING);
			}
		});
		
		//Open/close selector
		mEditDropDownMenu.addItemListener(4, new InputListener(){
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (mNoteSelection.isOpen())
					mNoteSelection.close(mCurrentBeat);			
				else
					mNoteSelection.open(mCurrentBeat);
				
				if (Math.abs(mNoteSelection.getEndBeat() - mNoteSelection.getStartBeat()) < 1.f / 64 &&
					!mNoteSelection.isOpen())
					mNoteSelection.clear();
			}
		});
		
		//Cut
		mEditDropDownMenu.addItemListener(5, new InputListener(){
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				cutNotes();
			}
		});
		
		//Copy
		mEditDropDownMenu.addItemListener(6, new InputListener(){
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				copyNotes();
			}
		});
		
		//Paste
		mEditDropDownMenu.addItemListener(7, new InputListener(){
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				pasteNotes();
			}
		});
		
		//Save
		mEditDropDownMenu.addItemListener(8, new InputListener(){
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				onSave();
			}

		});
		
		//Exit
		mEditDropDownMenu.addItemListener(9, new InputListener(){
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new MenuScreen(game));
			}

		});
		
		float maxWidth = 0.f;
		for(String item : mEditDropDownMenu.getItems()){
			float strWidth = mEditDropDownMenu.getStyle().font.getBounds(item).width;
			if (strWidth > maxWidth)
				maxWidth = strWidth; 
		}
		mEditDropDownMenu.setWidth(maxWidth + 2 * Constants.CELL_PADDING);
		mEditDropDownMenu.setPosition(0, Gdx.graphics.getHeight() - mEditDropDownMenu.getHeight());
		
		//Have to manually add left padding
		mEditDropDownMenu.getStyle().background.setLeftWidth(Constants.CELL_PADDING);
		mEditDropDownMenu.getStyle().background.setRightWidth(Constants.CELL_PADDING);
		mEditDropDownMenu.getList().getStyle().selection.setLeftWidth(Constants.CELL_PADDING);	
	}
	
	private void buildButtons(){
		
		mLeftButton = new Image(Assets.instance.gui.leftArrow);
		mRightButton = new Image(Assets.instance.gui.rightArrow);
		mUpButton = new Image(Assets.instance.gui.upArrow);
		mDownButton = new Image(Assets.instance.gui.downArrow);
		
		mUpButton.setColor(Color.RED);
		mDownButton.setColor(Color.RED);
		
		mLeftButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				onLeftPress();
				return true;
			}
		});
		
		mRightButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				onRightPress();
				return true;
			}
		});
		
		mUpButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				onUpPress();
				return true;
			}
		});
		
		mDownButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				onDownPress();
				return true;
			}
		});
	}
	
	private void buildInformation(){
		
		
		mInformationTable = new Table();
		
		Label currentBeatHeader = new Label("Current Beat", mSkin);
		currentBeatHeader.setAlignment(Align.center);
		currentBeatHeader.setWrap(true);
		
		Label currentSecondHeader = new Label("Current Time", mSkin);
		currentSecondHeader.setAlignment(Align.center);
		currentSecondHeader.setWrap(true);
		
		Label snapToHeader = new Label("Snap to", mSkin);
		snapToHeader.setAlignment(Align.center);
		snapToHeader.setWrap(true);
		
		Label songLengthHeader = new Label("Song Length", mSkin);
		songLengthHeader.setAlignment(Align.center);
		songLengthHeader.setWrap(true);
		
		mCurrentBeatLabel = new Label("", mSkin);
		mCurrentBeatLabel.setAlignment(Align.center);
		mCurrentBeatLabel.setWrap(true);
		
		mCurrentSecondLabel = new Label("", mSkin);
		mCurrentSecondLabel.setAlignment(Align.center);
		mCurrentSecondLabel.setWrap(true);
		
		mSnapToLabel = new Label("",  mSkin);
		mSnapToLabel.setAlignment(Align.center);
		mSnapToLabel.setWrap(true);
		
		mLengthLabel = new Label(Integer.toString(mData.getLength()) + " s", mSkin);
		mLengthLabel.setAlignment(Align.center);
		mLengthLabel.setWrap(true);;

		mInformationTable.add(currentBeatHeader).fillX().expandX();
		mInformationTable.add(currentSecondHeader).fillX().expandX();
		mInformationTable.add(snapToHeader).fillX().expandX();
		mInformationTable.add(songLengthHeader).fillX().expandX().row();
		mInformationTable.add(mCurrentBeatLabel).fillX().expandX();
		mInformationTable.add(mCurrentSecondLabel).fillX().expandX();
		mInformationTable.add(mSnapToLabel).fillX().expandX();
		mInformationTable.add(mLengthLabel).fillX().expandX();
		mInformationTable.setWidth(mRightCamera.viewportWidth - 2 * Assets.instance.gui.upArrow.getRegionWidth());
		//mInformationTable.debug();

		//Center the table
		mInformationTable.setPosition(Gdx.graphics.getWidth() / 2.f, Gdx.graphics.getHeight() / 2.f);
		
		float moveX = -mRightCamera.viewportWidth / 2.f;
		
		float moveY = mRightCamera.viewportHeight / 2.f + mRightCamera.position.y + Constants.CELL_PADDING;
		//Move to top left of track viewport
		mInformationTable.moveBy(moveX, moveY);
	}
	
	private void buildDialogs(){
		
		buildBeatJumpDialog();
		buildSongInformationDialog();	
	}
	
	private void buildBeatJumpDialog(){
		
		mBeatJumpDialog = new Dialog("Jump to Beat", mSkin, "default");
		mBeatJumpInput = new TextField("", mSkin);
		
		//Accept only digits
		mBeatJumpInput.setTextFieldFilter(new TextFieldFilter(){
			
			public boolean acceptChar(TextField textField, char c){
				if (Character.isDigit(c)) return true;
				return false;
			}
		});
		
		mBeatJumpInput.setTextFieldListener(new TextFieldListener(){

			@Override
			public void keyTyped(TextField textField, char c) {
				if (c == '\n' || c == '\r'){
					if (!textField.getText().equals("")){
						mBeatJumpDialog.hide();				
						mStage.setKeyboardFocus(mEditDropDownMenu);
						scrollToBeat(Integer.parseInt(textField.getText()));
						mBeatJumpInput.setText("");
						mStage.addListener(mListener);
					}
				}
			}	
		});
		
		mBeatJumpInput.setMaxLength(4);	
		mBeatJumpDialog.setPosition(Gdx.graphics.getWidth() / 2.f, Gdx.graphics.getHeight() / 2.f);		

		mBeatJumpDialog.row();
		mBeatJumpDialog.add(mBeatJumpInput).pad(Constants.CELL_PADDING).row();
		
		Button goButton = new TextButton("Go", mSkin, "default");
		
		goButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if (!mBeatJumpInput.getText().equals("")){
					mBeatJumpDialog.hide();
					mStage.setKeyboardFocus(mEditDropDownMenu);
					scrollToBeat(Integer.parseInt(mBeatJumpInput.getText()));	
					mBeatJumpInput.setText("");
					mStage.addListener(mListener);
				}
				return true;
			}
		});
		
		mBeatJumpDialog.add(goButton).row();
		mBeatJumpDialog.setBackground(new NinePatchDrawable(Assets.instance.gui.greyPanelNinePatch));
	}
	
	private void buildSongInformationDialog(){
		
		mSongInformationDialog = new Dialog("Edit Song Info", mSkin, "default");
		mSongInformationDialog.setPosition(Gdx.graphics.getWidth() / 2.f, Gdx.graphics.getHeight() / 2.f);		
		mSongInformationDialog.row();
		
		mNameInput = new TextField(mData.getName(), mSkin);
		mArtistInput = new TextField(mData.getArtist(), mSkin);
		mBpmInput = new TextField(Float.toString(mData.getBpm()), mSkin);
		mOffsetInput = new TextField(Float.toString(mData.getOffset()), mSkin);
		
		//Accept only digits and decimals
		mBpmInput.setTextFieldFilter(new TextFieldFilter(){
			
			public boolean acceptChar(TextField textField, char c){
				if (Character.isDigit(c) || c == '.') return true;
				return false;
			}
		});
		
		//Accept only digits, decimals, and the negative sign
		mOffsetInput.setTextFieldFilter(new TextFieldFilter(){
			
			public boolean acceptChar(TextField textField, char c){
				if (Character.isDigit(c) || c == '.' || c == '-') return true;
				return false;
			}
		});
		
		TextFieldListener infoListener = new TextFieldListener(){
			
			@Override
			public void keyTyped(TextField textField, char c){
				if (c == '\n' || c == 'r'){
					if (!informationInputsBlank()){
						onInformationEdit();
					}
				}
			}
		};
		
		mNameInput.setTextFieldListener(infoListener);
		mArtistInput.setTextFieldListener(infoListener);
		mBpmInput.setTextFieldListener(infoListener);
		mOffsetInput.setTextFieldListener(infoListener);
		
		mSongInformationDialog.add(
				new Label("Name", mSkin, "black"))
				.align(Align.left)
				.padBottom(Constants.CELL_PADDING)
				.padTop(Constants.CELL_PADDING);
		
		mSongInformationDialog.add(mNameInput)
				.align(Align.right)
				.padBottom(Constants.CELL_PADDING)
				.padTop(Constants.CELL_PADDING)
				.row();
		
		mSongInformationDialog.add(
				new Label("Artist", mSkin, "black"))
				.align(Align.left)
				.padBottom(Constants.CELL_PADDING);
		
		mSongInformationDialog.add(mArtistInput)
				.align(Align.right)
				.padBottom(Constants.CELL_PADDING)
				.row();
		
		mSongInformationDialog.add(
				new Label("BPM", mSkin, "black"))
				.align(Align.left)
				.padBottom(Constants.CELL_PADDING);
		
		mSongInformationDialog.add(mBpmInput)
				.align(Align.right)
				.padBottom(Constants.CELL_PADDING)
				.row();
		
		mSongInformationDialog.add(
				new Label("Song Offset", mSkin, "black"))
				.align(Align.left)
				.padBottom(Constants.CELL_PADDING);
		
		mSongInformationDialog.add(mOffsetInput)
				.align(Align.right)
				.padBottom(Constants.CELL_PADDING)
				.row();
				
		Button goButton = new TextButton("Go", mSkin, "default");
		
		goButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if (!informationInputsBlank()){
					onInformationEdit();
				}
				return true;
			}
		});
		
		mSongInformationDialog.add(goButton).align(Align.center).colspan(2);
		
		mSongInformationDialog.setBackground(new NinePatchDrawable(Assets.instance.gui.greyPanelNinePatch));
		mSongInformationDialog.debug();
	}
	
	private void onSave(){
		
		AudioManager.instance.dispose();
		mBuilder.updateSongData(mPendingChangeData);
		mBuilder.save();				
		Assets.instance.maps.updateMap(mData.getName(), mPendingChangeData);
		Options.instance.updateScores(mData.getName(), mPendingChangeData.getName());
		mData = new SongData(mPendingChangeData);
	}
	
	private void onInformationEdit(){
		
		mSongInformationDialog.hide();					
		mStage.setKeyboardFocus(mEditDropDownMenu);
		mPendingChangeData.setTitle(mNameInput.getText());
		mPendingChangeData.setArtist(mArtistInput.getText());
		mPendingChangeData.setBpm(Float.parseFloat(mBpmInput.getText()));
		mPendingChangeData.setOffset(Float.parseFloat(mOffsetInput.getText()));
		mNameInput.setText("");
		mArtistInput.setText("");
		mBpmInput.setText("");
		mOffsetInput.setText("");
		mStage.addListener(mListener);
	}
	
	
	private boolean informationInputsBlank(){
		return (mNameInput.getText().equals("") ||
				mArtistInput.getText().equals("") ||
				mBpmInput.getText().equals("") ||
				mOffsetInput.getText().equals(""));
	}
	
	private void buildStage(){
		
		mStage.addActor(mLeftButton);
		mStage.addActor(mRightButton);
		mStage.addActor(mUpButton);
		mStage.addActor(mDownButton);		
		mStage.addActor(mInformationTable);
		mStage.addActor(mMenuBackground);
		mStage.addActor(mEditDropDownMenu);
		mStage.setKeyboardFocus(mEditDropDownMenu);

		//Center origins	
		mLeftButton.moveBy(-mRightButton.getWidth() / 2.f, -mRightButton.getHeight() / 2.f);
		mRightButton.moveBy(-mRightButton.getWidth() / 2.f, -mRightButton.getHeight() / 2.f);
		mUpButton.moveBy(-mUpButton.getWidth() / 2.f, -mUpButton.getHeight() / 2.f);
		mDownButton.moveBy(-mDownButton.getWidth() / 2.f, -mDownButton.getHeight() / 2.f);
		
		//Move buttons to base position		
		mLeftButton.moveBy(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		mRightButton.moveBy(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		mUpButton.moveBy(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		mDownButton.moveBy(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		
		//Translate to align with track
		mLeftButton.moveBy(0, mRightCamera.position.y);
		mRightButton.moveBy(0, mRightCamera.position.y);
		mUpButton.moveBy(0, mRightCamera.position.y);
		mDownButton.moveBy(0, mRightCamera.position.y);
		
		//Move relative to track
		mLeftButton.moveBy(
				-mRightCamera.viewportWidth / 2 - mRightButton.getWidth() / 2.f - Constants.CELL_PADDING,
				0);
		mRightButton.moveBy(
				mRightCamera.viewportWidth / 2 + mRightButton.getWidth() /2.f + Constants.CELL_PADDING,
				0);
		mUpButton.moveBy(
				mRightCamera.viewportWidth / 2 - mUpButton.getWidth(),
				mRightCamera.viewportHeight / 2 + mUpButton.getHeight() / 2 + Constants.CELL_PADDING);
		mDownButton.moveBy(
				mRightCamera.viewportWidth / 2 - mDownButton.getWidth(),
				-mRightCamera.viewportHeight / 2 - mDownButton.getHeight() / 2 - Constants.CELL_PADDING);		
	}
	
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
		mBatch.dispose();
		mShapeRenderer.dispose();
	}
	
	public void update(float delta){
		
		String beatStr = String.format("%.3f", mCurrentBeat);
		mCurrentBeatLabel.setText(beatStr);
		
		String secondStr = String.format("%.2f", mCurrentSecond);
		secondStr += " s";
		mCurrentSecondLabel.setText(secondStr);
		
		int quantization = mNoteQuantizations[mColorIndex];
		String snapStr = Integer.toString(quantization * 4);
		snapStr += (quantization == 8) ? "nd" : "th";
		snapStr += " notes";
		mSnapToLabel.setText(snapStr);
		
		cleanupNotes();
	}
	
	public void cleanupNotes(){
		
		ArrayList<Note> toRemove = new ArrayList<Note>();
		for(Note note : mRightRegularNotes){
			if (note.isFlaggedForRemoval()){
				toRemove.add(note);
				mBuilder.removeNote(note);
			}
		}
		for(HoldNote note : mRightHoldNotes){
			if (note.isFlaggedForRemoval()){
				toRemove.add(note);
				mBuilder.removeHold(note);
			}
		}
		mRightRegularNotes.removeAll(toRemove);
		mRightHoldNotes.removeAll(toRemove);
	}

	@Override
	public void render(float delta) {
		
		mStage.act(delta);
		update(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glViewport(
				(int)(Gdx.graphics.getWidth() / 2 - mRightCamera.viewportWidth / 2), 
				(int)(Gdx.graphics.getHeight() / 2 + mRightCamera.position.y - mRightCamera.viewportHeight / 2), 
				(int)mRightCamera.viewportWidth, 
				(int)mRightCamera.viewportHeight);
		mShapeRenderer.setProjectionMatrix(mRightCamera.combined);	
		mShapeRenderer.begin(ShapeType.Line);
		mShapeRenderer.setColor(Color.WHITE);
		float measureWidthPixels = mRightOutlines[0].getBounds().width * Constants.MEASURE_WIDTH_NOTES;
		for (int i = 0; i <= (int)(mData.getBpm() * (mData.getLength() / 60.f)); ++i){
			mShapeRenderer.line(
				mRightCamera.viewportWidth / 2.f - 2 * mUpButton.getWidth() / 2.f - i * measureWidthPixels,
				mRightCamera.position.y + mTrackHeight / 2.f,
				mRightCamera.viewportWidth / 2.f - 2 * mUpButton.getWidth() / 2.f - i * measureWidthPixels,
				mRightCamera.position.y - mTrackHeight / 2.f);
		}
		mShapeRenderer.end();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mStage.draw();	
		Table.drawDebug(mStage);
		renderTrack();
	}
	
	private void renderTrack(){

		mBatch.setProjectionMatrix(mRightCamera.combined);
		mBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glViewport(
				(int)(Gdx.graphics.getWidth() / 2 - mRightCamera.viewportWidth / 2), 
				(int)(Gdx.graphics.getHeight() / 2 + mRightCamera.position.y - mRightCamera.viewportHeight / 2), 
				(int)mRightCamera.viewportWidth, 
				(int)mRightCamera.viewportHeight);
		mBatch.begin();
		renderOutlines();
		renderNotes();
		renderMeasures();		
		mBatch.end();
		renderSelection();
	}
	
	private void renderOutlines(){
		
		for(OutlineNote note : mRightOutlines){
			note.render(mBatch);
		}
	}
	
	private void renderNotes(){
		Gdx.gl20.glBlendFunc(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
		for(Note note : mRightRegularNotes){
			note.render(mBatch);
		}
		for(HoldNote note : mRightHoldNotes){
			note.render(mBatch);
		}
		for(HoldNote note : mActiveHoldNotes){
			note.render(mBatch);
		}
	}
	
	private void renderMeasures(){
		int numMeasures = (int)Math.ceil(mData.getBpm() * mData.getLength() / 60.f / 4.f);
		float measureWidthPixels = mRightOutlines[0].getBounds().width * Constants.MEASURE_WIDTH_NOTES;		
		float renderY = mRightCamera.position.y + mTrackHeight / 2.f + Assets.instance.fonts.defaultFont.getLineHeight() + Constants.CELL_PADDING;
		float renderX = mTrackWidth / 2.f - mUpButton.getWidth();
		for(int i = 1; i < numMeasures; ++i){			
			Assets.instance.fonts.defaultFont.drawMultiLine(
					mBatch, Integer.toString(i),
					renderX, renderY,
					0, BitmapFont.HAlignment.CENTER);
			renderX -= 4 * measureWidthPixels;
		}
	}
	
	private void renderSelection(){
		
		if (!mNoteSelection.isActive()) return;
		Gdx.gl.glEnable(GL20.GL_BLEND);
		mShapeRenderer.begin(ShapeType.Filled);
		mShapeRenderer.setColor(1, 1, 1, 0.35f);
		float measureWidthPixels = mRightOutlines[0].getBounds().width * Constants.MEASURE_WIDTH_NOTES;
		float baseWidth = mRightOutlines[0].getBounds().width;
		mShapeRenderer.rect(
				mRightCamera.viewportWidth / 2.f - 2 * mUpButton.getWidth() / 2.f - mNoteSelection.getEndBeat() * measureWidthPixels - mRightOutlines[0].getBounds().width / 2.f,
				mRightCamera.position.y - mTrackHeight / 2.f,
				baseWidth + (mNoteSelection.getEndBeat() - mNoteSelection.getStartBeat()) * measureWidthPixels,
				mTrackHeight);
		mShapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}
	
	private boolean removeNote(NoteSlot slot, float beat){
		
		//Don't use equality due to floating point error
		for(Note note : mRightRegularNotes){
			if (Math.abs(note.beat - beat) < 1.f/64 && note.slot == slot){
				note.flagForRemoval();
				return true;
			}
		}
		
		for(HoldNote note : mRightHoldNotes){
			if (beat <= note.beat + note.getHoldDuration() && 
				(beat > note.beat || Math.abs(beat - note.beat) < 1.f/64) && 
				note.slot == slot){
				note.flagForRemoval();
				return true;
			}
		}
		return false;
	}
	
	private void placeNote(float beat, NoteSlot slot, NoteType type){
		
		removeNote(slot, (beat - mCurrentBeat));
		Note note = new Note(beat, slot, type, mScoreManager);
		
		//Center note relative to track and up/down buttons
		note.setPosition(
				mRightCamera.position.x - note.getBounds().width / 2.f,
				mRightCamera.position.y - note.getBounds().height / 2.f);
		
		//Move to appropriate slot
		float moveIncrement = 2 * note.getBounds().height - note.getBounds().height / 2;
		switch(slot){
		case MIDDLE_LEFT:
			break;
		case TOP_LEFT:
			note.moveBy(0, moveIncrement);
			break;
		case BOTTOM_LEFT:
			note.moveBy(0, -moveIncrement);
			break;
		case MIDDLE_RIGHT:
			break;
		case TOP_RIGHT:
			note.moveBy(0, moveIncrement);
			break;
		case BOTTOM_RIGHT:
			note.moveBy(0, -moveIncrement);
			break;
		}
		
		//Move to starting position (beat 0)
		note.moveBy(mRightCamera.viewportWidth / 2.f - 2 * mUpButton.getWidth() / 2.f, 0);
		
		//Move to appropriate beat
		float measureWidthPixels = note.getBounds().width * Constants.MEASURE_WIDTH_NOTES;
		note.moveBy(-measureWidthPixels * (beat - mCurrentBeat), 0.f);
		
		mRightRegularNotes.add(note);
		mBuilder.addNote(note);
	}

	
	private void placeNewNote(NoteSlot slot, NoteType type){
		
		Note note = new Note(mCurrentBeat, slot, type, mScoreManager);
		
		//Center note relative to track and up/down buttons
		note.setPosition(
				mRightCamera.position.x - note.getBounds().width / 2.f,
				mRightCamera.position.y - note.getBounds().height / 2.f);
		
		//Move to appropriate slot
		float moveIncrement = 2 * note.getBounds().height - note.getBounds().height / 2;
		switch(slot){
		case MIDDLE_LEFT:
			break;
		case TOP_LEFT:
			note.moveBy(0, moveIncrement);
			break;
		case BOTTOM_LEFT:
			note.moveBy(0, -moveIncrement);
			break;
		case MIDDLE_RIGHT:
			break;
		case TOP_RIGHT:
			note.moveBy(0, moveIncrement);
			break;
		case BOTTOM_RIGHT:
			note.moveBy(0, -moveIncrement);
			break;
		}
		
		//Move to starting position (beat 0)
		note.moveBy(mRightCamera.viewportWidth / 2.f - 2 * mUpButton.getWidth() / 2.f, 0);
		
		mRightRegularNotes.add(note);
		mBuilder.addNote(note);
	}
	
	private void placeHoldNote(float beat, float duration, NoteSlot slot, NoteType type){
		
		removeNote(slot, (beat - mCurrentBeat));
		HoldNote note = new HoldNote(beat, slot, type, duration, mData.getBpm(), mScoreManager);
		
		//Center note relative to track and up/down buttons
		note.setPosition(
				mRightCamera.position.x - note.getHitBounds().width / 2.f,
				mRightCamera.position.y - note.getHitBounds().height / 2.f);
		
		//Move to appropriate slot
		float moveIncrement = 2 * note.getHitBounds().height - note.getHitBounds().height / 2;
		switch(slot){
		case MIDDLE_LEFT:
			break;
		case TOP_LEFT:
			note.moveBy(0, moveIncrement);
			break;
		case BOTTOM_LEFT:
			note.moveBy(0, -moveIncrement);
			break;
		case MIDDLE_RIGHT:
			break;
		case TOP_RIGHT:
			note.moveBy(0, moveIncrement);
			break;
		case BOTTOM_RIGHT:
			note.moveBy(0, -moveIncrement);
			break;
		}
		
		//Move to starting position (beat 0)
		note.moveBy(mRightCamera.viewportWidth / 2.f - 2 * mUpButton.getWidth() / 2.f, 0);
		
		//Move to appropriate beat
		float measureWidthPixels = note.getHitBounds().width * Constants.MEASURE_WIDTH_NOTES;
		note.moveBy(-measureWidthPixels * (beat - mCurrentBeat), 0.f);
		
		mRightHoldNotes.add(note);
		mBuilder.addHold(note);
	}
	
	private void placeNewHoldNote(NoteSlot slot, NoteType type){
		
		removeNote(slot, mCurrentBeat);
		HoldNote note = new HoldNote(mCurrentBeat, slot, type, 0.f, mData.getBpm(), mScoreManager);
		
		//Center note relative to track and up/down buttons
		note.setPosition(
				mRightCamera.position.x - note.getHitBounds().width / 2.f,
				mRightCamera.position.y - note.getHitBounds().height / 2.f);
		
		//Move to appropriate slot
		float moveIncrement = 2 * note.getHitBounds().height - note.getHitBounds().height / 2;
		switch(slot){
		case MIDDLE_LEFT:
			break;
		case TOP_LEFT:
			note.moveBy(0, moveIncrement);
			break;
		case BOTTOM_LEFT:
			note.moveBy(0, -moveIncrement);
			break;
		case MIDDLE_RIGHT:
			break;
		case TOP_RIGHT:
			note.moveBy(0, moveIncrement);
			break;
		case BOTTOM_RIGHT:
			note.moveBy(0, -moveIncrement);
			break;
		}
		
		//Move to starting position (beat 0)
		note.moveBy(mRightCamera.viewportWidth / 2.f - 2 * mUpButton.getWidth() / 2.f, 0);
		
		mActiveHoldNotes.add(note);
	}
	
	private void onLeftPress(){
		
		int maxBeat = (int) (mData.getBpm() * (mData.getLength() / 60.f));
		if (mCurrentBeatFraction.getNumerator() == maxBeat && mCurrentBeatFraction.getDenominator() == 1) return;
		float measureWidthPixels = mRightOutlines[0].getBounds().width * Constants.MEASURE_WIDTH_NOTES;		
		mCurrentBeatFraction = mCurrentBeatFraction.plus(1, mNoteQuantizations[mColorIndex]);
	
		if (mCurrentBeatFraction.toFloat() > maxBeat)
			mCurrentBeatFraction = new Fraction(maxBeat, 1);
		
		if ((int)mCurrentBeatFraction.toFloat() == maxBeat){
			for(OutlineNote note : mRightOutlines){
				note.moveBy(-(maxBeat - mCurrentBeat) * measureWidthPixels, 0.f);
			}
			mRightCamera.translate(-(maxBeat - mCurrentBeat) * measureWidthPixels, 0.f);
		}
		else{			
			for(OutlineNote note : mRightOutlines){
				note.moveBy(-measureWidthPixels / mNoteQuantizations[mColorIndex], 0);
			}
			mRightCamera.translate(-measureWidthPixels / mNoteQuantizations[mColorIndex], 0);
		}
		
		mCurrentBeat = mCurrentBeatFraction.toFloat();
		mCurrentSecond = mCurrentBeat / (mData.getBpm() / 60.f);
		mRightCamera.update();
		
		//Extend active hold notes accordingly
		for(HoldNote activeNote : mActiveHoldNotes){
			activeNote.addHoldDuration(1.f / mNoteQuantizations[mColorIndex]);
			
			//Remove regular and hold notes along the way
			for(Note regNote : mRightRegularNotes){
				if (regNote.beat <= activeNote.beat + activeNote.getHoldDuration() && 
					(regNote.beat > activeNote.beat || Math.abs(regNote.beat - activeNote.beat) < 1.f / 64) && 
					activeNote.slot == regNote.slot){
					regNote.flagForRemoval();
				}
			}
			for(HoldNote holdNote : mRightHoldNotes){
				if (holdNote.beat <= activeNote.beat + activeNote.getHoldDuration() && 
					(holdNote.beat > activeNote.beat || Math.abs(holdNote.beat - activeNote.beat) < 1.f / 64) && 
					activeNote.slot == holdNote.slot){
					holdNote.flagForRemoval();
				}
			}
		}
	}
	
	private void onRightPress(){
			
		if (mCurrentBeat == 0.f) return;
		mCurrentBeatFraction = mCurrentBeatFraction.minus(1, mNoteQuantizations[mColorIndex]);
		if (mCurrentBeatFraction.toFloat() < 0.f)
			mCurrentBeatFraction = new Fraction();
		mCurrentBeat = mCurrentBeatFraction.toFloat();
		if (mCurrentBeat == 0.f)
			mCurrentSecond = 0.f;
		else
			mCurrentSecond = mCurrentBeat / (mData.getBpm() / 60.f);
		
		float measureWidthPixels = mRightOutlines[0].getBounds().width * Constants.MEASURE_WIDTH_NOTES;
		if (mCurrentBeat == 0.f)
			mRightCamera.position.x = 0.f;
		else
			mRightCamera.translate(measureWidthPixels / mNoteQuantizations[mColorIndex], 0);
		mRightCamera.update();
			
		float beatZeroX = mRightCamera.viewportWidth / 2.f - mUpButton.getWidth() - mRightOutlines[0].getBounds().width / 2.f;
		if (mCurrentBeat == 0.f){
			for(OutlineNote note : mRightOutlines){
				note.setPosition(beatZeroX, note.position.y);
			}
		}
		else{			
			for(OutlineNote note : mRightOutlines){
				note.moveBy(measureWidthPixels / mNoteQuantizations[mColorIndex], 0);
			}
		}
		
		ArrayList<HoldNote> toRemove = new ArrayList<HoldNote>();
		//Decrease active hold notes length accordingly
		for(HoldNote activeNote : mActiveHoldNotes){
			
			//Remove active hold notes where we've scrolled past their starting point
			if (mCurrentBeat < activeNote.beat){
				toRemove.add(activeNote);		
				continue;
			}
			activeNote.addHoldDuration(-1.f / mNoteQuantizations[mColorIndex]);
		}
		mActiveHoldNotes.removeAll(toRemove);
	}
	
	private void onUpPress(){
		
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ||
			Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)){
			float newZoom = mRightCamera.zoom - Constants.ZOOM_INCREMENT;
			if (newZoom < Constants.MIN_ZOOM)
				return;
			else
				mRightCamera.zoom = newZoom;
			mRightCamera.update();
		}
		
		else{
			mColorIndex = (mColorIndex + mNoteColors.length - 1) % mNoteColors.length;
			mUpButton.setColor(mNoteColors[mColorIndex]);
			mDownButton.setColor(mNoteColors[mColorIndex]);
		}
	}
	
	private void onDownPress(){
		
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ||
			Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)){
			float newZoom = mRightCamera.zoom + Constants.ZOOM_INCREMENT;
			if (newZoom > Constants.MAX_ZOOM)
				return;
			else
				mRightCamera.zoom = newZoom;
			mRightCamera.update();
		}
		
		else{
			mColorIndex = (mColorIndex + 1) % mNoteColors.length;
			mUpButton.setColor(mNoteColors[mColorIndex]);
			mDownButton.setColor(mNoteColors[mColorIndex]);		
		}
	}
	
	private void onSlotPress(NoteSlot slot){

		if (removeNote(slot, mCurrentBeat))
			return;
		NoteType type = NoteType.THIRTY_SECOND;
		switch(mCurrentBeatFraction.getDenominator()){
		case 1:
			type = NoteType.QUARTER;
			break;
		case 2:
			type = NoteType.EIGHTH;
			break;
		case 3:
			type = NoteType.TWELVTH;
			break;
		case 4:
			type = NoteType.SIXTEENTH;
			break;
		case 6:
			type = NoteType.TWENTY_FOURTH;
			break;
		case 8:
			type = NoteType.THIRTY_SECOND;
			break;
		}
		
		//Initially place a hold note, if the hold duration is 0
		//it will be changed to a normal note
		placeNewHoldNote(slot, type);
	}
	
	private void onSlotRelease(NoteSlot slot){
		
		//Replace hold notes which have duration 0 with regular notes
		ArrayList<HoldNote> toRemove = new ArrayList<HoldNote>();
		for(HoldNote note : mActiveHoldNotes){
			if (note.getHoldDuration() < 1.f / 64 && note.slot == slot){
				toRemove.add(note);
				placeNewNote(slot, note.type);
			}
		}
		mActiveHoldNotes.removeAll(toRemove);
		
		//Place rest of hold notes
		for(HoldNote note : mActiveHoldNotes){
			if (note.slot == slot){
				mRightHoldNotes.add(note);
				mBuilder.addHold(note);
			}
		}
		mActiveHoldNotes.removeAll(mRightHoldNotes);		
	}
	
	private void scrollToBeat(int beat){
		
		int maxMeasure = (int) (mData.getBpm() * (mData.getLength() / 60.f));
		if (beat > maxMeasure) beat = maxMeasure;
		if (beat < 0) beat = 0;
		float distanceBeats = mCurrentBeat - beat;
		float measureWidthPixels = mRightOutlines[0].getBounds().width * Constants.MEASURE_WIDTH_NOTES;
		mRightCamera.translate(distanceBeats * measureWidthPixels, 0.f);
		mRightCamera.update();
		for(OutlineNote note : mRightOutlines){
			note.moveBy(distanceBeats * measureWidthPixels, 0);
		}
		mCurrentBeatFraction = new Fraction(beat, 1);
		mCurrentBeat = beat;
	}
	
	private void cutNotes(){
		
		ArrayList<Note> notes = new ArrayList<Note>();
		ArrayList<HoldNote> holds = new ArrayList<HoldNote>();
		if (!mNoteSelection.isOpen() && mNoteSelection.isActive()){
			
			for (Note note : mRightRegularNotes){
				if (note.beat >= mNoteSelection.getStartBeat() && note.beat <= mNoteSelection.getEndBeat()){
					notes.add(new Note(note, new ScoreManager()));
					note.flagForRemoval();
				}
			}
			
			for (HoldNote hold : mRightHoldNotes){
				if (hold.beat >= mNoteSelection.getStartBeat() && hold.beat <= mNoteSelection.getEndBeat()){
					holds.add(new HoldNote(hold, mData.getBpm(), new ScoreManager()));
					hold.flagForRemoval();
				}
			}			
		}
		mClipboard.copy(notes,  holds);
	}
	
	private void copyNotes(){
		ArrayList<Note> notes = new ArrayList<Note>();
		ArrayList<HoldNote> holds = new ArrayList<HoldNote>();
		if (!mNoteSelection.isOpen() && mNoteSelection.isActive()){
			
			for (Note note : mRightRegularNotes){
				if (note.beat >= mNoteSelection.getStartBeat() && note.beat <= mNoteSelection.getEndBeat())
					notes.add(new Note(note, new ScoreManager()));
			}
			
			for (HoldNote hold : mRightHoldNotes){
				if (hold.beat >= mNoteSelection.getStartBeat() && hold.beat <= mNoteSelection.getEndBeat())
					holds.add(new HoldNote(hold, mData.getBpm(), new ScoreManager()));
			}			
		}
		mClipboard.copy(notes,  holds);
	}
	
	private void pasteNotes(){
		
		if (mClipboard.isEmpty()) return;
		float beatDifference = mClipboard.getFirstBeat() - mCurrentBeat;
		ArrayList<Note> notes = mClipboard.getNotes();
		ArrayList<HoldNote> holds = mClipboard.getHoldNotes();
		
		for (Note note : notes){
			float newBeat = note.beat - beatDifference;
			placeNote(newBeat, note.slot, Note.beatToType(newBeat));
		}
		for (HoldNote hold : holds){
			float newBeat = hold.beat - beatDifference;
			placeHoldNote(newBeat, hold.getHoldDuration(), hold.slot, Note.beatToType(newBeat));
	
		}
	}
}