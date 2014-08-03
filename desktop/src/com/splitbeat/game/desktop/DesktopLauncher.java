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
		config.width = 1600;
		config.height = 900;
		config.foregroundFPS = 60;
		config.resizable = false;
		
		if (rebuildAtlas){
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.debug = drawDebugOutline;
			TexturePacker2.process(settings, "assets-raw/images/game", "../android/assets/images/game", "split-beat-game.pack");
			TexturePacker2.process(settings, "assets-raw/images/gui", "../android/assets/images/gui", "split-beat-gui.pack");
		}
		
		new LwjglApplication(new SplitBeatGame(), config);
	}
}
