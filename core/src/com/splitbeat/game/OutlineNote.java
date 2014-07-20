package com.splitbeat.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.splitbeat.game.Constants.NoteSlot;
import com.splitbeat.game.Constants.NoteType;
import com.splitbeat.game.Constants.Timing;

public class OutlineNote extends Note {
	
	
	private Color mTintColor;
	private Color mOverlayColor;
	private boolean mNoteHit;
	private float mTimeSinceHit;
	private float mTimeSincePressed;
	private float mAlpha;
	private float mScale;

	OutlineNote(NoteSlot slot, ScoreManager scoreManager) {
		super(0, slot, NoteType.OUTLINE, scoreManager);
		mTintColor = Color.CLEAR;
		mOverlayColor = Color.CLEAR;
		mOverlayColor = Color.CLEAR;
		mOverlayColor.a = 0.f;
		mTimeSinceHit = 0.f;
		mScale = 1.f;
		mTimeSinceHit = 0.f;
		mNoteHit = false;
	}
	
	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
		
		if (mNoteHit){
			mTimeSinceHit += deltaTime;
			if (mTimeSinceHit >= Constants.GLOW_ANIMATION_TIME){
				mTimeSinceHit = 0.f;
				mTintColor = Color.CLEAR;
				mOverlayColor = Color.CLEAR;
				mNoteHit = false;
			}
			else if (mTimeSinceHit >= Constants.GLOW_ANIMATION_TIME/ 2.f){
				mAlpha -= (deltaTime / Constants.GLOW_ANIMATION_TIME / 2);
			}
			else{
				mAlpha += (deltaTime / Constants.GLOW_ANIMATION_TIME / 2);
			}
		}
		
		if (pressed){
			mTimeSincePressed += deltaTime;
			if (mTimeSincePressed >= Constants.OUTLINE_PRESS_ANIMATION_TIME){
				mTimeSincePressed = 0.f;
				pressed = false;
				mScale = 1.f;			
			}
			else if (mTimeSincePressed >= Constants.OUTLINE_PRESS_ANIMATION_TIME / 2.f){
				mScale += (deltaTime / Constants.OUTLINE_PRESS_ANIMATION_TIME / 2);
			}
			else{
				mScale -= (deltaTime / Constants.OUTLINE_PRESS_ANIMATION_TIME / 2);
			}
			mSprite.setScale(mScale);
		}
	}
	
	@Override
	public void render(SpriteBatch batch){
		
		//Set blending to addition for laser-like effect
		batch.end();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		batch.begin();
		
		//Apply calculated alpha for decay effect on glow outline
		mTintColor.a = mAlpha;
		mOverlayColor.a = mAlpha;
		batch.setColor(mTintColor);
		batch.draw(Assets.instance.button.hitBackground, 
				mSprite.getX(), mSprite.getY(),
				Assets.instance.button.hitBackground.getRegionWidth(), Assets.instance.button.hitBackground.getRegionHeight());
		batch.setColor(mOverlayColor);
		batch.draw(Assets.instance.button.hitOverlay, 
				mSprite.getX(), mSprite.getY(),
				Assets.instance.button.hitOverlay.getRegionWidth(), Assets.instance.button.hitOverlay.getRegionHeight());
		mSprite.draw(batch);
		
		//Reset alpha values so batch isn't contaminated
		mTintColor.a = 1.f;
		mOverlayColor.a = 1.f;
		batch.setColor(Color.WHITE);
	}

	public void onNoteHit(Timing timing){
		mNoteHit = true;
		mTimeSinceHit = 0.f;
		switch(timing){
		case MISS:
			mTintColor = Color.PURPLE;
			mOverlayColor = Color.WHITE;
			break;
		case GOOD:
			mTintColor = Color.BLUE;
			mOverlayColor = Color.WHITE;
			break;
		case GREAT:
			mTintColor = Color.GREEN;
			mOverlayColor = Color.WHITE;
			break;
		case AMAZING:
			mTintColor = Color.ORANGE;
			mOverlayColor = Color.WHITE;
			break;
		case FLAWLESS:
			mTintColor = Color.WHITE;
			mOverlayColor = Color.WHITE;
			break;
		default:
			mTintColor = Color.PURPLE;
			mOverlayColor = Color.WHITE;
			break;
		}	
	}
	
	public void onPress(){
		pressed = true;
		mTimeSincePressed = 0.f;
		mScale = 1.f;
	}
}
