package nl.vanstormbroek.DroidDopje;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MyShows extends ListActivity {
	BierdopjeDBAdapter mDbHelper;
	private static final String TAG = "MyShows";
	Cursor cur;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.listitem);
		super.onCreate(savedInstanceState);
		mDbHelper = new BierdopjeDBAdapter(this);
		cur = mDbHelper.getMatchingShows(null);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.listitementry,
				cur,
				new String[] {BierdopjeDBAdapter.SHOWNAME,BierdopjeDBAdapter.MYSHOW},
				new int[] { R.id.name_entry, R.id.imgFav});

		adapter.setViewBinder(new MyViewBinder());

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Cursor c = mDbHelper.fetchShowByRowId(id);
		BierdopjeShow bdShow = mDbHelper.cursorToShow(c);
		bdShow.myshow = !bdShow.myshow;
		mDbHelper.updateShow(id, bdShow);
		c.close();
		//update list:
		cur.requery();
	}
	
	private class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			int viewId = view.getId();
			switch(viewId) {
			case R.id.name_entry:
				TextView showNameView = (TextView) view;
				showNameView.setText(cursor.getString(columnIndex));
				break;
			case R.id.imgFav:
				ImageView showFavIcon = (ImageView) view;

				boolean showFavType = (cursor.getInt(columnIndex) == 1);
				if (showFavType) {
					showFavIcon.setImageResource(android.R.drawable.btn_star_big_on);
				} else {
					showFavIcon.setImageResource(android.R.drawable.btn_star_big_off);
				}

				break;
			}
			return true;
		}

	}
}