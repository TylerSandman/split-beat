package com.splitbeat.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.splitbeat.game.Constants.NoteSlot;
import com.splitbeat.game.Constants.NoteType;
import com.splitbeat.game.Constants.Timing;

public class Note extends AbstractGameObject{

	public float beat;
	public NoteType type;
	public NoteSlot slot;	
	public boolean pressed;
	protected ScoreManager mScoreManager;
	protected TextureRegion mTextureRegion;
	private boolean mLeftTrack;
	private Timing mTiming;
	
	//Slot 0 is top, slot 1 is middle, slot 2 is bottom
	Note(float beat, NoteSlot slot, NoteType type, ScoreManager scoreManager){
		super(null);
		this.beat = beat;
		this.slot = slot;
		this.type = type;
		mScoreManager = scoreManager;
		mTiming = Timing.NONE;
		pressed = false;
		mLeftTrack = false;	
		
		switch(type){
		case QUARTER:
			mTextureRegion = Assets.instance.button.buttonRed;
			break;
		case EIGHTH:
			mTextureRegion = Assets.instance.button.buttonBlue;
			break;
		case TWELVTH:
		case TWENTY_FOURTH:
			mTextureRegion = Assets.instance.button.buttonYellow;
			break;
		case SIXTEENTH:
			mTextureRegion = Assets.instance.button.buttonGreen;
			break;
		case THIRTY_SECOND:
			mTextureRegion = Assets.instance.button.buttonOrange;
			break;
		case OUTLINE:
			mTextureRegion = Assets.instance.button.buttonGrey;
			break;
		default:
			mTextureRegion = Assets.instance.button.buttonRed;
			break;
		}
		
		//Center the sprite's origin
		mSprite = new Sprite(mTextureRegion);
		mSprite.setOriginCenter();
		
		//Move it to the appropriate slot
		float moveIncrement = 2 * Gdx.graphics.getHeight() / 7;
		switch(slot){
		case TOP_RIGHT:
			mSprite.translate(0, moveIncrement);
			break;
		case MIDDLE_RIGHT:
			break;
		case BOTTOM_RIGHT:
			mSprite.translate(0, -moveIncrement);
			break;
		case TOP_LEFT:
			mSprite.translate(0, moveIncrement);
			mLeftTrack = true;
			break;
		case MIDDLE_LEFT:
			mLeftTrack = true;
			break;
		case BOTTOM_LEFT:
			mSprite.translate(0, -moveIncrement);
			mLeftTrack = true;
			break;
		}
		
		if (mLeftTrack)
			mSprite.flip(true, false);
		
		//Set the sprite to the base position (at beat 0)
		float buttonFrameX;
		if (mLeftTrack)
			buttonFrameX = mSprite.getWidth() / 2 - Gdx.graphics.getWidth() / 4;
		else
			buttonFrameX = -3 * mSprite.getWidth() / 2 + Gdx.graphics.getWidth() / 4;
		mSprite.setPosition(buttonFrameX, mSprite.getY() -mSprite.getHeight() / 2);
				
		//Move it to the appropriate beat
		float measureWidthPixels = mSprite.getWidth() * Constants.MEASURE_WIDTH_NOTES;
		if (mLeftTrack)
			mSprite.translate((beat / (Constants.MEASURE_WIDTH_NOTES / 4.f)) * measureWidthPixels, 0);
		else
			mSprite.translate((-beat / (Constants.MEASURE_WIDTH_NOTES / 4.f)) * measureWidthPixels, 0);	
			
		position.x = mSprite.getX();
		position.y = mSprite.getY();
	}
	
	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		mSprite.draw(batch);
	}
	
	public Rectangle getHitBounds(){
		return mSprite.getBoundingRectangle();
	}
	
	public void onPress(){
		pressed = true;
		if (mTiming != Timing.MISS && mTiming != Timing.GOOD)			
			flagForRemoval();
	}
	
	public void onRelease(){}
	
	public void onOutOfBounds(){
		flagForRemoval();
	}
	
	public void onMiss(){
		pressed = true;
	}
	
	public Timing resolveTimingWindow(float distance){
		
		if (pressed) return mTiming;
		if (distance > Constants.MISS_WINDOW){					
			mTiming = Timing.MISS;
		}
		else if ((distance <= Constants.MISS_WINDOW) && (distance > Constants.GOOD_WINDOW)){
			mTiming = Timing.GOOD;
		}
		else if ((distance <= Constants.GOOD_WINDOW) && (distance > Constants.GREAT_WINDOW)){
			mTiming = Timing.GREAT;
		}
		else if ((distance <= Constants.GREAT_WINDOW) && (distance > Constants.AMAZING_WINDOW)){
			mTiming = Timing.AMAZING;
		}
		else if (distance <= Constants.AMAZING_WINDOW){
			mTiming = Timing.FLAWLESS;	
		}
		return mTiming;
	}
	
	public int getMaxScore(){
		return Constants.FLAWLESS_POINTS;
	}
}
