package com.splitbeat.game;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.splitbeat.game.Constants.NoteSlot;
import com.splitbeat.game.Constants.NoteType;
import com.splitbeat.game.Constants.Timing;

public class World implements Disposable{
	
	private OrthographicCamera mLeftCamera;
	private OrthographicCamera mRightCamera;
	private OrthographicCamera mHUDCamera;
	private SpriteBatch mBatch;
	private PlayerController mController;
	private ScoreManager mScoreManager;
	private TiledMap mLeftMap;
	private TiledMap mRightMap;
	private ArrayList<BPMMarker> mLeftMarkers;
	private ArrayList<BPMMarker> mRightMarkers;
	private ArrayList<Note> mLeftNotes;
	private ArrayList<Note> mRightNotes;
	private OutlineNote[] mLeftOutlines;
	private OutlineNote[] mRightOutlines;
	private Timing mTimingToDisplay;
	
	private float mBPM;
	private float mMeasureWidthPixels;
	private float mNoteSpeed;		
	private float mOffset;
	private float mSecondsSincePlay;
	private boolean mResetRequested;
	
	World(){
		init();
	}
	
	private void init(){
		
		mController = new PlayerController(this);
		mTimingToDisplay = Timing.NONE;
		mBatch = new SpriteBatch();
		mScoreManager = new ScoreManager();
		mResetRequested = false;
		mSecondsSincePlay = 0.f;
		
		//Configure cameras
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		mRightCamera = new OrthographicCamera(w / 2, h);
		mLeftCamera = new OrthographicCamera(w / 2, h);
		mHUDCamera = new OrthographicCamera(w, h);
		mHUDCamera.position.set(0, 0, 0);
		mHUDCamera.update();
		
		//Parse Song information
		mLeftMap = Assets.instance.maps.left;
		mRightMap = Assets.instance.maps.right;
		String bpmStr= mLeftMap.getProperties().get("bpm", String.class);
		mBPM = (float) Double.parseDouble(bpmStr);
		String offsetStr = mLeftMap.getProperties().get("offset", String.class);
		mOffset = (float) Double.parseDouble(offsetStr);
		mOffset += Constants.GLOBAL_OFFSET;
		
		//Parse tracks
		mLeftNotes = new ArrayList<Note>();
		MapLayer noteLayer =  mLeftMap.getLayers().get(0);
		MapObjects notes = noteLayer.getObjects();
		
		for(MapObject note : notes)		
			mLeftNotes.add(NoteFactory.createNote(note, mLeftMap, mScoreManager, true));
		
		mRightNotes = new ArrayList<Note>();
		noteLayer = mRightMap.getLayers().get(0);
		notes = noteLayer.getObjects();
		
		for(MapObject note : notes)
			mRightNotes.add(NoteFactory.createNote(note, mRightMap, mScoreManager, false));
		
		ArrayList<Note> allNotes = new ArrayList<Note>();
		allNotes.addAll(mLeftNotes);
		allNotes.addAll(mRightNotes);
		mScoreManager.setMaxScore(allNotes);
			
		//Sort by beat. Mostly used for efficient collision checking by
		//iterating through the first few beats, rather than all of the notes
		Collections.sort(mLeftNotes, new NoteComparator());
		Collections.sort(mRightNotes, new NoteComparator());
		
		//Parse BPM markers
		mLeftMarkers = new ArrayList<BPMMarker>();
		if (mLeftMap.getLayers().getCount() > 1){
			MapLayer markerLayer = mLeftMap.getLayers().get(1);
			MapObjects markers = markerLayer.getObjects();
			
			for(MapObject marker : markers){
				mLeftMarkers.add(MarkerFactory.createMarker(marker, mLeftMap));
			}
		}
		
		mRightMarkers = new ArrayList<BPMMarker>();
		MapLayers layers = mRightMap.getLayers();
		int count = layers.getCount();
		if (mRightMap.getLayers().getCount() > 1){
			MapLayer markerLayer = mRightMap.getLayers().get(1);
			MapObjects markers = markerLayer.getObjects();
			
			for(MapObject marker : markers){
				mRightMarkers.add(MarkerFactory.createMarker(marker, mRightMap));
			}
		}
		
		//Create note outlines for hit detection
		mLeftOutlines = new OutlineNote[]{
				new OutlineNote(NoteSlot.TOP_LEFT, mScoreManager),
				new OutlineNote(NoteSlot.MIDDLE_LEFT, mScoreManager),
				new OutlineNote(NoteSlot.BOTTOM_LEFT, mScoreManager)
		};
		mRightOutlines = new OutlineNote[]{
				new OutlineNote(NoteSlot.TOP_RIGHT, mScoreManager),
				new OutlineNote(NoteSlot.MIDDLE_RIGHT, mScoreManager),
				new OutlineNote(NoteSlot.BOTTOM_RIGHT, mScoreManager)
		};
		
		//Calculate scroll speed
		mMeasureWidthPixels = mRightOutlines[0].getBounds().width * Constants.MEASURE_WIDTH_NOTES;
		//Pixels per measure times measures per second
		mNoteSpeed = mMeasureWidthPixels * (mBPM / 4.f) / 60.f;
		for(Note outline : mRightOutlines)
			outline.velocity.x = -mNoteSpeed;
		for(Note outline : mLeftOutlines)
			outline.velocity.x = mNoteSpeed;
		
		//Adjust camera for song offset
		mRightCamera.translate(-mNoteSpeed * mOffset, 0);
		mRightCamera.update();
		mLeftCamera.translate(mNoteSpeed * mOffset, 0);
		mLeftCamera.update();
		for(OutlineNote outline : mLeftOutlines)
			outline.update(mOffset);
		for(OutlineNote outline : mRightOutlines)
			outline.update(mOffset);
			
		AudioManager.instance.play(Assets.instance.music.paperPlanes);
	}
	
	public void requestReset(){
		mResetRequested = true;
	}
	
	private void reset(){
		
		AudioManager.instance.stopMusic();
		mTimingToDisplay = Timing.NONE;
		mScoreManager = new ScoreManager();
		mResetRequested = false;
		mSecondsSincePlay = 0.f;
		
		//Reset cameras
		mLeftCamera.position.set(0, 0, 0);
		mRightCamera.position.set(0, 0, 0);
		mHUDCamera.position.set(0, 0, 0);
		mLeftCamera.update();
		mRightCamera.update();
		mHUDCamera.update();
		
		//Parse tracks
		mLeftNotes = new ArrayList<Note>();
		MapLayer noteLayer =  mLeftMap.getLayers().get(0);
		MapObjects notes = noteLayer.getObjects();
		
		for(MapObject note : notes)		
			mLeftNotes.add(NoteFactory.createNote(note, mLeftMap, mScoreManager, true));
		
		mRightNotes = new ArrayList<Note>();
		noteLayer = mRightMap.getLayers().get(0);
		notes = noteLayer.getObjects();
		
		for(MapObject note : notes)
			mRightNotes.add(NoteFactory.createNote(note, mRightMap, mScoreManager, false));
		
		ArrayList<Note> allNotes = new ArrayList<Note>();
		allNotes.addAll(mLeftNotes);
		allNotes.addAll(mRightNotes);
		mScoreManager.setMaxScore(allNotes);
			
		//Sort by beat. Mostly used for efficient collision checking by
		//iterating through the first few beats, rather than all of the notes
		Collections.sort(mLeftNotes, new NoteComparator());
		Collections.sort(mRightNotes, new NoteComparator());
		
		//Parse BPM markers
		mLeftMarkers = new ArrayList<BPMMarker>();
		if (mLeftMap.getLayers().getCount() > 1){
			MapLayer markerLayer = mLeftMap.getLayers().get(1);
			MapObjects markers = markerLayer.getObjects();
			
			for(MapObject marker : markers){
				mLeftMarkers.add(MarkerFactory.createMarker(marker, mLeftMap));
			}
		}
		
		mRightMarkers = new ArrayList<BPMMarker>();
		MapLayers layers = mRightMap.getLayers();
		int count = layers.getCount();
		if (mRightMap.getLayers().getCount() > 1){
			MapLayer markerLayer = mRightMap.getLayers().get(1);
			MapObjects markers = markerLayer.getObjects();
			
			for(MapObject marker : markers){
				mRightMarkers.add(MarkerFactory.createMarker(marker, mRightMap));
			}
		}
		
		//Create note outlines for hit detection
		mLeftOutlines = new OutlineNote[]{
				new OutlineNote(NoteSlot.TOP_LEFT, mScoreManager),
				new OutlineNote(NoteSlot.MIDDLE_LEFT, mScoreManager),
				new OutlineNote(NoteSlot.BOTTOM_LEFT, mScoreManager)
		};
		mRightOutlines = new OutlineNote[]{
				new OutlineNote(NoteSlot.TOP_RIGHT, mScoreManager),
				new OutlineNote(NoteSlot.MIDDLE_RIGHT, mScoreManager),
				new OutlineNote(NoteSlot.BOTTOM_RIGHT, mScoreManager)
		};

		for(Note outline : mRightOutlines)
			outline.velocity.x = -mNoteSpeed;
		for(Note outline : mLeftOutlines)
			outline.velocity.x = mNoteSpeed;
		
		float songOffset = mOffset - Constants.GLOBAL_OFFSET;
		float netOffset = mOffset + songOffset;
		//Adjust camera for song offset
		mRightCamera.translate(-mNoteSpeed * netOffset, 0);
		mRightCamera.update();
		mLeftCamera.translate(mNoteSpeed * netOffset, 0);
		mLeftCamera.update();
		for(OutlineNote outline : mLeftOutlines)
			outline.update(netOffset);
		for(OutlineNote outline : mRightOutlines)
			outline.update(netOffset);
				
		AudioManager.instance.play(Assets.instance.music.paperPlanes);
	}
	
	public void update(float deltaTime){

		mRightCamera.translate(-mNoteSpeed * deltaTime, 0);
		mRightCamera.update();
		mLeftCamera.translate(mNoteSpeed * deltaTime, 0);
		mLeftCamera.update();
		for(OutlineNote outline : mLeftOutlines)
			outline.update(deltaTime);
		for(OutlineNote outline : mRightOutlines)
			outline.update(deltaTime);
		for(Note note : mLeftNotes)
			note.update(deltaTime);
		for(Note note : mRightNotes)
			note.update(deltaTime);
		handleInput(deltaTime);
		cleanUpObjects();	
		if (mResetRequested){
			AudioManager.instance.stopMusic();
			reset();
		}
	}
	
	public void handleInput(float deltaTime){
		mController.handleDebugInput(deltaTime);
	}
	
	public void render(){
		
		update(Gdx.graphics.getDeltaTime());
		renderWorld();
		renderHUD();
	}
	
	private void renderWorld(){
		
		renderLeftWorld();
		renderRightWorld();
	}
	
	private void renderLeftWorld(){
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth () / 2, Gdx.graphics.getHeight());
		mBatch.setProjectionMatrix(mLeftCamera.combined);
		mBatch.begin();
		renderLeftOutlines();
		renderLeftNotes();
		mBatch.end();
	}
	
	private void renderRightWorld(){
		
		Gdx.gl.glViewport(Gdx.graphics.getWidth () / 2, 0, Gdx.graphics.getWidth () / 2, Gdx.graphics.getHeight());
		mBatch.setProjectionMatrix(mRightCamera.combined);
		mBatch.begin();
		renderRightOutlines();
		renderRightNotes();
		mBatch.end();
	}
	
	private void renderLeftNotes(){
		
		float cameraBorder = mLeftCamera.position.x + mLeftCamera.viewportWidth / 2 + mMeasureWidthPixels;
		for(Note note : mLeftNotes){
			//Only render visible notes
			if(note.getHitBounds().x <= cameraBorder)
				note.render(mBatch);
		}
	}
	
	private void renderRightNotes(){
		
		float cameraBorder = mRightCamera.position.x - mRightCamera.viewportWidth / 2 - mMeasureWidthPixels;
		for(Note note : mRightNotes){
			//Only render visible notes
			if(note.getHitBounds().x >= cameraBorder)
				note.render(mBatch);
		}
	}
	
	private void renderLeftOutlines(){
		for(OutlineNote outline : mLeftOutlines)
			outline.render(mBatch);	
	}
	
	private void renderRightOutlines(){
		for(OutlineNote outline : mRightOutlines)
			outline.render(mBatch);	
	}
	
	private void renderHUD(){
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mBatch.setProjectionMatrix(mHUDCamera.combined);
		mBatch.begin();
		renderTiming();
		renderCombo();
		renderScore();
		mBatch.end();
	}
	
	private void renderTiming(){
		
		String timingStr = "";
		if (mTimingToDisplay == Timing.MISS)
			timingStr = "MISS";
		
		if (mTimingToDisplay == Timing.GOOD)
			timingStr = "GOOD";
		
		else if (mTimingToDisplay == Timing.GREAT)
			timingStr = "GREAT";
		
		else if (mTimingToDisplay == Timing.AMAZING)
			timingStr = "AMAZING";
		
		else if (mTimingToDisplay == Timing.FLAWLESS)
			timingStr = "FLAWLESS";			
		
		Assets.instance.fonts.defaultFont.drawMultiLine(
				mBatch, timingStr, 
				0, 0, 
				0, BitmapFont.HAlignment.CENTER);
	}
	
	private void renderCombo(){
		int combo = mScoreManager.getCurrentCombo();
		if (combo < 5) return;
		String comboStr = Integer.toString(combo);
		Assets.instance.fonts.defaultFont.drawMultiLine(
				mBatch, comboStr,
				0, -Assets.instance.fonts.defaultFont.getLineHeight() * 2.f,
				0, BitmapFont.HAlignment.CENTER);
	}
	
	private void renderScore(){
		
		float percentScore = mScoreManager.getPercentageScore();
		String scoreStr = String.format("%.2f", percentScore);
		Assets.instance.fonts.defaultFont.drawMultiLine(
				mBatch, scoreStr,
				0, mHUDCamera.viewportHeight / 2.f - Assets.instance.fonts.defaultFont.getLineHeight() * 2.f,
				0, BitmapFont.HAlignment.CENTER);
	}
	
	public void resize(int width, int height){}
	
	public void pressSlot(NoteSlot slot){
		OutlineNote outline;
		switch(slot){
		case TOP_LEFT:
			outline = mLeftOutlines[0];
			break;
		case MIDDLE_LEFT:
			outline = mLeftOutlines[1];
			break;
		case BOTTOM_LEFT:
			outline = mLeftOutlines[2];
			break;
		case TOP_RIGHT:
			outline = mRightOutlines[0];
			break;
		case MIDDLE_RIGHT:
			outline = mRightOutlines[1];
			break;
		case BOTTOM_RIGHT:
			outline = mRightOutlines[2];
			break;
		default:
			outline = mLeftOutlines[0];
			break;
		}
		outline.onPress();
		checkSlotCollision(slot);
	}
	
	public void checkCollisions(){
		checkSlotCollision(NoteSlot.TOP_LEFT);
		checkSlotCollision(NoteSlot.MIDDLE_LEFT);
		checkSlotCollision(NoteSlot.BOTTOM_LEFT);
		checkSlotCollision(NoteSlot.TOP_RIGHT);
		checkSlotCollision(NoteSlot.MIDDLE_RIGHT);
		checkSlotCollision(NoteSlot.BOTTOM_RIGHT);
	}
	
	public void checkSlotCollision(NoteSlot slot){
		
		OutlineNote outline;
		Rectangle outlineBounds;
		boolean checkLeft = false;
		switch(slot){
		case TOP_LEFT:
			outline = mLeftOutlines[0];
			checkLeft = true;
			break;
		case MIDDLE_LEFT:
			outline = mLeftOutlines[1];
			checkLeft = true;
			break;
		case BOTTOM_LEFT:
			outline = mLeftOutlines[2];
			checkLeft = true;
			break;
		case TOP_RIGHT:
			outline = mRightOutlines[0];
			break;
		case MIDDLE_RIGHT:
			outline = mRightOutlines[1];
			break;
		case BOTTOM_RIGHT:
			outline = mRightOutlines[2];
			break;
		default:
			outline = mLeftOutlines[0];
			break;
		}
		outlineBounds = outline.getBounds();
		
		Rectangle noteBounds;
		
		//Check left collisions
		if (checkLeft){
			int bound = (mLeftNotes.size() < 8) ? mLeftNotes.size() : 8;
			
			//Check first 8 notes for collisions for efficiency
			for(int i = 0; i < bound; ++i){
				Note currentNote = mLeftNotes.get(i);
				noteBounds = currentNote.getHitBounds();
				if (Intersector.overlaps(noteBounds, outlineBounds)){
					float distance = Math.abs(outline.position.x - currentNote.position.x) / mNoteSpeed;
					mTimingToDisplay = currentNote.resolveTimingWindow(distance);
					mScoreManager.processTiming(mTimingToDisplay);
					currentNote.onPress();
					outline.onNoteHit(mTimingToDisplay);
				}
			}
		}
		
		//Check right collisions
		else{			
			int bound = (mRightNotes.size() < 8) ? mRightNotes.size() : 8;
			
			//Check first 8 notes for collisions for efficiency
			for(int i = 0; i < bound; ++i){
				Note currentNote = mRightNotes.get(i);
				noteBounds = currentNote.getHitBounds();
				if (Intersector.overlaps(noteBounds, outlineBounds)){
					float distance = Math.abs(outline.position.x - currentNote.position.x) / mNoteSpeed;
					mTimingToDisplay = currentNote.resolveTimingWindow(distance);
					mScoreManager.processTiming(mTimingToDisplay);
					currentNote.onPress();
					outline.onNoteHit(mTimingToDisplay);
				}
			}
		}
	}
	
	private void cleanUpObjects(){
		
		cleanUpLeftObjects();
		cleanUpRightObjects();
	}
	
	private void cleanUpLeftObjects(){
		
		ArrayList<Note> toRemove = new ArrayList<Note>();
		int bound = (mLeftNotes.size() < 16) ? mLeftNotes.size() : 16;
		
		//Check first 16 notes for efficiency
		for(int i = 0; i < bound; ++i){
			
			Note currentNote = mLeftNotes.get(i);
			if (currentNote.isFlaggedForRemoval())	
				toRemove.add(currentNote);
			
			//Trigger out of bounds event
			else if (currentNote.getHitBounds().x < mLeftCamera.position.x - mLeftCamera.viewportWidth / 2){				
				if (!currentNote.pressed){
					mTimingToDisplay = Timing.MISS;
					mScoreManager.processTiming(Timing.MISS);
					currentNote.onMiss();
				}
			}
			
			if (currentNote.getBounds().x < mLeftCamera.position.x - mLeftCamera.viewportWidth / 2.f)
				currentNote.onOutOfBounds();
		}
		mLeftNotes.removeAll(toRemove);
	}
	
	private void cleanUpRightObjects(){
		
		ArrayList<Note> toRemove = new ArrayList<Note>();
		int bound = (mRightNotes.size() < 16) ? mRightNotes.size() : 16;
		
		//Check first 16 notes for efficiency
		for(int i = 0; i < bound; ++i){
			
			Note currentNote = mRightNotes.get(i);
			if (currentNote.isFlaggedForRemoval())	
				toRemove.add(currentNote);
			
			//Trigger out of bounds event
			else if (currentNote.getHitBounds().x > mRightCamera.position.x + mRightCamera.viewportWidth / 2){				
				if (!currentNote.pressed){
					mTimingToDisplay = Timing.MISS;
					mScoreManager.processTiming(Timing.MISS);
					currentNote.onMiss();
				}
			}
			
			if (currentNote.getBounds().x > mRightCamera.position.x + mRightCamera.viewportWidth / 2.f)
				currentNote.onOutOfBounds();
		}
		mRightNotes.removeAll(toRemove);
	}
	
	

	@Override
	public void dispose() {
		AudioManager.instance.stopMusic();
		mBatch.dispose();		
	}
}
