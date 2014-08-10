package com.splitbeat.game;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;

public class TmxMapBuilder {
	
	private Writer mFileWriter;
	private InputStream mIStream;
	private XmlWriter mXmlWriter;
	private XmlReader mXmlReader;
	private Element mRoot;
	private SongData mData;
	
	TmxMapBuilder(){			
	}
	
	public void create(SongData data){
		
		mData = data;
		String songPath = 
				Constants.LOCAL_MAPS_PATH + "/" + mData.getName() + "/" + 
				mData.getName().toLowerCase().replace(" ", "_") + ".tmx";
		mFileWriter = Gdx.files.local(songPath).writer(false);
		mXmlWriter = new XmlWriter(mFileWriter);
		mXmlReader = new XmlReader();		
		init();
		save();
	}
	
	private void init(){	
		
		//Number of beats in the song multiplied by how many notes are in a measure
		int mapWidth = (int) Math.ceil(mData.getBpm() * mData.getLength() / 60.f * Constants.MEASURE_WIDTH_NOTES);		
	
		//Root element containing Tiled Map data
		mRoot = new Element("map", null);
		
		//Map attributes
		mRoot.setAttribute("version", "1.0");
		mRoot.setAttribute("orientation", "orthogonal");
		//Calculate the number of beats
		mRoot.setAttribute("width", Integer.toString(mapWidth));
		mRoot.setAttribute("height", "3");
		mRoot.setAttribute("tilewidth", "1");
		mRoot.setAttribute("tileheight", "1");
		
		//Properties like bpm, offset, artist, etc
		Element propsEle = new Element("properties", mRoot);
		mRoot.addChild(propsEle);
		
		Element bpmEle = new Element("property", propsEle);
		bpmEle.setAttribute("name", "bpm");
		bpmEle.setAttribute("value", Float.toString(mData.getBpm()));
		propsEle.addChild(bpmEle);
		
		Element offsetEle = new Element("property", propsEle);
		offsetEle.setAttribute("name", "offset");
		offsetEle.setAttribute("value", Float.toString(mData.getOffset()));
		propsEle.addChild(offsetEle);
		
		Element artistEle = new Element("property", propsEle);
		artistEle.setAttribute("name", "artist");
		artistEle.setAttribute("value", mData.getArtist());
		propsEle.addChild(artistEle);
		
		Element lengthEle = new Element("length", propsEle);
		lengthEle.setAttribute("name", "length");
		lengthEle.setAttribute("value", Integer.toString(mData.getLength()));
		propsEle.addChild(lengthEle);
			
		//Notes
		Element notes = new Element("objectgroup", mRoot);
		mRoot.addChild(notes);
		notes.setAttribute("name", "Notes");
		notes.setAttribute("width", Integer.toString(mapWidth));
		notes.setAttribute("height", "3");
		
		//Markers
		Element markers = new Element("objectgroup", mRoot);
		mRoot.addChild(markers);
		markers.setAttribute("name", "Markers");
		markers.setAttribute("width", Integer.toString(mapWidth));
		markers.setAttribute("height", "3");	
	}
	
	public void save(){
		try {
			mXmlWriter
			.element("map")
				.attribute("version", mRoot.getAttribute("version"))
				.attribute("orientation", mRoot.getAttribute("orientation"))
				.attribute("width", mRoot.getAttribute("width"))
				.attribute("height", mRoot.getAttribute("height"))
				.attribute("tilewidth", mRoot.getAttribute("tilewidth"))
				.attribute("tileheight", mRoot.getAttribute("tileheight"))					
				.element("properties")
					.element("property")
						.attribute("name", "bpm")
						.attribute("value", mRoot.getChildByName("properties").getChild(0).getAttribute("value"))
					.pop()
					.element("property")
						.attribute("name", "offset")
						.attribute("value", mRoot.getChildByName("properties").getChild(1).getAttribute("value"))
					.pop()
					.element("property")
						.attribute("name", "artist")
						.attribute("value", mRoot.getChildByName("properties").getChild(2).getAttribute("value"))
					.pop()
					.element("property")
						.attribute("name", "length")
						.attribute("value", mRoot.getChildByName("properties").getChild(3).getAttribute("value"))
					.pop()					
				.pop();
				//Notes
				mXmlWriter
				.element("objectgroup")
					.attribute("name", "Notes")
					.attribute("width", mRoot.getAttribute("width"))
					.attribute("height", mRoot.getAttribute("height"))
				.pop()
				.element("objectgroup")
					.attribute("name", "Markers")
					.attribute("width", mRoot.getAttribute("width"))
					.attribute("height", mRoot.getAttribute("height"))
				.pop()
			.pop().close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
