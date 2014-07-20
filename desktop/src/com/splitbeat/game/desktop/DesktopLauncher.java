package com.splitbeat.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;
import com.splitbeat.game.SplitBeatGame;

public class DesktopLauncher {
	
	private static boolean rebuildAtlas = true;
	private static boolean drawDebugOutline = false;
	
	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1900;
		config.height = 1200;
		config.resizable = false;
		
		if (rebuildAtlas){
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.debug = drawDebugOutline;
			TexturePacker2.process(settings, "assets-raw/images/480x320", "../android/assets/images/480x320", "split-beat.pack");
			TexturePacker2.process(settings, "assets-raw/images/960x720", "../android/assets/images/960x720", "split-beat.pack");
		}
		
		new LwjglApplication(new SplitBeatGame(), config);
	}
}
