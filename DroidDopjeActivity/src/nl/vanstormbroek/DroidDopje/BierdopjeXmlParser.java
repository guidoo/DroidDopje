package nl.vanstormbroek.DroidDopje;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.ContentValues;
import android.util.Log;


public class BierdopjeXmlParser {
	private final static String TAG = "BierdopjeXmlParser";
	private ContentValues cv;
	
	public BierdopjeXmlParser(String feedUrl) {
		//super(feedUrl);
		getXML("http://www.vanstormbroek.nl/guido/24.xml");
	}
	
	public BierdopjeXmlParser() {
		getXML("http://www.vanstormbroek.nl/guido/24.xml");
	}

	public boolean getXML(String request) {
		try {
			Log.i(TAG, "im trying....");
			URL url = new URL(request);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			
			XMLReader xr = sp.getXMLReader();
			//set handler for xmlreader:
			BierdopjeParseHandler bierdopjeParseHandler = new BierdopjeParseHandler();
			xr.setContentHandler(bierdopjeParseHandler);
			xr.parse(new InputSource(url.openStream()));	
			Log.i(TAG, "xr is parsed");			
			cv = bierdopjeParseHandler.getCV();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "oeps: " + e.getMessage());
		} 
		return true;
	}
	
	public ContentValues getCV() {
		return cv;
	}
}
