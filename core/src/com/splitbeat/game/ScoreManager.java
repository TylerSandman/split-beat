package com.splitbeat.game;

import java.util.ArrayList;

import com.splitbeat.game.Constants.Timing;

public class ScoreManager {
	
	private int mScore;
	private int mMaxScore;
	private int mNumNotes;
	private int mMisses;
	private int mGoods;
	private int mGreats;
	private int mAmazings;
	private int mFlawlesses;
	private int mHolds;
	private int mDrops;
	private int mCombo;
	private int mMaxCombo;
	
	ScoreManager(){
		mScore = 0;
		mMaxScore = 0;
		mMisses = 0;
		mGoods = 0;
		mGreats = 0;
		mAmazings = 0;
		mFlawlesses = 0;
		mHolds = 0;
		mDrops = 0;
		mNumNotes = 0;
		mCombo = 0;
		mMaxCombo = 0;
	}
	
	public float getPercentageScore(){
		if (mMaxScore == 0 || mScore < 0) 
			return 0.f;
		return mScore / (float) mMaxScore;
	}
	
	public void setMaxScore(ArrayList<Note> notes){
		mMaxScore = 0;
		for (Note note : notes){
			mMaxScore += note.getMaxScore();
		}
		mNumNotes = notes.size();
	}
	
	public void processTiming(Timing timing){
		switch(timing){
		case MISS:
			mScore += Constants.MISS_POINTS;
			mMisses++;
			mCombo = 0;
			break;
		case GOOD:
			mScore += Constants.GOOD_POINTS;
			mGoods++;
			mCombo = 0;
			break;
		case GREAT:
			mScore += Constants.GREAT_POINTS;
			mGreats++;
			mCombo++;
			break;
		case AMAZING:
			mScore += Constants.AMAZING_POINTS;
			mAmazings++;
			mCombo++;
			break;
		case FLAWLESS:
			mScore += Constants.FLAWLESS_POINTS;
			mFlawlesses++;
			mCombo++;
			break;
		case HOLD:
			mScore += Constants.HOLD_POINTS;
			mHolds++;
			break;
		case DROP:
			mScore += Constants.DROP_POINTS;
			mDrops++;
			break;
		default:
			break;			
		}
		if (mCombo > mMaxCombo)
			mMaxCombo = mCombo;
	}
	
	public int getCurrentCombo(){
		return mCombo;
	}
	
	public int getMaxCombo(){
		return mMaxCombo;
	}
}
