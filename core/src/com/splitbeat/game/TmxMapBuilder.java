package com.splitbeat.game;

import java.io.IOException;
import java.io.Writer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlWriter;

public class TmxMapBuilder {
	
	private Writer mFileWriter;
	private XmlWriter mXmlWriter;
	
	TmxMapBuilder(){			
	}
	
	public void create(String name){
		
		mFileWriter = Gdx.files.local(Constants.LOCAL_MAPS_PATH + name + ".tmx").writer(false);
		mXmlWriter = new XmlWriter(mFileWriter);
		init();
	}
	
	private void init(){
		//Root element containing Tiled Map data
		try {
			mXmlWriter
			.element("map")
				.attribute("version", "1.0")
				.attribute("orientation", "orthogonal")
				.attribute("width", "100")
				.attribute("height", "3")
				.attribute("tilewidth", "1")
				.attribute("tileheight", "1")
				//Map properties like bpm and offset
				.element("properties")
					.element("property")
						.attribute("name", "bpm")
						.attribute("value", "120")
					.pop()
					.element("property")
						.attribute("name", "offset")
						.attribute("value", "0.00")
					.pop()
				.pop()
				//Notes
				.element("objectgroup")
				.attribute("name", "Notes")
				.attribute("width", "100")
				.attribute("height", "3")
				.pop()
				//BPM markers
				.element("objectgroup")
				.attribute("name", "Markers")
				.attribute("width", "100")
				.attribute("height", "3")
				.pop()
			.pop().close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

}
