package at.linuxhacker.tankassistant;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class DisplayData extends ListActivity {
	
	private static final int DIALOG_ID = 99;
	private static final String fields [] = {
		DbHelper.C_TIMESTAMP,
		DbHelper.C_KILOMETERSTAND,
		DbHelper.C_LITER,
		DbHelper.C_PREIS,
		DbHelper.C_ID
	};
	private CursorAdapter dataSource;
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		DbHelper dbHelper = new DbHelper( this );
		SQLiteDatabase db = dbHelper.getReadableDatabase( );
		Cursor data = db.query( dbHelper.TABLE, this.fields, null, null, null, null, null );
		
		this.dataSource = new SimpleCursorAdapter( this, R.layout.displaydata, data, this.fields, new int[] { R.id.displayDataTimestamp, R.id.displayDataKilometer, R.id.displayDataLiter, R.id.displayDataPreis } );
		ListView view = getListView( );
		view.setHeaderDividersEnabled( true );
		view.addHeaderView( getLayoutInflater( ).inflate( R.layout.displaydata, null));
		setListAdapter ( dataSource );
	}
}
