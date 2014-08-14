package com.splitbeat.game;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
	private ArrayList<Note> mLeftNotes;
	private ArrayList<Note> mRightNotes;
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
	
	private float mCurrentBeat;
	private int mColorIndex;
	
	private ScoreManager mScoreManager;
	
	SongEditScreen(Game game){
		super(game);
	}
	
	private void init(){
		
		mBuilder = new TmxMapBuilder();
		mShapeRenderer = new ShapeRenderer();
		mBatch = new SpriteBatch();
		mRightMarkers = new ArrayList<BPMMarker>();
		mRightMarkers = new ArrayList<BPMMarker>();
		mRightNotes = new ArrayList<Note>();
		mRightNotes = new ArrayList<Note>();
		mScoreManager = new ScoreManager();
		mNoteColors = new Color[]{
				Color.RED,
				Color.BLUE,
				Color.PURPLE,
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
		float trackWidth = w - 2 * Assets.instance.gui.leftArrow.getRegionWidth() - 4 * Constants.CELL_PADDING;
		
		//Same with trackheight
		float trackHeight = Gdx.graphics.getHeight() * 0.5f;
		
		float h = Gdx.graphics.getHeight();
		mLeftCamera = new OrthographicCamera(trackWidth, trackHeight);
		mRightCamera = new OrthographicCamera(trackWidth, trackHeight);
		
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
		buildStage();
		
		placeNote(1, NoteSlot.MIDDLE_RIGHT, NoteType.QUARTER);
		placeNote(2, NoteSlot.MIDDLE_RIGHT, NoteType.EIGHTH);
		
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
				case(Keys.ENTER):
					 //TODO SOMETHING
					break;
				case(Keys.ESCAPE):
					//TODO SOMETHING
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
	
	private void buildStage(){
		
		mStage.addActor(mLeftButton);
		mStage.addActor(mRightButton);
		mStage.addActor(mUpButton);
		mStage.addActor(mDownButton);
		
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
				mRightCamera.position.y + mRightCamera.viewportHeight / 2.f,
				mRightCamera.viewportWidth / 2.f - 2 * mUpButton.getWidth() / 2.f - i * measureWidthPixels,
				mRightCamera.position.y - mRightCamera.viewportHeight / 2.f);
		}
		
		mShapeRenderer.end();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mStage.draw();	
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
		mBatch.end();
	}
	
	private void renderOutlines(){
		
		for(OutlineNote note : mRightOutlines){
			note.render(mBatch);
		}
	}
	
	private void renderNotes(){
		Gdx.gl20.glBlendFunc(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
		for(Note note : mRightNotes){
			note.render(mBatch);
		}
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}
	
	private void placeNote(float beat, NoteSlot slot, NoteType type){
		
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
		}
		
		//Move to starting position (beat 0)
		note.moveBy(mRightCamera.viewportWidth / 2.f - 2 * mUpButton.getWidth() / 2.f, 0);
		
		//Move to appropriate beat
		float measureWidthPixels = note.getBounds().width * Constants.MEASURE_WIDTH_NOTES;
		note.moveBy(beat * -measureWidthPixels, 0);
		
		mRightNotes.add(note);
	}
	
	private void onLeftPress(){
		
		float measureWidthPixels = mRightOutlines[0].getBounds().width * Constants.MEASURE_WIDTH_NOTES;
		mRightCamera.translate(-measureWidthPixels / mNoteQuantizations[mColorIndex], 0);
		mRightCamera.update();
		for(OutlineNote note : mRightOutlines){
			note.moveBy(-measureWidthPixels / mNoteQuantizations[mColorIndex], 0);
		}
	}
	
	private void onRightPress(){
		
		float measureWidthPixels = mRightOutlines[0].getBounds().width * Constants.MEASURE_WIDTH_NOTES;
		mRightCamera.translate(measureWidthPixels / mNoteQuantizations[mColorIndex], 0);
		mRightCamera.update();
		for(OutlineNote note : mRightOutlines){
			note.moveBy(measureWidthPixels / mNoteQuantizations[mColorIndex], 0);
		}
		
	}
	
	private void onUpPress(){
		
		mColorIndex = (mColorIndex + mNoteColors.length - 1) % mNoteColors.length;
		mUpButton.setColor(mNoteColors[mColorIndex]);
		mDownButton.setColor(mNoteColors[mColorIndex]);
	}
	
	private void onDownPress(){
		
		mColorIndex = (mColorIndex + 1) % mNoteColors.length;
		mUpButton.setColor(mNoteColors[mColorIndex]);
		mDownButton.setColor(mNoteColors[mColorIndex]);		
	}
}