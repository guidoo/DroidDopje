package nl.vanstormbroek.DroidDopje;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.util.Log;

public class BierdopjeRequest {
	private final static String TAG = "DROIDDOPJE";
	private final static String BIERDOPJE_API_URL = "http://www.vanstormbroek.nl/guido/";
	private final static String BIERDOPJE_API_KEY = "";
	//private final static String BIERDOPJE_API_URL = "http://api.bierdopje.com/";
	//private final static String BIERDOPJE_API_KEY = "69FBEB5B4903C0DC";	
	private DefaultHttpClient client = new DefaultHttpClient();
	private BierdopjeShow bdShow;
	private BierdopjeShowList bdShowList;
	private BierdopjeEpisodeList bdEpList;
	private BierdopjeEpisode bdEp;
	private BierdopjeSubtitleList bdSubList;
	private BierdopjeSubtitle bdSub;
	private BierdopjeEpisodeResponse bdEpResp;
	private Serializer serial;
	private BierdopjeDBAdapter mDbHelper;
	
	/**
	 * Constructor for the request.
	 * Here is determined whether to retrieve from cache or fetch from the web. 
	 * 
	 * @param request Contains the URL, including the action, for instance: GetShowById/1190
	 * @param DbHelper The BierdopjeDBAdapter to use
	 */
	public BierdopjeRequest(String request, BierdopjeDBAdapter DbHelper) {	
		this.mDbHelper = DbHelper;
		serial = new Persister();
		
		if (request.contains("GetEpisodeById")) {
 			if (!getEpisodeFromCache(request)) {
 				Log.i(TAG, "Getting from the web: " + request);
 				Reader reader = getReader(request);
 				this.bdEp = getEpisodeFromWeb(reader);
 			} else {
 				Log.i(TAG, "Getting from the CACHE: " + request);
 			}
 		} 
		
		if (request.contains("GetEpisodesForSeason")) {
 			//if (!getEpisodeFromCache(request)) {
 				Log.i(TAG, "Getting from the web (eplist): " + request);
 				Reader reader = getReader(request);
 				this.bdEpList = getEpisodeListFromWeb(reader);
 			//} else {
 			//	Log.i(TAG, "Getting from the CACHE (eplist): " + request);
 			//}
 		} 
		
		
		if (request.contains("GetShowById")) {
 			if (!getShowFromCache(request)) {
 				Log.i(TAG, "Getting show from the web: " + request);
 				Reader reader = getReader(request);
 				this.bdShow = getShowFromWeb(reader);
 			} else {
 				Log.i(TAG, "Getting show from the CACHE: " + request);
 			}
 		} 
		
		if (request.contains("GetShowByShowLink")) {
			if (!getShowFromCache(request)) {
				Log.i(TAG, "Getting show with link from the web: " + request);
				Reader reader = getReader(request);
				this.bdShow = getShowFromWeb(reader);
			} else {
				Log.i(TAG, "Getting show with link from the CACHE: " + request);
			}
		}
 		
	}
	
	private BierdopjeEpisodeList getEpisodeListFromWeb(Reader reader) {
		BierdopjeEpisodeList bdEpisodeList = null;
		try {
			bdEpisodeList = serial.read(BierdopjeEpisodeList.class, reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bdEpisodeList;
	}

	/**
	 * Retrieves show information from the web
	 * 
	 * @param reader - is serialized and parsed to a show
	 * @return bdShow is returned
	 */
	private BierdopjeShow getShowFromWeb(Reader reader) {
		BierdopjeShow bdShow = null;
		try {
			bdShow = serial.read(BierdopjeShow.class, reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bdShow;
	}

	/**
	 * Retrieves show information from the cache
	 * 
	 * @param reader - is serialized and parsed to a show
	 * @return whether show is found or not. If so, this.bdShow is instantiated.
	 */
	private boolean getShowFromCache(String request) {
		int showId = 0;
		
		try {
			showId = Integer.parseInt(request.substring(request.lastIndexOf("/")+1));	
		} catch (StringIndexOutOfBoundsException e) {
			Log.e(TAG, "Can't look for " + request + " so assuming not in cache.");
			return false;
		} catch (NumberFormatException nfe) {
			Log.e(TAG, "Request doesn't contain a showId: " + request);
			return false;
		}
		
		Log.i(TAG, "found showId in requestforcache: " + showId);
		Cursor cursor = mDbHelper.fetchShow(showId);
		boolean isEmpty = cursor.getCount() == 0; 
		if (!isEmpty) {
			this.bdShow = mDbHelper.cursorToShow(cursor);
		}
		cursor.close();
		
		return !isEmpty;
	}

	/**
	 * Retrieves episode information from the cache
	 * 
	 * @param request - URL request for the episode
	 * @return whether the episode information is in cache.
	 * If so, this.bdEp is instantiated 
	 */
	private boolean getEpisodeFromCache(String request) {
		int epId = 0;

		try {
			epId = Integer.parseInt(request.substring(request.lastIndexOf("/")+1));	
		} catch (StringIndexOutOfBoundsException e) {
			Log.e(TAG, "Can't look for " + request + " so assuming not in cache.");
			return false;
		} catch (NumberFormatException nfe) {
			Log.e(TAG, "Request doesn't contain an epId: " + request);
			return false;
		}
		
		Log.i(TAG, "found epid in cache: " + epId);
		Cursor cursor = mDbHelper.fetchEpisode(epId);
		boolean isEmpty = cursor.getCount() != 0; 
		if (!isEmpty) {
			this.bdEp= mDbHelper.cursorToEpisode(cursor);
		}
		cursor.close();
	
		return !isEmpty;
	}

	/**
	 * Retrieves episode information from the web
	 * 
	 * @param reader - is serialized and parsed to an episode
	 * @return bdEpisode is returned
	 */
	private BierdopjeEpisode getEpisodeFromWeb(Reader reader) {
		BierdopjeEpisode bdEpisode = null;
		try {
			bdEpisode = serial.read(BierdopjeEpisode.class, reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bdEpisode;
	}
	
	/**
	 * @param request URL to fetch
	 * @return reader, to be passed along for further processing.
	 */
	private Reader getReader(String request) {
		Reader reader = null;
		String xmlData = null;
			try {
				xmlData = new String(retrieve(BIERDOPJE_API_URL + BIERDOPJE_API_KEY + request));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.w(TAG, "RETURN RESPONSE EMPTY");
			}
     		if (xmlData == null) {
     			reader = new StringReader("error");
     		} else {
     			reader = new StringReader(xmlData);   		
     		}
		return reader;
	}
	
	/**
	 * Gets an episode, previously filled by instantiating a new BierdopjeRequest.
	 * This can be either from cache or the web
	 * @return BierdopjeEpisode
	 */
	public BierdopjeEpisode getEpisode() {
		
		return bdEp;
	}
	
	/**
	 * Gets an episodelist, previously filled by instantiating a new BierdopjeRequest.
	 * This can be either from cache or the web
	 * @return BierdopjeEpisodeList
	 */
	public BierdopjeEpisodeList getEpisodeList() {
		
		return bdEpList;
	}
	
	/**
	 * Gets a show, previously filled by instantiating a new BierdopjeRequest.
	 * This can be either from cache or the web
	 * @return BierdopjeShow
	 */
	public BierdopjeShow getShow() {
		return bdShow;
	}
	
	/**
	 * Gets a showlist, previously filled by instantiating a new BierdopjeRequest.
	 * This can be either from cache or the web
	 * @return BierdopjeShowList
	 */
	public BierdopjeShowList getShowList() {
		return bdShowList;
	}
	
	/**
	 * Handles getting the information from web
	 * @param url request
	 * @return String with response from the request
	 */
	private String retrieve(String url) {
		Log.i(TAG, "getting : " + url);
		HttpGet getRequest = new HttpGet(url);

		try {

			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}

			HttpEntity getResponseEntity = getResponse.getEntity();

			if (getResponseEntity != null) {
				return EntityUtils.toString(getResponseEntity);
			}

		} 
		catch (IOException e) {
			getRequest.abort();
			Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
		}

		return null;

	}
	
	/**
	 * Just to show something nicer than Object@0423f03 :)
	 */
	@Override
	public String toString() {
		if (bdShow != null) {
			return bdShow.toString();
		} else {
			return "no-show, haha";
		}
	}

}
