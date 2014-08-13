package com.splitbeat.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractGameObject {
	
	protected Sprite mSprite;
	public Vector2 velocity;
	private boolean mFlaggedForRemoval;
	public Vector2 position;
	public AbstractGameObject(Sprite sprite){
		mSprite = sprite;
		velocity = new Vector2();
		position = new Vector2();
		mFlaggedForRemoval = false;
	}
	
	public void update(float deltaTime){
		mSprite.translate(velocity.x * deltaTime, velocity.y * deltaTime);
		position.x = mSprite.getX();
		position.y = mSprite.getY();
	}
	
	public Rectangle getBounds(){
		return mSprite.getBoundingRectangle();
	}
	
	public boolean isFlaggedForRemoval(){
		return mFlaggedForRemoval;
	}
	
	public void flagForRemoval(){
		mFlaggedForRemoval = true;
	}
	
	public void moveBy(float x, float y){
		mSprite.translate(x, y);
	}
	
	public void setPosition(float x, float y){
		mSprite.setPosition(x, y);
	}
	
	public void setOrigin(float x, float y){
		mSprite.setOrigin(x, y);
	}
	
	public abstract void render(SpriteBatch batch);
}
