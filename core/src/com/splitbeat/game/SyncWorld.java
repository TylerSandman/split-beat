package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.splitbeat.game.Constants.NoteSlot;

public class SyncWorld extends World {

	private float[] mNoteTimings;
	private int mTimingIndex;
	private float mNoteSpeed;
	private float mCalculatedOffset;
	
	SyncWorld(Game game) {
		super(game, "", Difficulty.Easy);
		
		//Calculate scroll speed
		float measureWidthPixels = mRightOutlines[0].getBounds().width * Constants.MEASURE_WIDTH_NOTES;
		//Pixels per measure times beats per second
		mNoteSpeed = measureWidthPixels * Constants.SYNC_BPM / 60.f;
		
		//Array containing note timings to recalculate global offset
		mNoteTimings = new float[Constants.NUM_SYNC_CALCULATION_BEATS];
		
		mTimingIndex = 0;
		mCalculatedOffset = Options.instance.getOffset();
	}
	
	@Override
	protected void initMaps(){
		mLeftMap = Assets.instance.sync.left;
		mRightMap = Assets.instance.sync.right;
	}
	
	@Override
	protected void initMusic(){
		AudioManager.instance.play(Assets.instance.music.sync);
	}
	
	@Override
	protected void onSongEnd(){
		Options.instance.setOffset(mCalculatedOffset);
	}
	
	@Override
	public void checkCollisions(){
		checkSlotCollision(NoteSlot.MIDDLE_RIGHT);
	}
	
	@Override
	public void checkSlotCollision(NoteSlot slot){
		
		//Only concerned with middle right slot
		OutlineNote outline;
		Rectangle outlineBounds;
		switch(slot){
		case MIDDLE_RIGHT:
			outline = mRightOutlines[1];
			break;
		default:
			return;
		}
		outlineBounds = outline.getBounds();	
		Rectangle noteBounds;			
		int bound = (mRightNotes.size() < Constants.NUM_SYNC_CALCULATION_BEATS) ? mRightNotes.size() : Constants.NUM_SYNC_CALCULATION_BEATS;
		
		//Check first 8 notes for collisions for efficiency
		for(int i = 0; i < bound; ++i){
			Note currentNote = mRightNotes.get(i);
			noteBounds = currentNote.getHitBounds();
			if (Intersector.overlaps(noteBounds, outlineBounds)){
				float distance = Math.abs(outline.position.x - currentNote.position.x) / mNoteSpeed * 2.f;
				mTimingToDisplay = currentNote.resolveTimingWindow(distance);
				currentNote.onPress();
				outline.onNoteHit(mTimingToDisplay);
				float offset = (outline.position.x - currentNote.position.x) / mNoteSpeed * 2.f;
				//Need consecutive notes to be close enough in timing to sync correctly
				if ((mTimingIndex == 0) ||
					(Math.abs(mNoteTimings[mTimingIndex - 1] - offset) < Constants.SYNC_BEAT_DIFFERENCE_THRESHOLD)){
					mNoteTimings[mTimingIndex] = offset;
					mTimingIndex = (mTimingIndex + 1) % Constants.NUM_SYNC_CALCULATION_BEATS;
					if (mTimingIndex == 0)
						recalculateOffset();
				}
				else
					restartSync();
			}
		}
	}
	
	private void recalculateOffset(){
		
		float offsetSum = 0.f;
		for (int i = 0; i < Constants.NUM_SYNC_CALCULATION_BEATS; ++i)
			offsetSum += mNoteTimings[i];
		mCalculatedOffset = offsetSum / Constants.NUM_SYNC_CALCULATION_BEATS;
		
		//Adjust cameras and outlines for new offset
		mLeftCamera.translate(mNoteSpeed * mCalculatedOffset, 0.f);
		mRightCamera.translate(-mNoteSpeed * mCalculatedOffset, 0.f);	
		for (OutlineNote outline : mLeftOutlines)
			outline.update(mCalculatedOffset);
		for (OutlineNote outline : mRightOutlines)
			outline.update(mCalculatedOffset);
		restartSync();
	}
	
	private void restartSync(){
		
		for (int i = 0; i < Constants.NUM_SYNC_CALCULATION_BEATS; ++i)
			mNoteTimings[i] = 0.f;
		mTimingIndex = 0;
	}
	
	@Override
	protected void renderScore(){
		String scoreStr = String.format("%.2f", mCalculatedOffset);
		Assets.instance.fonts.defaultFont.drawMultiLine(
				mBatch, scoreStr,
				0, mHUDCamera.viewportHeight / 2.f - Assets.instance.fonts.defaultFont.getLineHeight() * 2.f,
				0, BitmapFont.HAlignment.CENTER);
	}
}
