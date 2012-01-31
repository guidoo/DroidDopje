package nl.vanstormbroek.DroidDopje;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BierdopjeDBAdapter {
	public static final String SHOWNAME = "showName";
	public static final String SHOWID = "showId";
	public static final String KEY_ROWID = "_id";
	public static final String TVDBID = "TVDbId";
	public static final String SHOWLINK = "showLink";
	public static final String FIRSTAIRED = "firstAired";
	public static final String LASTAIRED = "lastAired";
	public static final String NEXTEPISODE = "nextEpisode";
	public static final String SEASONS = "seasons";
	public static final String EPISODES = "episodes";
	public static final String GENRES = "genres";
	public static final String SHOWSTATUS = "showStatus";
	public static final String NETWORK = "network";
	public static final String AIRTIME = "airtime";
	public static final String RUNTIME = "runtime";
	public static final String SCORE = "score";
	public static final String FAVORITES = "favorites";
	public static final String HAS_TRANSLATORS = "has_translators";
	public static final String UPDATED = "updated";
	public static final String SUMMARY = "summary";
	//***end show
	public static final String EPISODEID = "episodeid";
	public static final String TITLE = "title";
	public static final String EPISODELINK = "episodelink";
	public static final String SEASON = "season";
	public static final String EPISODE = "episode";
	public static final String EPNUMBER = "epnumber";
	public static final String WIP = "wip";
	public static final String WIPPERCENTAGE = "wippercentage";
	public static final String WIPUSER = "wipuser";
	public static final String VOTES = "votes";
	public static final String FORMATTED = "formatted";
	public static final String AIRDATE = "airdate";
	public static final String IS_SPECIAL = "is_special";
	public static final String SUBSNL = "subsnl";
	public static final String SUBSEN = "subsen";
	//***end episode specific

	//***end of bierdopje XML tags***
	public static final String MYSHOW = "myshow";

	private static final String TAG = "BierdopjeDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "bierdopjedb";
	private static final String DATABASE_TABLE_SHOWS = "shows";
	private static final String DATABASE_TABLE_EPISODES = "episodes";
	private static final int DATABASE_VERSION = 1;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE =
			"create table "+ DATABASE_TABLE_SHOWS +" (_id integer primary key autoincrement, "
					+ SHOWNAME + " text not null, "
					+ SHOWID + " integer not null, "
					+ TVDBID + " integer, "
					+ SHOWLINK + " text, "
					+ FIRSTAIRED + " date, "
					+ LASTAIRED + " date, "
					+ NEXTEPISODE + " date, "
					+ SEASONS + " integer, "
					+ EPISODES + " integer, "
					+ GENRES + " text, "
					+ SHOWSTATUS + " text, "
					+ NETWORK + " text, " 
					+ AIRTIME + " text, "    //9:00 PM
					+ RUNTIME + " integer, " //60
					+ SCORE + " decimal, "
					+ FAVORITES + " integer, "
					+ HAS_TRANSLATORS + " boolean, "
					+ UPDATED + " text, "
					+ SUMMARY + " text, "
					+ MYSHOW + " boolean"
					+ ");"; 

	private static final String DATABASE_CREATE2 =  
			"create table "+ DATABASE_TABLE_EPISODES +" (_id integer primary key autoincrement, "
					+ SHOWNAME + " text not null, "
					+ EPISODEID + " integer not null, "
					+ TITLE + " string, "
					+ TVDBID + " integer, "
					+ SHOWLINK + " text, "
					+ EPISODELINK + " text, "
					+ SEASON + " integer, "
					+ EPISODE + " integer, "
					+ EPNUMBER + " integer, "
					+ WIP + " boolean, "
					+ WIPPERCENTAGE + " string, "
					+ WIPUSER + " string, "				
					+ SCORE + " decimal, "
					+ VOTES + " integer, "
					+ FORMATTED + " string, "
					+ AIRDATE + " string, "
					+ IS_SPECIAL + " boolean, "
					+ SUBSNL + " boolean, "
					+ SUBSEN + " boolean, "
					+ UPDATED + " text, "
					+ SUMMARY + " text"				
					+ ");";


	private final Context mCtx;
	//private final Activity mActivity;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			//DatabaseHelper(Activity activity) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			//super(activity, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE2);
			Log.i(TAG, "DB Created");
			
			//populateWithData(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS shows");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public BierdopjeDBAdapter(Context ctx) {
		this.mCtx = ctx;
		open();
	}

	/**
	 * Open the database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public BierdopjeDBAdapter open() throws SQLException {
		//voor debug doeleinden:
//		Log.w(TAG, "DELETEING PREV DATABASE!!!!");
//		mCtx.deleteDatabase(DATABASE_NAME); 

		mDbHelper = new DatabaseHelper(mCtx);
		Log.i(TAG, "HELPER created");
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	/**
	 * 
	 * @param bdShowList
	 * @return
	 */
	public int insertShowList(BierdopjeShowList bdShowList) {
		for (int i=0; i < bdShowList.shows.size(); i++) {
			insertShow(bdShowList.shows.get(i));
			Log.i(TAG, "inserting " + bdShowList.shows.get(i).showname);
		}
		return bdShowList.size();
	}

	/**
	 * Inserts a show into the database.
	 * 
	 * @param bdShow  class with showinformation
	 * @return rowId or -1 if failed
	 */
	public long insertShow(BierdopjeShow bdShow) {
		if (bdShow == null || bdShow.showid == 0) {
			Log.i(TAG, "Called with nullshow " );
			return -1;
		}
		if (showIsPresent(bdShow.showid)) {
			Log.i(TAG, "episode " + bdShow.showid + " reeds aanwezig");
			return -1;
		}

		
		ContentValues initialValues = new ContentValues();
		initialValues.put(SHOWNAME, bdShow.showname);
		initialValues.put(SHOWID, bdShow.showid);
		initialValues.put(TVDBID, bdShow.tvdbid);
		initialValues.put(SHOWLINK, bdShow.showlink.toString());
		initialValues.put(FIRSTAIRED, bdShow.firstaired);
		initialValues.put(LASTAIRED, bdShow.lastaired);
		initialValues.put(NEXTEPISODE, bdShow.nextepisode);
		initialValues.put(SEASONS, bdShow.seasons);
		initialValues.put(EPISODES, bdShow.episodes);
		if (bdShow.genres.size() > 0) {
			initialValues.put(GENRES, bdShow.genres.get(0));
		}
		initialValues.put(SHOWSTATUS, bdShow.showstatus);
		initialValues.put(NETWORK, bdShow.network);
		initialValues.put(AIRTIME, bdShow.airtime);
		initialValues.put(RUNTIME, bdShow.runtime);
		initialValues.put(SCORE, bdShow.score);
		initialValues.put(FAVORITES, bdShow.favorites);
		initialValues.put(HAS_TRANSLATORS, bdShow.has_translators);
		initialValues.put(UPDATED, bdShow.updated);
		initialValues.put(SUMMARY, bdShow.summary);
		//temp:
		if (bdShow.showid == 69) {
			initialValues.put(MYSHOW, true);
		}

		return mDb.insert(DATABASE_TABLE_SHOWS, null, initialValues);
	}

	private boolean showIsPresent(int showid2) {
		boolean isPresent;
		Cursor mCursor =
				mDb.query(true, DATABASE_TABLE_SHOWS, new String[] {KEY_ROWID}, SHOWID + "=" + showid2, null,
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();	
			isPresent = mCursor.getCount() != 0; 
		} else { 
			isPresent = false;
		}
		mCursor.close();
		return isPresent;
	}

	/**
	 * Create a new show using the params provided. If the show is
	 * successfully created return the new rowId for that show, otherwise return
	 * a -1 to indicate failure.
	 * 
	 * @param name the title of the show
	 * @param bdid the bierdopjeid of the show
	 * @return rowId or -1 if failed
	 */
	public long createShow(String name, int bdid) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(SHOWNAME, name);
		initialValues.put(SHOWID, bdid);

		return mDb.insert(DATABASE_TABLE_SHOWS, null, initialValues);
	}

	/**
	 * Delete the show with the given rowId
	 * 
	 * @param rowId id of show to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteShow(long rowId) {

		return mDb.delete(DATABASE_TABLE_SHOWS, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all shows in the database
	 * 
	 * @return Cursor over all shows
	 */
	public Cursor fetchAllShows() {

		return mDb.query(DATABASE_TABLE_SHOWS, new String[] {KEY_ROWID, SHOWNAME,
				SHOWID}, null, null, null, null, null);
	}
	/**
	 * Return a Cursor positioned at the show that matches the given showId
	 * 
	 * @param rowId of the row to retrieve
	 * @return Cursor positioned to matching show, if found
	 * @throws SQLException if show could not be found/retrieved
	 */
	public Cursor fetchShowByRowId(long rowId) throws SQLException {

		Cursor mCursor =
				mDb.query(true, DATABASE_TABLE_SHOWS, null, KEY_ROWID + "=" + rowId, null,			
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	/**
	 * Return a Cursor positioned at the show that matches the given showId
	 * 
	 * @param showId of show to retrieve
	 * @return Cursor positioned to matching show, if found
	 * @throws SQLException if show could not be found/retrieved
	 */
	public Cursor fetchShow(long showId) throws SQLException {

		Cursor mCursor =
				mDb.query(true, DATABASE_TABLE_SHOWS, null, SHOWID + "=" + showId, null,			
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	/**
	 * Return a Cursor positioned at the episode that matches the given epId
	 * 
	 * @param epId id of episode to retrieve
	 * @return Cursor positioned to matching episode, if found
	 * @throws SQLException if show could not be found/retrieved
	 */
	public Cursor fetchEpisode(int epId) throws SQLException {
		Cursor mCursor =

				mDb.query(true, DATABASE_TABLE_EPISODES, new String[] {KEY_ROWID,
						SHOWNAME, SHOWLINK}, EPISODEID + "=" + epId, null,
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			//this.bdEp = mCursor.
 		}

		return mCursor;

	}

	/**
	 * Update the show using the details provided. The show to be updated is
	 * specified using the rowId, and it is altered to use the title and body
	 * values passed in
	 * 
	 * @param rowId id of show to update
	 * @param bdShow new information for the show
	 * @return true if the show was successfully updated, false otherwise
	 */
	public boolean updateShow(long rowId, BierdopjeShow bdShow) {
		ContentValues args = new ContentValues();
		Log.i(TAG, "param bdshow: " + bdShow.myshow);
		args = showInfoToContentValues(bdShow);
		Log.i(TAG, "van args: " + args.get(MYSHOW));
		return mDb.update(DATABASE_TABLE_SHOWS, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	private ContentValues showInfoToContentValues(BierdopjeShow bdShow) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(SHOWNAME, bdShow.showname);
		initialValues.put(SHOWID, bdShow.showid);
		initialValues.put(TVDBID, bdShow.tvdbid);
		initialValues.put(SHOWLINK, bdShow.showlink.toString());
		initialValues.put(FIRSTAIRED, bdShow.firstaired);
		initialValues.put(LASTAIRED, bdShow.lastaired);
		initialValues.put(NEXTEPISODE, bdShow.nextepisode);
		initialValues.put(SEASONS, bdShow.seasons);
		initialValues.put(EPISODES, bdShow.episodes);
		if (bdShow.genres != null) {
			if (bdShow.genres.size() > 0) {
				initialValues.put(GENRES, bdShow.genres.get(0));
			}
		}
		initialValues.put(SHOWSTATUS, bdShow.showstatus);
		initialValues.put(NETWORK, bdShow.network);
		initialValues.put(AIRTIME, bdShow.airtime);
		initialValues.put(RUNTIME, bdShow.runtime);
		initialValues.put(SCORE, bdShow.score);
		initialValues.put(FAVORITES, bdShow.favorites);
		initialValues.put(HAS_TRANSLATORS, bdShow.has_translators);
		initialValues.put(UPDATED, bdShow.updated);
		initialValues.put(SUMMARY, bdShow.summary);
		initialValues.put(MYSHOW, bdShow.myshow);
		
		return initialValues;
	}

	/**
	 * Inserts an episode into the database.
	 * 
	 * @param bdEpisode  class with episodeinformation
	 * @return rowId or -1 if failed
	 */
	public long insertEpisode(BierdopjeEpisode bdEpisode) {

		if (bdEpisode == null || bdEpisode.episodeid == 0) {
			Log.i(TAG, "Called with nullshow " );
			return -1;
		}
		if (episodeIsPresent(bdEpisode.episodeid)) {
			Log.i(TAG, "episode " + bdEpisode.episodeid + " reeds aanwezig");
			return -1;
		}

		ContentValues initialValues = new ContentValues();
		initialValues.put(SHOWNAME, bdEpisode.showname);
		initialValues.put(EPISODEID, bdEpisode.episodeid);
		initialValues.put(TVDBID, bdEpisode.tvdbid);
		initialValues.put(TITLE, bdEpisode.title);		
		initialValues.put(SHOWLINK, bdEpisode.showlink.toString());
		initialValues.put(EPISODELINK, bdEpisode.episodelink.toString());
		initialValues.put(SEASON, bdEpisode.season);
		initialValues.put(EPISODE, bdEpisode.episode);
		initialValues.put(EPNUMBER, bdEpisode.epnumber);
		initialValues.put(AIRDATE, bdEpisode.airdate);
		initialValues.put(WIP, bdEpisode.wip);
		initialValues.put(WIPPERCENTAGE, bdEpisode.wippercentage);
		initialValues.put(WIPUSER, bdEpisode.wipuser);
		initialValues.put(FORMATTED, bdEpisode.formatted);
		initialValues.put(SUBSNL, bdEpisode.subsnl);
		initialValues.put(SUBSEN, bdEpisode.subsen);
		initialValues.put(SCORE, bdEpisode.score);
		initialValues.put(VOTES, bdEpisode.votes);
		initialValues.put(IS_SPECIAL, bdEpisode.is_special);
		initialValues.put(UPDATED, bdEpisode.updated);
		initialValues.put(SUMMARY, bdEpisode.summary);

		return mDb.insert(DATABASE_TABLE_EPISODES, null, initialValues);
	}


	private boolean episodeIsPresent(int episodeid2) {
		boolean isPresent;
		Cursor mCursor =
				mDb.query(true, DATABASE_TABLE_EPISODES, new String[] {KEY_ROWID}, EPISODEID + "=" + episodeid2, null,
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();	
			isPresent = mCursor.getCount() != 0; 
		} else { 
			isPresent = false;
		}
		mCursor.close();
		return isPresent;
 
	}

	public Cursor getMatchingShows(String constraint) throws SQLException {

		String queryString =
				"SELECT _id, " + SHOWID +", "+ SHOWNAME+ ", " + MYSHOW + " FROM " + DATABASE_TABLE_SHOWS;

		if (constraint != null) {
			// Query for any rows where the state name begins with the
			// string specified in constraint.
			//
			// NOTE:
			// If wildcards are to be used in a rawQuery, they must appear
			// in the query parameters, and not in the query string proper.
			// See http://code.google.com/p/android/issues/detail?id=3153
			constraint = "%" + constraint.trim() + "%";
			queryString += " WHERE showname LIKE ?";
		}
		String params[] = { constraint };

		if (constraint == null) {
			// If no parameters are used in the query,
			// the params arg must be null.
			params = null;
		}
		try {
			Cursor cursor = mDb.rawQuery(queryString, params);
			
			if (cursor != null) {
				Log.i(TAG,"Curosrcount: " + cursor.getCount() + "qs: " + queryString);
				//this.mActivity.startManagingCursor(cursor);
				cursor.moveToFirst();
				return cursor;
			}
			Log.i(TAG,"NO CURSIR");
		}
		catch (SQLException e) {
			Log.e("AutoCompleteDbAdapter", e.toString());
			throw e;
		}

		return null;
	}

	public BierdopjeShow cursorToShow(Cursor c) {
		BierdopjeShow bdShow = new BierdopjeShow();
		if (c != null) {
			c.moveToFirst();
		} else {
			return null;
		}
		bdShow.airtime = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.AIRTIME));
		bdShow.episodes = c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.EPISODES));
		bdShow.favorites = c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.FAVORITES));
		bdShow.firstaired = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.FIRSTAIRED));
		bdShow.has_translators = Boolean.parseBoolean(c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.HAS_TRANSLATORS)));
		bdShow.lastaired = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.LASTAIRED));
		bdShow.myshow =  (c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.MYSHOW))==1);
		bdShow.network = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.NETWORK));
		bdShow.nextepisode = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.NEXTEPISODE));
		bdShow.runtime = c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.RUNTIME));
		bdShow.score = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SCORE));
		bdShow.seasons = c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SEASONS));
		bdShow.showid = c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SHOWID));
		try {
			bdShow.showlink = new URL(c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SHOWLINK)));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bdShow.showname = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SHOWNAME));
		bdShow.showstatus = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SHOWSTATUS));
		bdShow.summary =  c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SUMMARY));
		bdShow.tvdbid = c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.TVDBID));
		bdShow.updated = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.UPDATED));
		
		
		return bdShow;
	}
	
	
	/**
	 * Populates the database with data;
	 * 
	 * @param db
	 *            The database to be populated; must have the appropriate table
	 *            ("state") and columns ("state" and "values") already set up.
	 */
	private void populateWithData(SQLiteDatabase db) {
		try {
			BierdopjeRequest BdRequest = null;
			
			BdRequest = new BierdopjeRequest("GetShowById/1190", null);
			long rowId = insertShow(BdRequest.getShow());
			
			BdRequest = new BierdopjeRequest("GetShowById/69", null);
			rowId = insertShow(BdRequest.getShow());
			
			BdRequest = new BierdopjeRequest("GetShowById/12708", null);
			rowId = insertShow(BdRequest.getShow());
			            
			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}

	public BierdopjeEpisode cursorToEpisode(Cursor c) {
		BierdopjeEpisode bdEp = new BierdopjeEpisode();
		if (c != null) {
			c.moveToFirst();
		} else {
			return null;
		}
		
		bdEp.airdate = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.AIRDATE));
		bdEp.episode = c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.EPISODE));
		bdEp.episodeid = c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.EPISODEID));
		try {
			bdEp.episodelink = new URL(c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.EPISODELINK)));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bdEp.epnumber = c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.EPNUMBER));
		bdEp.formatted = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.FORMATTED));
		bdEp.is_special = Boolean.parseBoolean(c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.IS_SPECIAL)));
		bdEp.score = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SCORE));
		bdEp.season = c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SEASON));
		try {
			bdEp.showlink = new URL(c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SHOWLINK)));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		bdEp.showname = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SHOWNAME));
		bdEp.subsen = Boolean.parseBoolean(c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SUBSEN)));
		bdEp.subsnl = Boolean.parseBoolean(c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SUBSNL)));
		bdEp.summary = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.SUMMARY));
		bdEp.title = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.TITLE));
		bdEp.tvdbid = c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.TVDBID));
		bdEp.updated = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.UPDATED));
		bdEp.votes = c.getInt(c.getColumnIndexOrThrow(BierdopjeDBAdapter.VOTES));
		bdEp.wip = Boolean.parseBoolean(c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.WIP)));
		bdEp.wippercentage = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.WIPPERCENTAGE));
		bdEp.wipuser = c.getString(c.getColumnIndexOrThrow(BierdopjeDBAdapter.WIPUSER));
		
		return bdEp;
	}


}