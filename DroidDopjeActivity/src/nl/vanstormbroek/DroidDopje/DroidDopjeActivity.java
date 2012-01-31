package nl.vanstormbroek.DroidDopje;

//some nice comment 
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DroidDopjeActivity extends Activity {

	private static final String TAG = "BierdopjeDbAdapter";	
	private String mBierdopjeUserId;
	private BierdopjeDBAdapter mDbHelper;
	private TextView mDebugTextView;
	private AutoCompleteTextView mShowNameView;
	private BierdopjeRequest BdRequest;
	private BierdopjeShow BdShow;

	private final static String BD_BASE_URL = "http://www.bierdopje.com";
	final static int[] to = new int[] { android.R.id.text1 }; //textview in layout dropdown.xml
	final static String[] from = new String[] { BierdopjeDBAdapter.SHOWNAME }; //table from database


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mDbHelper = new BierdopjeDBAdapter(this);
		Log.i(TAG, "created dbhelper");

		BdRequest = new BierdopjeRequest("GetShowById/1190", mDbHelper);
		long rowId = mDbHelper.insertShow(BdRequest.getShow());

		BdRequest = new BierdopjeRequest("GetShowById/69", mDbHelper);
		rowId = mDbHelper.insertShow(BdRequest.getShow());

		BdRequest = new BierdopjeRequest("GetShowById/12708", mDbHelper);
		rowId = mDbHelper.insertShow(BdRequest.getShow());





		mShowNameView = (AutoCompleteTextView) findViewById(R.id.ShowNameView);

		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DroidDopjeActivity.this, MyShows.class);
				startActivity(intent);

			};
		});
		//parseMyShows(null);

		// parseCalendar(null); //mag aan als de cal gedaan moet worden.
		/*
		BdRequest = new BierdopjeRequest("GetEpisodeById-EpisodeCached.xml", mDbHelper);
		if (mDbHelper.insertEpisode(BdRequest.getEpisode()) > -1) {
			Log.i(TAG, "cached ep inserted");
		}
		BdRequest = new BierdopjeRequest("GetEpisodeById-EpisodeNotCached.xml", mDbHelper);
		if (mDbHelper.insertEpisode(BdRequest.getEpisode()) > -1) {
				Log.i(TAG, "non - cached ep inserted");
		}

		Cursor cursor = mDbHelper.fetchEpisode(599549);//606720
		if (cursor.moveToFirst()) {
			String res = cursor.getString(cursor.getColumnIndexOrThrow(BierdopjeDBAdapter.SHOWLINK));
			Log.i(TAG, "found " + res);
		} else {
			Log.i(TAG, "No cursor... ");
		} 
		cursor.close();
		 */


		//BdRequest = new BierdopjeRequest("showlist.xml");
		//mDbHelper.insertShowList(BdRequest.getShowList());


		// Create a SimpleCursorAdapter for the State Name field.
		SimpleCursorAdapter adapter = 
				new SimpleCursorAdapter(this, 
						R.layout.dropdown,null,
						from, to);
		mShowNameView.setAdapter(adapter);

		// Set an OnItemClickListener, to update dependent fields when
		// a choice is made in the AutoCompleteTextView.
		mDebugTextView = (TextView) findViewById(R.id.DebugTextView);

		mShowNameView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {
				// Get the cursor, positioned to the corresponding row in the
				// result set
				Cursor cursor = (Cursor) listView.getItemAtPosition(position);

				int bdid = cursor.getInt(cursor.getColumnIndexOrThrow(BierdopjeDBAdapter.SHOWID));
				cursor.close();

				BdRequest = new BierdopjeRequest("GetShowById/" + bdid, mDbHelper);

				BdShow = BdRequest.getShow(); 
				if (BdShow != null) {
					Log.i(TAG, "Found requested show: " + BdShow.toString());
				} else {
					Toast.makeText(getApplicationContext(),"Show niet gevonden!",Toast.LENGTH_SHORT).show();
				}


				// Update the parent class's TextView
				mDebugTextView.setText(Integer.toString(bdid));

				//Update Spinner
				Spinner mSeasonSpinner = (Spinner) findViewById(R.id.SeasonSpinner);
				ArrayAdapter <CharSequence> adapter;
				adapter = new ArrayAdapter <CharSequence> (getApplicationContext(), android.R.layout.simple_spinner_item );

				//BdShow.seasons = 7;
				for (int i=BdShow.seasons;i>0; i--) {
					//adapter.add("Seizoen " + i);
					adapter.add("" +i);
				}


				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mSeasonSpinner.setAdapter(adapter);
				mSeasonSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

				//mShowNameView. 'submit'
				
				
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				mShowNameView.clearFocus();
			}
		});

		mShowNameView.setOnKeyListener(new OnKeyListener() {
		    public boolean onKey(View v, int keyCode, KeyEvent event) {
		        // If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER)) {
		          // Perform action on key press
		          Toast.makeText(DroidDopjeActivity.this, mShowNameView.getText(), Toast.LENGTH_SHORT).show();
		          return true;
		        }
		        return false;
		    } 
		   
		});
		
		
		// Set the CursorToStringConverter, to provide the labels for the
		// choices to be displayed in the AutoCompleteTextView.
		adapter.setCursorToStringConverter(new CursorToStringConverter() {
			public String convertToString(android.database.Cursor cursor) {

				// Get the label for this row out of the "shownName" column
				final int columnIndex = cursor.getColumnIndexOrThrow("showName");
				final String str = cursor.getString(columnIndex);

				return str;
			}
		});

		// Set the FilterQueryProvider, to run queries for choices
		// that match the specified input.
		adapter.setFilterQueryProvider(new FilterQueryProvider() {
			public Cursor runQuery(CharSequence constraint) {
				// Search for shows whose names containing the specified letters.

				Cursor cursor = mDbHelper.getMatchingShows(
						(constraint != null ? constraint.toString() : null));

				return cursor;
			}
		});

		
		
	}

	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			Toast.makeText(parent.getContext(), "Fetching episodes for " +
					parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
			BdRequest = new BierdopjeRequest("GetEpisodesForSeason/" + BdShow.showid + 
					'/' + parent.getItemAtPosition(pos), mDbHelper);
			BierdopjeEpisodeList bdEpList = BdRequest.getEpisodeList();
			if (bdEpList != null) {
				Log.i(TAG, "Listsize: " + bdEpList.episodes.size());

				Spinner mEpisodeSpinner = (Spinner) findViewById(R.id.EpisodeSpinner);

				ArrayAdapter <CharSequence> adapter;
				adapter = new ArrayAdapter <CharSequence> (getApplicationContext(), android.R.layout.simple_spinner_item );


				for (int i=0;i<bdEpList.episodes.size();i++) {
					BierdopjeEpisode bdEp = bdEpList.episodes.get(i); 
					adapter.add("" + bdEp.episode + " - " + bdEp.title);
				}


				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mEpisodeSpinner.setAdapter(adapter);
				mEpisodeSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

			}

		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}


	public void parseMyShows(URL url) {
		if (url == null)
			try {
				url = new URL("http://www.vanstormbroek.nl/guido/guidooshows.html");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

		Document document = null;
		try {
			document = Jsoup.connect(url.toString()).get();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Elements elts = document.select("a[href]");
		ContentValues cv = new ContentValues();

		for (Element elt : elts) {
			String res = elt.attr("href");
			if (res.startsWith("/shows/")) {
				Log.i(TAG, "myshowsid: " + res);

				cv.put("showlink", BD_BASE_URL + res);
			}


			BdRequest = new BierdopjeRequest("/GetShowByShowLink/" + cv.getAsString("showlink"), mDbHelper);
			mDbHelper.insertShow(BdRequest.getShow());


		}

	}

	public void parseCalendar(URL myUrl) {
		//fallback, temo:
		if (myUrl == null)
			try {
				myUrl = new URL("http://www.vanstormbroek.nl/guido/episodes.html");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		Document document = null;
		try {
			document = Jsoup.connect(myUrl.toString()).get();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Elements elts = document.select("span[class=lijstitem]");

		for (Element elt : elts) {
			String res = elt.attr("id").toString().substring(elt.attr("id").toString().indexOf("/")+1);
			Log.i(TAG, "id: " + res);

			BdRequest = new BierdopjeRequest("GetEpisodeById-606720.xml",mDbHelper);
			//BdRequest = new BierdopjeRequest("/GetEpisodeById/" + res);
			mDbHelper.insertEpisode(BdRequest.getEpisode());


		}
	}

	public boolean getBierdopjeUserId(URL myUrl) {
		try {
			if (myUrl == null) myUrl = new URL("http://www.vanstormbroek.nl/guido/episodes.html");

			HttpURLConnection myUc = (HttpURLConnection) myUrl.openConnection();

			InputStream is = new BufferedInputStream(myUc.getInputStream());
			ByteArrayBuffer baf = new ByteArrayBuffer(1024);
			int current = 0;
			while ((current = is.read()) != -1) {
				baf.append((byte) current);
			}	
			byte[] ba =  baf.toByteArray() ;
			String bas = new String(ba);		
			int pos = bas.indexOf("<a href=\"http://external.bierdopje.com/calendar/");
			Log.i(TAG, "Calendar link found at: " + pos);
			if (pos>-1 )bas = bas.substring(pos);
			pos = bas.indexOf("/calendar/");
			bas = bas.substring(10);
			int pos2 = bas.indexOf("/rss/");
			if ((pos > -1) && (pos2 > -1)) {
				String result = bas.substring(pos, pos2);
				Log.i(TAG, "BierdopjeUserId: " + result);
				if (result.length() == 0) return false;
				mBierdopjeUserId = result;
				return true;
			}		 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return false;
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
	}
}
