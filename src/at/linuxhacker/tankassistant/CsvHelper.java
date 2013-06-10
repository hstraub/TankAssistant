package at.linuxhacker.tankassistant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class CsvHelper {
	
	private static String C_CSV_DIRNAME = "tankassistant";
	private static String C_CSV_FILENAME = "tankassistant.csv";
	private static int C_MAX_BACKUP_FILE_VERSIONS = 10;
	
	private Context context;

	public CsvHelper( Context context ) {
		this.context = context;
	}

    public void csvExport( ) {
    	int i = 0;
    	Date now = new Date( );
    	SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd_HHmm" );
    	String filenamePrefix = new String( format.format( now ) );
    	
    	String directoryname = Environment.getExternalStorageDirectory( ) + File.separator + C_CSV_DIRNAME;
    	File directory = new File( directoryname );
    	directory.mkdirs( );
    	String filename = directoryname + File.separator + filenamePrefix + "-" + C_CSV_FILENAME;
    	CSVWriter writer;
    	DbHelper dbHelper = new DbHelper( this.context );
    	SQLiteDatabase db = dbHelper.getReadableDatabase( );
    	Cursor cursor = db.query( DbHelper.TABLE,
    			null, null, null, null, null, null );
    	cursor.moveToFirst( );
    	
    	try {
    		writer = new CSVWriter(
    				new FileWriter( filename ) );

    		while( cursor.isAfterLast( ) == false ) {
    			String[] values = { 
    			        cursor.getString( cursor.getColumnIndex( DbHelper.C_TIMESTAMP ) ),
    			        cursor.getString( cursor.getColumnIndex( DbHelper.C_KILOMETERSTAND ) ),
    			        cursor.getString( cursor.getColumnIndex( DbHelper.C_PREIS ) ),
    			        cursor.getString( cursor.getColumnIndex( DbHelper.C_LITER ) ),
    			        cursor.getString( cursor.getColumnIndex( DbHelper.C_POS_LONG ) ),
    			        cursor.getString( cursor.getColumnIndex( DbHelper.C_POS_LAT ) ),
    			        cursor.getString( cursor.getColumnIndex( DbHelper.C_POS_ACC ) ),
    			        cursor.getString( cursor.getColumnIndex( DbHelper.C_POS_FIXAGE ) )
    			};

	    		writer.writeNext( values );
	    		cursor.moveToNext( );
	    		i++;
    		}
    		writer.close( ); 
    	} catch ( Exception e ) {
    		Toast toast = Toast.makeText( this.context, "Fehler: " + e.getMessage( ), Toast.LENGTH_LONG );
    		toast.show( );
    	}
    	Toast toast = Toast.makeText( this.context, "Export von " + i + " Records ins File: "
    			+ filename, Toast.LENGTH_LONG );
    	toast.show( );
    	
    	// Cleanup Directory
    	String[] filenames = directory.list( );
    	Arrays.sort( filenames );
    	if ( filenames.length > 10 ) {
    		for ( i = 0; i <filenames.length - C_MAX_BACKUP_FILE_VERSIONS;
    				i++ ) {
    			new File( directory, filenames[i] ).delete( );
    		}
    	}
    }

//    	public void csvImport( ) {
//        	DbHelper dbHelper;
//        	SQLiteDatabase db;
//        	int i = 0;
//        	String directoryname = Environment.getExternalStorageDirectory( ) + File.separator + C_CSV_DIRNAME;
//        	File directory = new File( directoryname );
//        	String[] filenames = directory.list( );
//        	Arrays.sort( filenames );
//        	String filename = directoryname + File.separator + filenames[filenames.length - 1];	
//
//        	dbHelper = new DbHelper( this.context );
//        	db = dbHelper.getWritableDatabase( );
//        	db.delete(DbHelper.TABLE, "", null);    	
//        	try {
//    	    	CSVReader reader = new CSVReader(
//    	    			new FileReader( filename ) );
//    	    	String [] nextLine;
//    	    	while( ( nextLine = reader.readNext( ) ) != null ) {
//    	    		ContentValues values = new ContentValues( );
//    	    		values.put( DbHelper.C_DATETIME, nextLine[0] );
//    	    		values.put( DbHelper.C_GEWICHT, nextLine[1] );
//    	    		db.insertOrThrow( DbHelper.TABLE, null, values );
//    	    		i++;
//    	    	}
//        	} catch ( Exception e ){
//        		Toast toast = Toast.makeText( this, "Fehler: " + e.getMessage( ), Toast.LENGTH_LONG );
//        		toast.show( );
//            }
//        	db.close( );
//
//        	Toast.makeText( this, "Import von " + i + " Record von File: "
//        			+ filename, Toast.LENGTH_LONG ).show( );
//        	    	
//        }
//
//    	
//    }
	
	
}
