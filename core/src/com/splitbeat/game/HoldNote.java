package com.splitbeat.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.splitbeat.game.Constants.NoteSlot;
import com.splitbeat.game.Constants.NoteType;
import com.splitbeat.game.Constants.Timing;

public class HoldNote extends Note {

	private Vector2 mDrawPosition;
	private Vector2 mDrawSize;
	private Color mHoldTintColor;
	private TextureRegion mHoldBackground;
	private TextureRegion mHoldOverlay;
	private float mHoldDurationBeats;
	private float mHoldDurationSeconds;
	private float mSecondsSinceHeld;
	private float mBPM;
	private float mNoteSpeed;
	private boolean mHolding;
	private int mKeycode;
	private boolean mLeftTrack;
	private boolean mSuccessfulRelease;
	
	HoldNote(float beat, NoteSlot slot, NoteType type, float holdDurationBeats, float bpm, ScoreManager scoreManager) {
		super(beat, slot, type, scoreManager);
		mHoldDurationBeats = holdDurationBeats;
		mBPM = bpm;
		float secondsPerBeat = 1.f / (mBPM / 60.f);
		mHoldDurationSeconds = mHoldDurationBeats * secondsPerBeat;
		float measureWidthPixels = mSprite.getWidth() * Constants.MEASURE_WIDTH_NOTES;
		mNoteSpeed = measureWidthPixels * (mBPM / 4.f) / 60.f;
		mHoldDurationSeconds += mSprite.getWidth() / 2.f / mNoteSpeed;
		mHolding = false;
		mLeftTrack = false;
		mSuccessfulRelease = false;
		mHoldBackground = new TextureRegion(Assets.instance.button.holdBackground);
		mHoldOverlay = new TextureRegion(Assets.instance.button.holdOverlay);
		
		switch(slot){
		case TOP_RIGHT:
			mKeycode = Input.Keys.O;
			break;
		case MIDDLE_RIGHT:
			mKeycode = Input.Keys.K;
			break;
		case BOTTOM_RIGHT:
			mKeycode = Input.Keys.M;
			break;
		case TOP_LEFT:
			mKeycode = Input.Keys.Q;
			mLeftTrack = true;
			break;
		case MIDDLE_LEFT:
			mKeycode = Input.Keys.S;
			mLeftTrack = true;
			break;
		case BOTTOM_LEFT:
			mKeycode = Input.Keys.X;
			mLeftTrack = true;
			break;
		default:
			mKeycode = Input.Keys.O;
			break;
		}
		
		if (mLeftTrack){
			mDrawPosition = new Vector2(
					mSprite.getX() + mSprite.getWidth() / 2, mSprite.getY());	
			mHoldBackground.flip(true, false);
			mHoldOverlay.flip(true, false);
		}
		else{
			mDrawPosition = new Vector2(
					mSprite.getX() - mSprite.getWidth() * Constants.MEASURE_WIDTH_NOTES * (mHoldDurationBeats / 4.f),
					mSprite.getY());			
		}
		mDrawSize = new Vector2(
				(mHoldDurationBeats / 4.f) * mSprite.getWidth() * Constants.MEASURE_WIDTH_NOTES + mSprite.getWidth() / 2.f,
				mHoldBackground.getRegionHeight());
		
		switch(type){
		case QUARTER:
			mHoldTintColor = Color.RED;
			break;
		case EIGHTH:
			mHoldTintColor = Color.BLUE;
			break;
		case TWELVTH:
			mHoldTintColor = Color.PURPLE;
			break;
		case SIXTEENTH:
			mHoldTintColor = Color.GREEN;
			break;
		default:
			mHoldTintColor = Color.RED;
			break;
		}
	}
	
	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
		if (mHolding && !Gdx.input.isKeyPressed(mKeycode)){
			onRelease();
		}
		if (mHolding || mSuccessfulRelease){
			mSecondsSinceHeld += deltaTime;
			if ((mSecondsSinceHeld >= mHoldDurationSeconds - Constants.HOLD_WINDOW) &&
				(!mSuccessfulRelease)){
				onRelease();
			}
			else{
				if (mLeftTrack && (mSecondsSinceHeld < mHoldDurationSeconds)){
					mDrawPosition.x += deltaTime * mNoteSpeed;
				}
				mDrawSize.x -= deltaTime * mNoteSpeed;
				mDrawSize.x = MathUtils.clamp(mDrawSize.x, 0, Integer.MAX_VALUE);
			}
		}
	}
	
	@Override
	public void render(SpriteBatch batch){
		
		//Set blending to addition for laser-like effect
		batch.end();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		batch.begin();
		
		//Draw hold
		batch.setColor(mHoldTintColor);
		batch.draw(mHoldBackground, 
				mDrawPosition.x, mDrawPosition.y,
				mDrawSize.x, mDrawSize.y);
		
		batch.setColor(Color.WHITE);
		batch.draw(mHoldOverlay, 
				mDrawPosition.x, mDrawPosition.y,
				mDrawSize.x, mHoldOverlay.getRegionHeight());
		
		//Clear tint on SpriteBatch and draw the actual note
		batch.setColor(Color.CLEAR);
		if (!mHolding)
			mSprite.setAlpha(1);		
		else
			mSprite.setAlpha(0);
		mSprite.draw(batch);
	}
	
	@Override
	public void onPress(){
		mHolding = true;
		pressed = true;
	}
	
	@Override
	public void onRelease(){
		if (mHolding){
			mHolding = false;
			//Grace period for holds
			if (mSecondsSinceHeld >= (mHoldDurationSeconds - Constants.HOLD_WINDOW)){
				mScoreManager.processTiming(Timing.HOLD);
				mSuccessfulRelease = true;
			}
			else{
				mScoreManager.processTiming(Timing.DROP);
				mSuccessfulRelease = false;
				mHoldTintColor = Color.GRAY;
			}
		}
	}
	
	@Override
	public void onMiss(){
		pressed = true;
		mHoldTintColor = Color.GRAY;
	}
	
	@Override
	public Rectangle getBounds(){
		if (mLeftTrack)
			return new Rectangle(mDrawPosition.x + mDrawSize.x, mDrawPosition.y, mDrawSize.x, mDrawSize.y);
		else
			return new Rectangle(mDrawPosition.x, mDrawPosition.y, mDrawSize.x, mDrawSize.y);
	}
	
	@Override
	public void onOutOfBounds(){
		flagForRemoval();
	}
	
	@Override
	public int getMaxScore(){
		return (Constants.FLAWLESS_POINTS + Constants.HOLD_POINTS);
	}
}