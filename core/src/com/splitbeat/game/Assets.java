package com.splitbeat.game;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener{
	
	public static final Assets instance = new Assets();
	private AssetManager mAssetManager;
	public AssetGUI gui;
	public AssetButton button;
	public AssetMap maps;
	public AssetSync sync;
	public AssetMusic music;
	public AssetSounds sounds;
	public AssetFonts fonts;
	
	private Assets(){}
	
	public void init (AssetManager assetManager){
		
		//Resolution smallRes = new Resolution(320, 480, "480x320");
		//Resolution largeRes = new Resolution(760, 920, "960x720");
		//Resolution[] resolutions = new Resolution[]{ smallRes, largeRes };
		//ResolutionFileResolver resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), resolutions);
		
		mAssetManager = assetManager;
		mAssetManager.setErrorListener(this);	
		
		//Load main maps
		loadMaps();

		//Load sync map
		mAssetManager.load(Constants.SYNC_LEFT_MAP, TiledMap.class);
		mAssetManager.load(Constants.SYNC_RIGHT_MAP, TiledMap.class);
		mAssetManager.finishLoading();
		
		//Load button textures;
		mAssetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));
		mAssetManager.load(Constants.TEXTURE_ATLAS_NOTES, TextureAtlas.class);
		mAssetManager.finishLoading();
		
		//Load GUI
		mAssetManager.load(Constants.TEXTURE_ATLAS_GUI, TextureAtlas.class);
		mAssetManager.finishLoading();
		
		//Load music
		mAssetManager.load("music/sync.ogg", Music.class);
		mAssetManager.finishLoading();
		
		//Load sound
		mAssetManager.load("sound/sync_click.ogg", Sound.class);
		mAssetManager.finishLoading();
		
		//Initialize assets
		TextureAtlas atlas = mAssetManager.get(Constants.TEXTURE_ATLAS_NOTES);	
		button = new AssetButton(atlas);
		maps = new AssetMap(mAssetManager);
		sync = new AssetSync(mAssetManager);
		music = new AssetMusic(mAssetManager);
		sounds = new AssetSounds(mAssetManager);
		fonts = new AssetFonts();
		atlas = mAssetManager.get(Constants.TEXTURE_ATLAS_GUI);
		gui = new AssetGUI(atlas);	
	}
	
	private void loadMaps(){
		mAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		FileHandle[] songHandles = Gdx.files.local(Constants.DEFAULT_MAPS_PATH).list();
		for (FileHandle handle : songHandles){
			if (handle.isDirectory()){
				
				//Naming requirement to have the base map name in snake case
				String baseMapName = handle.name().toLowerCase().replace(" ", "_");
				
				FileHandle[] oggSongs = handle.list(".ogg");
				FileHandle[] mp3Songs = handle.list(".mp3");
				
				//Load song
				String loadPath = "";
				if (oggSongs.length > 0)
					loadPath = handle.path() + "/" + oggSongs[0].name();
				else if (mp3Songs.length > 0)
					loadPath = handle.path() + "?" + mp3Songs[0].name();
					
				else
					continue;
				mAssetManager.load(loadPath, Music.class);
				mAssetManager.finishLoading();
				
				FileHandle[] tiledMapHandles = handle.list(".tmx");
				for (Difficulty dif : Difficulty.values())
					loadMap(dif, baseMapName, tiledMapHandles);
			}
		}
	}
	
	private void loadMap(Difficulty difficulty, String baseMapName, FileHandle[] tiledMapHandles){
		
		String dif = "";
		switch(difficulty){
		case Easy:
			dif = "_easy";
			break;
		case Medium:
			dif = "_medium";
			break;
		case Hard:
			dif = "_hard";
			break;
		}
		
		String leftPath = "";
		String rightPath = "";
		for (FileHandle mapHandle : tiledMapHandles){
			if (mapHandle.nameWithoutExtension().
					equals(baseMapName + dif + "_left")){
				leftPath = mapHandle.path();
			}
			else if (mapHandle.nameWithoutExtension().
					equals(baseMapName + dif + "_right")){
				rightPath = mapHandle.path();
			}
		}
		if (!leftPath.equals("") && !rightPath.equals("")){
			mAssetManager.load(leftPath, TiledMap.class);
			mAssetManager.load(rightPath, TiledMap.class);
			mAssetManager.finishLoading();
		}
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error("Asset", "Couldn't load asset '" + asset.fileName + "'", (Exception) throwable);
	}

	@Override
	public void dispose() {	
		mAssetManager.dispose();
		fonts.defaultFont.dispose();
	}
	
	public class AssetGUI{
		
		public final AtlasRegion redPanelLeft;
		public final AtlasRegion redPanelRight;
		public final AtlasRegion redPanelRepeat;
		public final AtlasRegion highlightedPanelLeft;
		public final AtlasRegion highlightedPanelRight;
		public final AtlasRegion highlightedPanelRepeat;
		public final AtlasRegion greenSlider;
		public final AtlasRegion yellowSlider;
		public final AtlasRegion redSlider;
		public final AtlasRegion greySliderLeft;
		public final AtlasRegion greySliderRight;
		public final AtlasRegion leftArrow;
		public final AtlasRegion rightArrow;
		public final AtlasRegion upArrow;
		public final AtlasRegion downArrow;
		public final NinePatch greyPanelNinePatch;
		AssetGUI(TextureAtlas atlas){
			redPanelLeft = atlas.findRegion("red_panel_left");
			redPanelRight = atlas.findRegion("red_panel_right");
			redPanelRepeat = atlas.findRegion("red_panel_repeat");
			highlightedPanelLeft = atlas.findRegion("highlighted_panel_left");
			highlightedPanelRight = atlas.findRegion("highlighted_panel_right");
			highlightedPanelRepeat = atlas.findRegion("highlighted_panel_repeat");
			greenSlider = atlas.findRegion("green_sliderRight");
			yellowSlider = atlas.findRegion("yellow_sliderRight");
			redSlider = atlas.findRegion("red_sliderRight");
			greySliderLeft = atlas.findRegion("grey_sliderLeft");
			greySliderRight = atlas.findRegion("grey_sliderRight");
			leftArrow = atlas.findRegion("left_arrow");
			rightArrow = atlas.findRegion("right_arrow");
			upArrow = atlas.findRegion("up_arrow");
			downArrow = atlas.findRegion("down_arrow");
			greyPanelNinePatch = atlas.createPatch("grey_panel.9");
		}
	}
	
	public class AssetButton{

		public final AtlasRegion buttonRed;
		public final AtlasRegion buttonBlue;
		public final AtlasRegion buttonGreen;
		public final AtlasRegion buttonOrange;
		public final AtlasRegion buttonYellow;
		public final AtlasRegion buttonGrey;
		public final AtlasRegion holdOverlay;
		public final AtlasRegion holdBackground;
		public final AtlasRegion hitOverlay;
		public final AtlasRegion hitBackground;
		AssetButton(TextureAtlas atlas){
			
			String filename = "Magic_button_";
			buttonRed = atlas.findRegion(filename + "red");
			buttonBlue = atlas.findRegion(filename + "blue");
			buttonGreen = atlas.findRegion(filename + "green");
			buttonOrange = atlas.findRegion(filename + "orange");
			buttonYellow = atlas.findRegion(filename + "yellow");
			buttonGrey = atlas.findRegion(filename + "grey");
			holdOverlay = atlas.findRegion("hold_overlay");
			holdBackground = atlas.findRegion("hold_background");
			hitOverlay = atlas.findRegion("hit_overlay");
			hitBackground = atlas.findRegion("hit_background");
			hitOverlay.getTexture().setFilter(
					TextureFilter.Linear, TextureFilter.Linear);
			hitBackground.getTexture().setFilter(
					TextureFilter.Linear, TextureFilter.Linear);
		}
	}
	
	//Maps MUST be loaded before music is
	public class AssetMusic{
		
		public final Music sync;
		public final HashMap<String, Music> musicMap;
		AssetMusic(AssetManager am){
			sync = am.get("music/sync.ogg", Music.class);
			musicMap = new HashMap<String, Music>();
			for (Map.Entry<String, SongData> entry : maps.dataMap.entrySet()){
				String path = Constants.DEFAULT_MAPS_PATH;
				path += entry.getKey() + "/";
				path += entry.getKey().toLowerCase().replace(" ", "_");
				String mp3Path = path + ".mp3";
				String oggPath = path + ".ogg";
				if (am.isLoaded(mp3Path))
					musicMap.put(entry.getKey(), am.get(mp3Path, Music.class));
				else if (am.isLoaded(oggPath))
					musicMap.put(entry.getKey(), am.get(oggPath, Music.class));
			};
		}	
	}
	
	public class AssetSounds{
		
		public final Sound syncClick;
		AssetSounds(AssetManager am){
			syncClick = am.get("sound/sync_click.ogg", Sound.class);
		}
	}
	
	public class AssetMap{
		
		public final HashMap<String, SongData> dataMap;
		AssetMap(AssetManager am){
			
			dataMap = new HashMap<String, SongData>();
			
			FileHandle[] songHandles = Gdx.files.local(Constants.DEFAULT_MAPS_PATH).list();
			for (FileHandle handle : songHandles){
				if (handle.isDirectory()){
					SongData data = new SongData(handle.name());
					String basePath = handle.path() + "/" + handle.name().toLowerCase().replace(" ", "_");
					
					//Used to get song information that is present in any map
					TiledMap map = null;
					
					//Easy maps
					String easyPath = basePath + "_easy_";
					if (am.isLoaded(easyPath + "left.tmx")){
						data.setEasyMaps(
								am.get(easyPath + "left.tmx", TiledMap.class), 
								am.get(easyPath + "right.tmx", TiledMap.class));
						map = am.get(easyPath + "left.tmx", TiledMap.class);
					}
					
					//Medium maps
					String mediumPath = basePath + "_medium_";
					if (am.isLoaded(mediumPath + "left.tmx")){
						data.setMediumMaps(
								am.get(mediumPath + "left.tmx", TiledMap.class), 
								am.get(mediumPath + "right.tmx", TiledMap.class));
						map = am.get(mediumPath + "left.tmx", TiledMap.class);
					}
					
					//Hard maps
					String hardPath = basePath + "_hard_";
					if (am.isLoaded(hardPath + "left.tmx")){
						data.setHardMaps(
								am.get(hardPath + "left.tmx", TiledMap.class), 
								am.get(hardPath + "right.tmx", TiledMap.class));
						map = am.get(hardPath + "left.tmx", TiledMap.class);
					}
					if (map == null) continue;
					MapProperties props = map.getProperties();
					
					String bpmStr= map.getProperties().get("bpm", String.class);
					float bpm = (float) Double.parseDouble(bpmStr);
					data.setBpm(bpm);
					
					String offsetStr= map.getProperties().get("offset", String.class);
					float offset = (float) Double.parseDouble(offsetStr);
					data.setOffset(offset);
					
					String lenStr= map.getProperties().get("length", String.class);
					int len = Integer.parseInt(lenStr);
					data.setLength(len);
					
					data.setArtist(props.get("artist", String.class));
					data.setTitle(props.get("title", String.class));
					dataMap.put(handle.name(), data);
				}
			}
		}
		
		public void updateMap(String name, Difficulty difficulty, SongData updatedData){
			
			if (dataMap.get(name) == null || dataMap.get(name).getLeftMap(difficulty) == null) return;
			String mapsPath = Constants.DEFAULT_MAPS_PATH + name + "/";
			mapsPath += name.toLowerCase().replace(" ", "_");
			mapsPath += "_";
			mapsPath += difficulty.toString().toLowerCase();
			String leftPath = mapsPath + "_left.tmx";
			String rightPath = mapsPath + "_right.tmx";
			mAssetManager.unload(leftPath);
			mAssetManager.unload(rightPath);
			mAssetManager.load(leftPath, TiledMap.class);
			mAssetManager.load(rightPath, TiledMap.class);
			mAssetManager.finishLoading();
			SongData newData = new SongData(dataMap.get(name));
			switch (difficulty){
			case Easy:
				newData.setEasyMaps(
						mAssetManager.get(leftPath, TiledMap.class), 
						mAssetManager.get(rightPath, TiledMap.class));
				break;
			case Medium:
				newData.setMediumMaps(
						mAssetManager.get(leftPath, TiledMap.class), 
						mAssetManager.get(rightPath, TiledMap.class));
				break;
			case Hard:
				newData.setHardMaps(
						mAssetManager.get(leftPath, TiledMap.class), 
						mAssetManager.get(rightPath, TiledMap.class));
				break;
			}
			newData.setArtist(updatedData.getArtist());
			newData.setTitle(updatedData.getTitle());
			newData.setBpm(updatedData.getBpm());
			newData.setOffset(updatedData.getOffset());
			dataMap.remove(name);
			dataMap.put(name, newData);
		}
	}
	
	public class AssetSync{
		
		public final TiledMap left;
		public final TiledMap right;
		AssetSync(AssetManager am){
			left = am.get(Constants.SYNC_LEFT_MAP, TiledMap.class);
			right = am.get(Constants.SYNC_RIGHT_MAP, TiledMap.class);
		}
	}
	
	public class AssetFonts{
		
		public final BitmapFont defaultFont;
		AssetFonts(){
			defaultFont = new BitmapFont();
			defaultFont.setUseIntegerPositions(false);
		}
	}
}
