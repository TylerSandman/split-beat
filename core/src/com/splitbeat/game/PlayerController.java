package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.splitbeat.game.Constants.NoteSlot;

public class PlayerController extends InputAdapter {
	
	private World mWorld;
	private Game mGame;
	public CameraHelper cameraHelper;
	
	PlayerController(Game game, World world){
		mGame = game;
		mWorld = world;
		cameraHelper = new CameraHelper();
		init();
	}
	
	private void init(){
		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public boolean keyDown(int keycode){
		switch(keycode){
		case(Input.Keys.O):
			mWorld.pressSlot(NoteSlot.TOP_RIGHT);
			break;
		case(Input.Keys.K):
			mWorld.pressSlot(NoteSlot.MIDDLE_RIGHT);
			break;
		case(Input.Keys.M):
			mWorld.pressSlot(NoteSlot.BOTTOM_RIGHT);
			break;
		case(Input.Keys.Q):
			mWorld.pressSlot(NoteSlot.TOP_LEFT);
			break;
		case(Input.Keys.S):
			mWorld.pressSlot(NoteSlot.MIDDLE_LEFT);
			break;
		case(Input.Keys.X):
			mWorld.pressSlot(NoteSlot.BOTTOM_LEFT);
			break;
		case(Input.Keys.R):
			mWorld.back();
		}
		return false;
	}
	
	public void handleDebugInput(float deltaTime){
		
		float camMoveSpeed = 100 * deltaTime;
		float camMoveAccelerationFactor = 10;
		
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			camMoveSpeed *= camMoveAccelerationFactor;
		
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			moveCamera(-camMoveSpeed, 0);
		
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			moveCamera(camMoveSpeed, 0);
		
		if (Gdx.input.isKeyPressed(Keys.UP))
			moveCamera(0, camMoveSpeed);
		
		if (Gdx.input.isKeyPressed(Keys.DOWN))
			moveCamera(0, -camMoveSpeed);
		
		if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
			cameraHelper.setPosition(0, 0);
		
		float camZoomSpeed = 1 * deltaTime;
		
		if (Gdx.input.isKeyPressed(Keys.COMMA))
			cameraHelper.addZoom(camZoomSpeed);
		
		if (Gdx.input.isKeyPressed(Keys.PERIOD))
			cameraHelper.addZoom(-camZoomSpeed);
		
		if (Gdx.input.isKeyPressed(Keys.SLASH))
			cameraHelper.setZoom(1);
	}
	
	private void moveCamera(float x, float y){
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}
}
