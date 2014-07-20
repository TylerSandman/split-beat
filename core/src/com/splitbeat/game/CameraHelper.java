package com.splitbeat.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CameraHelper {
	
	private final float MAX_ZOOM_IN = 0.25f;
	private final float MAX_ZOOM_OUT = 10.0f;
	
	private Vector2 mPosition;
	private float mZoom;
	
	public CameraHelper(){
		mPosition = new Vector2();
		mZoom = 1.0f;
	}
	
	public void update(float deltaTime){}
	
	public void setPosition(float x, float y){
		this.mPosition.set(x, y);
	}
	
	public Vector2 getPosition(){ return mPosition; }
	
	public void addZoom(float amount){ setZoom(mZoom + amount); }
	
	public void setZoom(float zoom){
		mZoom = MathUtils.clamp(zoom, MAX_ZOOM_IN, MAX_ZOOM_OUT);
	}
	
	public float getZoom(){ return mZoom; }
	
	public void applyTo (OrthographicCamera camera){
		camera.position.x = mPosition.x;
		camera.position.y = mPosition.y;
		camera.zoom = mZoom;
		camera.update();
	}

}
