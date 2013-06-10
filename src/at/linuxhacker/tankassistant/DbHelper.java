package at.linuxhacker.tankassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.Toast;

public class DbHelper extends SQLiteOpenHelper {
	static String TAG = "DbHelper";
	static String DB_NAME = "tanken.db";
	static int DB_VERSION = 4;
	static String TABLE = "getankt";
	static String C_ID = BaseColumns._ID;
	static String C_TIMESTAMP = "tankzeitpunkt";
	static String C_PREIS = "preis";
	static String C_LITER = "liter";
	static String C_KILOMETERSTAND = "kilometerstand";
	static String C_POS_LAT = "latitude";
	static String C_POS_LONG = "longitude";
	static String C_POS_ACC = "accuracy";
	static String C_POS_FIXAGE = "fixage";
	Context context;

	DbHelper( Context context ) {
		super( context, DB_NAME, null, DB_VERSION );
		this.context = context;
	}
	@Override
	public void onCreate( SQLiteDatabase db ) {
		String sql = "create table " + TABLE + " ( " +
			C_ID + " integer primary key autoincrement, " +
			C_TIMESTAMP + " text, " +
			C_PREIS + " real, " +
			C_LITER + " real, " +
			C_KILOMETERSTAND + " real, " +
			C_POS_LAT + " real, " +
			C_POS_LONG + " real, " +
			C_POS_ACC + " real, " +
			C_POS_FIXAGE + " int" +
			" )";
		db.execSQL( sql );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if ( oldVersion < 4 ) {
			String sql = "drop table " + TABLE;
			db.execSQL( sql );
			this.onCreate( db );
		}
	}

}
