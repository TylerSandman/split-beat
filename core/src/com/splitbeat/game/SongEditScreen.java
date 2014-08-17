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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.splitbeat.game.Constants.NoteSlot;
import com.splitbeat.game.Constants.NoteType;
public class SongEditScreen extends AbstractGameScreen {
	
	private TmxMapBuilder mBuilder;
	private OrthographicCamera mLeftCamera;
	private OrthographicCamera mRightCamera;
	private OrthographicCamera mHUDCamera;
	private SongData mData;
	
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
	
	private Stage mStage;
	private Skin mSkin;
	private ShapeRenderer mShapeRenderer;
	SpriteBatch mBatch;
	
	private Image mLeftButton;
	private Image mRightButton;
	private Image mUpButton;
	private Image mDownButton;
	
	private Table mInformationTable;
	private Label mCurrentBeatLabel;
	private Label mCurrentSecondLabel;
	private Label mSnapToLabel;
	private Label mLengthLabel;
	
	private Fraction mCurrentBeatFraction;
	private float mCurrentBeat;
	private float mCurrentSecond;
	private int mColorIndex;
	private float mTrackWidth;
	private float mTrackHeight;
	private boolean mPlacingHold;
	
	private ScoreManager mScoreManager;
	
	SongEditScreen(Game game){
		super(game);
	}
	
	private void init(){
		
		mBuilder = new TmxMapBuilder();
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
		mPlacingHold = false;
		
		mSkin = new Skin(
				Gdx.files.internal(Constants.GUI_SKIN),
				new TextureAtlas(Constants.TEXTURE_ATLAS_GUI));
		
		//Dummy data for now
		mData = new SongData("Test Map");
		mData.setArtist("Tyler.S");
		mData.setBpm(170.f);
		mData.setTitle("Test Map");
		mData.setOffset(-0.04f);
		mData.setLength(220);
		mBuilder.create(mData);
		
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
		
		buildButtons();
		buildInformation();
		buildStage();
		
		mStage.addListener(new InputListener(){
			
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
		});
		
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
	
	private void buildStage(){
		
		mStage.addActor(mLeftButton);
		mStage.addActor(mRightButton);
		mStage.addActor(mUpButton);
		mStage.addActor(mDownButton);		
		mStage.addActor(mInformationTable);
		
		
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
			if (note.isFlaggedForRemoval())
				toRemove.add(note);
		}
		for(Note note : mRightHoldNotes){
			if (note.isFlaggedForRemoval())
				toRemove.add(note);
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
		
		mShapeRenderer.begin(ShapeType.Filled);
		mShapeRenderer.setColor(0, 1, 0, 1);
		mShapeRenderer.end();
		
		mShapeRenderer.begin(ShapeType.Line);
		mShapeRenderer.setColor(Color.WHITE);
		float measureWidthPixels = mRightOutlines[0].getBounds().width * Constants.MEASURE_WIDTH_NOTES;
		for (int i = 0; i < (int)(mData.getBpm() * (mData.getLength() / 60.f)); ++i){
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
	
	private void placeNote(NoteSlot slot, NoteType type){
		
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
	}
	
	private void placeHoldNote(NoteSlot slot, NoteType type){
		
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
		
		float measureWidthPixels = mRightOutlines[0].getBounds().width * Constants.MEASURE_WIDTH_NOTES;
		mRightCamera.translate(-measureWidthPixels / mNoteQuantizations[mColorIndex], 0);
		mRightCamera.update();
		mCurrentBeatFraction = mCurrentBeatFraction.plus(1, mNoteQuantizations[mColorIndex]);
		mCurrentBeat = mCurrentBeatFraction.toFloat();
		if (mCurrentBeat == 0.f)
			mCurrentSecond = 0.f;
		else
			mCurrentSecond = mCurrentBeat / (mData.getBpm() / 60.f);
		for(OutlineNote note : mRightOutlines){
			note.moveBy(-measureWidthPixels / mNoteQuantizations[mColorIndex], 0);
		}
		
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
		placeHoldNote(slot, type);
	}
	
	private void onSlotRelease(NoteSlot slot){
		
		//Replace hold notes which have duration 0 with regular notes
		ArrayList<HoldNote> toRemove = new ArrayList<HoldNote>();
		for(HoldNote note : mActiveHoldNotes){
			if (note.getHoldDuration() < 1.f / 64 && note.slot == slot){
				toRemove.add(note);
				placeNote(slot, note.type);
			}
		}
		mActiveHoldNotes.removeAll(toRemove);
		
		//Place rest of hold notes
		for(HoldNote note : mActiveHoldNotes){
			mRightHoldNotes.add(note);
		}
		mActiveHoldNotes.removeAll(mRightHoldNotes);		
	}
}