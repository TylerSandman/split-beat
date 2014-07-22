package com.splitbeat.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BPMMarker extends AbstractGameObject{

	public final float beat;
	public final float bpm;
	BPMMarker(float beat, float bpm, boolean leftTrack){
		
		//Use button sprite to keep track of position and do collision detection
		super(new Sprite(Assets.instance.button.buttonGrey));
		this.beat = beat;
		this.bpm = bpm;
		
		//Center the sprite's origin
		mSprite.setOriginCenter();
		
		//Set the sprite to the base position (at beat 0)
		float buttonFrameX;
		if (leftTrack)
			buttonFrameX = mSprite.getWidth() / 2 - Gdx.graphics.getWidth() / 4;
		else
			buttonFrameX = -3 * mSprite.getWidth() / 2 + Gdx.graphics.getWidth() / 4;
		mSprite.setPosition(buttonFrameX, mSprite.getY() -mSprite.getHeight() / 2);
				
		//Move it to the appropriate beat
		float measureWidthPixels = mSprite.getWidth() * Constants.MEASURE_WIDTH_NOTES;
		if (leftTrack)
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
	public void render(SpriteBatch batch) {mSprite.draw(batch);}
}
