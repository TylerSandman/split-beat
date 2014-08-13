package com.splitbeat.game;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
	
	private ArrayList<BPMMarker> mLeftMarkers;
	private ArrayList<BPMMarker> mRightMarkers;
	private ArrayList<Note> mLeftNotes;
	private ArrayList<Note> mRightNotes;
	private OutlineNote[] mLeftOutlines;
	private OutlineNote[] mRightOutlines;
	
	private Stage mStage;
	private Skin mSkin;
	private ShapeRenderer mShapeRenderer;
	SpriteBatch mBatch;
	
	private Image mLeftButton;
	private Image mRightButton;
	private Image mUpButton;
	private Image mDownButton;
	
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
		mLeftNotes = new ArrayList<Note>();
		mRightNotes = new ArrayList<Note>();
		mScoreManager = new ScoreManager();
		
		mSkin = new Skin(
				Gdx.files.internal(Constants.GUI_SKIN),
				new TextureAtlas(Constants.TEXTURE_ATLAS_GUI));
		
		//Dummy data for now
		SongData data = new SongData("Test Map");
		data.setArtist("Tyler.S");
		data.setBpm(170.f);
		data.setTitle("Test Map");
		data.setOffset(-0.04f);
		data.setLength(220);
		mBuilder.create(data);
		
		//Configure cameras
		float w = Gdx.graphics.getWidth();
		
		//Trackwidth is as large as possible leaving room for GUI + padding
		float trackWidth = w - 2 * Assets.instance.gui.leftArrow.getRegionWidth() - 4 * Constants.CELL_PADDING;
		
		//Same with trackheight
		float trackHeight = Gdx.graphics.getHeight() * 0.5f;
		
		float h = Gdx.graphics.getHeight();
		mRightCamera = new OrthographicCamera(trackWidth, trackHeight);
		mLeftCamera = new OrthographicCamera(trackWidth, trackHeight);
		
		//Move cameras to bottom of screen
		float camTranslate = -Gdx.graphics.getHeight() / 2.f + mLeftCamera.viewportHeight / 2.f;
		mRightCamera.translate(0, camTranslate);		
		mLeftCamera.translate(0, camTranslate);
		
		//Move up to make room for GUI + padding
		float camPadTranslate = Assets.instance.gui.downArrow.getRegionHeight() + 2 * Constants.CELL_PADDING;	
		mRightCamera.translate(0, camPadTranslate);
		mLeftCamera.translate(0, camPadTranslate);
		
		mHUDCamera = new OrthographicCamera(w, h);
		mHUDCamera.position.set(0, 0, 0);
		mHUDCamera.update();
		mLeftCamera.update();
		mRightCamera.update();
		
		buildButtons();
		buildStage();
		
		mStage.addListener(new InputListener(){
			
			@Override
			public boolean keyDown(InputEvent event, int keycode){
				int newIndex;
				Difficulty newDifficulty;
				switch(keycode){
				case(Keys.DOWN):
					//TODO SWITCH NOTE DOWN
					break;
				case(Keys.UP):
					//TODO SWITCH NOTE UP
					break;
				case(Keys.LEFT):
					//TODO MOVE TRACK LEFT
					break;
				case(Keys.RIGHT):
					//TODO MOVE TRACK RIGHT
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
		
		mLeftOutlines = new OutlineNote[]{
				new OutlineNote(NoteSlot.TOP_LEFT, mScoreManager),
				new OutlineNote(NoteSlot.MIDDLE_LEFT, mScoreManager),
				new OutlineNote(NoteSlot.BOTTOM_LEFT, mScoreManager)
		};
		
		//Center outlines relative to track and up/down buttons
		for(OutlineNote note : mLeftOutlines){
			note.setPosition(
					mLeftCamera.position.x - note.getBounds().width / 2.f,
					mLeftCamera.position.y - note.getBounds().height / 2.f);
		}
		
		//Move to appropriate slots
		float moveIncrement = 2 * mLeftCamera.viewportHeight / 7;
		mLeftOutlines[0].moveBy(0, -moveIncrement);
		mLeftOutlines[2].moveBy(0, moveIncrement);
		
		//Move to starting position (beat 0)
		for(OutlineNote note : mLeftOutlines){
			note.moveBy(mLeftCamera.viewportWidth / 2.f - 2 * mUpButton.getWidth() / 2.f, 0);
		}
	}
	
	private void buildButtons(){
		
		mLeftButton = new Image(Assets.instance.gui.leftArrow);
		mRightButton = new Image(Assets.instance.gui.rightArrow);
		mUpButton = new Image(Assets.instance.gui.upArrow);
		mDownButton = new Image(Assets.instance.gui.downArrow);
		
		mLeftButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				//TODO MOVE TRACK LEFT
				return true;
			}
		});
		
		mRightButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				//TODO MOVE TRACK RIGHT
				return true;
			}
		});
		
		mUpButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				//TODO MOVE TRACK UP
				return true;
			}
		});
		
		mDownButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)  {
				//TODO MOVE TRACK DOWN
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
		mLeftButton.moveBy(-mLeftButton.getWidth() / 2.f, -mLeftButton.getHeight() / 2.f);
		mRightButton.moveBy(-mRightButton.getWidth() / 2.f, -mRightButton.getHeight() / 2.f);
		mUpButton.moveBy(-mUpButton.getWidth() / 2.f, -mUpButton.getHeight() / 2.f);
		mDownButton.moveBy(-mDownButton.getWidth() / 2.f, -mDownButton.getHeight() / 2.f);
		
		//Move buttons to base position		
		mLeftButton.moveBy(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		mRightButton.moveBy(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		mUpButton.moveBy(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		mDownButton.moveBy(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		
		//Translate to align with track
		mLeftButton.moveBy(0, mLeftCamera.position.y);
		mRightButton.moveBy(0, mLeftCamera.position.y);
		mUpButton.moveBy(0, mLeftCamera.position.y);
		mDownButton.moveBy(0, mLeftCamera.position.y);
		
		//Move relative to track
		mLeftButton.moveBy(
				-mLeftCamera.viewportWidth / 2 - mLeftButton.getWidth() / 2.f - Constants.CELL_PADDING,
				0);
		mRightButton.moveBy(
				mLeftCamera.viewportWidth / 2 + mRightButton.getWidth() /2.f + Constants.CELL_PADDING,
				0);
		mUpButton.moveBy(
				mLeftCamera.viewportWidth / 2 - mUpButton.getWidth(),
				mLeftCamera.viewportHeight / 2 + mUpButton.getHeight() / 2 + Constants.CELL_PADDING);
		mDownButton.moveBy(
				mLeftCamera.viewportWidth / 2 - mDownButton.getWidth(),
				-mLeftCamera.viewportHeight / 2 - mDownButton.getHeight() / 2 - Constants.CELL_PADDING);
		
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
		
		for(Note note : mLeftNotes){
			note.update(delta);
		}
	}

	@Override
	public void render(float delta) {
		
		mStage.act(delta);
		update(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mShapeRenderer.begin(ShapeType.Filled);
		mShapeRenderer.setColor(0, 1, 0, 1);
		mShapeRenderer.rect(
				Gdx.graphics.getWidth() / 2.f - mLeftCamera.viewportWidth / 2.f,
				Gdx.graphics.getHeight() / 2.f - mLeftCamera.viewportHeight / 2.f + mLeftCamera.position.y,
				mLeftCamera.viewportWidth,
				mLeftCamera.viewportHeight);
		mShapeRenderer.end();
		mStage.draw();	
		renderTrack();
	}
	
	private void renderTrack(){

		mBatch.setProjectionMatrix(mLeftCamera.combined);
		Gdx.gl.glViewport(
				(int)(Gdx.graphics.getWidth() / 2 - mLeftCamera.viewportWidth / 2), 
				(int)(Gdx.graphics.getHeight() / 2 + mLeftCamera.position.y - mLeftCamera.viewportHeight / 2), 
				(int)mLeftCamera.viewportWidth, 
				(int)mLeftCamera.viewportHeight);
		mBatch.begin();
		renderOutlines();
		renderNotes();
		mBatch.end();
	}
	
	private void renderOutlines(){
		
		for(OutlineNote note : mLeftOutlines){
			note.render(mBatch);
		}
	}
	
	private void renderNotes(){
		
		for(Note note : mLeftNotes){
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
				mLeftCamera.position.x - note.getBounds().width / 2.f,
				mLeftCamera.position.y - note.getBounds().height / 2.f);
		
		//Move to appropriate slot
		float moveIncrement = 2 * mLeftCamera.viewportHeight / 7;
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
		note.moveBy(mLeftCamera.viewportWidth / 2.f - 2 * mUpButton.getWidth() / 2.f, 0);

		mLeftNotes.add(note);
	}
	
	

}