package at.linuxhacker.tankassistant;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class TankAssitantActivity extends Activity implements LocationListener {
	private double posLat = 0;
	private double posLong = 0;
	private float posAccuracy = 0;
	private double posFixAge = 0;
	private Location oldLocation = null;
	private Location actualLocation = null;
	private LocationManager locationManager;
	private List<String> providers;
	private String bestProvider;
	private Geocoder geocoder;
	private DecimalFormat positionFormat = new DecimalFormat( "###.0000" );
	private Button buttonSave;
	private TextView textPreis;
	private TextView textLiter;
	private TextView textKilometer;
	private Button buttonSucheTankstelle;
	private Button buttonDisplayData;
	private SimpleDateFormat timestampFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
        
		this.initiateButtonsAndTextFields( );
    	this.initiateLocationThings( );
    	this.setListeners( );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tankassistent_main, menu);
        return true;
    }
 
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        
        CsvHelper csvHelper = new CsvHelper( this );
        
        switch( item.getItemId( ) ) {
    	case R.id.action_license_view:
    		Intent license = new Intent( this, LicenseActivity.class );
    		startActivity( license );
    		return true;
        case R.id.itemBackup:
            csvHelper.csvExport( );
            return true;
        case R.id.itemRestore:
            csvHelper.csvExport( );
            return true;
    	default:
    		return false;
    	}

    }
    
    private void initiateLocationThings( ) {

    	this.locationManager =
        		( LocationManager ) getSystemService( Context.LOCATION_SERVICE );
        this.geocoder = new Geocoder( this );
        Criteria criteria = new Criteria( );
        criteria.setAccuracy( Criteria.ACCURACY_LOW );
    	this.providers = this.locationManager.getProviders( true );
    	/*
    	Toast.makeText( this, "Provider: " + providers,
    			Toast.LENGTH_LONG ).show( );
    	*/
    	Location location = this.getLastKnownLocation( );
    	if ( location != null ) {
    		this.updatePosition( location );
    	}
    	for ( String provider : providers ) {
    		this.locationManager.requestLocationUpdates( provider , 0, 0, this );
    	}  	
    }
    
    Location getLastKnownLocation( ) {
    	List<Location> locations = new ArrayList<Location>( );
    	Location location = null;
    	for ( String provider : this.providers ) {
    		location = this.locationManager.getLastKnownLocation( provider );
    		/*
        	Toast.makeText( this, "LastLocation Provider: " + provider + ": " + location.getAccuracy( ),
        			Toast.LENGTH_SHORT ).show( );
        	*/
        	if ( location != null ) {
        		locations.add( location );
        	}
     	}
    	location = this.findBestLocation( locations );
    	return location;
    }
    
    Location findBestLocation( List<Location> locations ) {
    	Location bestLocation = null;
    	for ( Location location : locations) {
    		if ( bestLocation == null ) {
    			bestLocation = location;
    			continue;
    		}
    		if ( location.getAccuracy( ) < bestLocation.getAccuracy( ) && 
    				location.getTime( ) > bestLocation.getTime( ) ) {
    			bestLocation = location;
    			this.bestProvider = location.getProvider( );
    		}
    	}
    	/*
    	Toast.makeText( this, "Best Location Provider: " + 
    			bestLocation.getProvider( ),
    			Toast.LENGTH_LONG ).show( );
    	*/
   	
    	return bestLocation;
    }
    
    private void initiateButtonsAndTextFields ( ) {
        
    	this.textKilometer = ( TextView ) findViewById( R.id.editKilometer );
		this.textPreis = ( TextView ) findViewById( R.id.editPreis );
		this.textLiter = ( TextView ) findViewById( R.id.editLiter );
    	this.buttonSave = ( Button ) findViewById( R.id.buttonSave );
    	this.buttonSave.setEnabled( false );
        this.buttonSucheTankstelle = ( Button )
        		findViewById( R.id.buttonSucheTankstelle );
        this.buttonDisplayData = ( Button ) findViewById( R.id.buttonDisplayData );
    	
    }
    
    protected void updatePosition( Location location ) {
    	/*
    	Toast.makeText( this, "Update Position " + location.getProvider( ),
    			Toast.LENGTH_SHORT ).show( );
    	*/
    	if ( this.actualLocation != null ) {
    		this.oldLocation = this.actualLocation;
    	}
    	this.actualLocation = location;
    	this.posLat = location.getLatitude( );
    	this.posLong = location.getLongitude( );
    	this.posAccuracy = location.getAccuracy( );
    	long fixTime = location.getTime( );
    	Date now = new Date( );
    	this.posFixAge = ( now.getTime( ) - fixTime ) / 1000;
    	String diffTime;
    	if ( this.posFixAge > 600 ) {
    		diffTime = String.valueOf( (int) this.posFixAge / 60 ) + "m"; 
    	} else {
    		diffTime = String.valueOf( ( int ) this.posFixAge ) + "s";
    	}
    	
		TextView posLong = ( TextView ) findViewById( R.id.text_gpsLong );
		TextView posLat = ( TextView ) findViewById( R.id.text_gpsLat );
		TextView posAcc = ( TextView ) findViewById( R.id.text_gpsAcc );
		TextView posProvider = ( TextView ) findViewById( R.id.text_gpsProvider );
		TextView posFixTime = ( TextView ) findViewById( R.id.text_gpsFixTime );
		posLong.setText( this.positionFormat.format( this.posLong ) + "°" );
		posLat.setText( this.positionFormat.format( this.posLat ) + "°" );
		posAcc.setText( String.valueOf( this.posAccuracy ) + "m" );
		posProvider.setText( location.getProvider( ) );
		posFixTime.setText( diffTime );
		
		this.initiateGeocodeReverseLookup( );
    }
    
    protected void initiateGeocodeReverseLookup( ) {
    	if ( this.actualLocation == null ) {
    		return;
    	}
    	if ( this.oldLocation != null &&
    			this.oldLocation.distanceTo( this.actualLocation )  < this.posAccuracy ) {
    		return;
    	}
    	new GeocodingReverseLookupTask( ).execute( this.actualLocation );
    }
    
    @Override
    protected void onResume( ) {
    	super.onResume( );
    	for ( String provider : this.providers ) {
    		this.locationManager.requestLocationUpdates( provider, 0, 0, this );
    	}
    	this.getLastKnownLocation( );
    }
    
    @Override
    protected void onPause( ) {
    	super.onPause( );
    	this.locationManager.removeUpdates( this );
    }
    
    @Override
    public void onLocationChanged( Location location ) {
    	this.updatePosition( location );
    }
    
    @Override
    public void onStatusChanged( String provider, int status, Bundle extras ) {
    	// TODO: was damit?
    }
    
    @Override
    public void onProviderEnabled( String provider ) {
    	this.providers.add( provider );
    	Toast.makeText( this, "Neuer Provider: " + provider,
    			Toast.LENGTH_LONG ).show( );
    }
    
    @Override
    public void onProviderDisabled( String provider ) {
    	Toast.makeText( this, "Provider weg: " + provider,
    			Toast.LENGTH_LONG ).show( );    	
    }
    
    private void setListeners( ) {
    	
    	TextWatcher textWatcher = new TextWatcher( ) {

    		@Override
    		public void onTextChanged( CharSequence s , int start, int before, int count ) {
    			TankAssitantActivity.this.checkTextFieldStatesAndSetSaveButton( );
    		}
    		
    		@Override
    		public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    		}

    		@Override
    		public void afterTextChanged( Editable s ) {

    		}
  		
    	};

    	this.textKilometer.addTextChangedListener( textWatcher );
    	this.textPreis.addTextChangedListener( textWatcher );
    	this.textLiter.addTextChangedListener( textWatcher );

    	this.buttonSave.setOnClickListener( new View.OnClickListener( ) {
			
			@Override
			public void onClick(View v) {
				DbHelper dbHelper = new DbHelper( TankAssitantActivity.this );
				SQLiteDatabase db = dbHelper.getWritableDatabase( );
				
				ContentValues values = new ContentValues( );
				values.put( DbHelper.C_TIMESTAMP,
					TankAssitantActivity.this.timestampFormat.format( new Date( ) ) );
				values.put( DbHelper.C_KILOMETERSTAND,
					Double.parseDouble(
						TankAssitantActivity.this.textKilometer.getText( ).toString( ) ) );
				values.put( DbHelper.C_PREIS,
					Double.parseDouble(
						TankAssitantActivity.this.textPreis.getText( ).toString( ) ) );
				values.put( DbHelper.C_LITER,
					Double.parseDouble(
						TankAssitantActivity.this.textLiter.getText( ).toString( ) ) );
				values.put( DbHelper.C_POS_LAT,
					TankAssitantActivity.this.posLat );
				values.put( DbHelper.C_POS_LONG,
					TankAssitantActivity.this.posLong );
				values.put( DbHelper.C_POS_ACC,
					TankAssitantActivity.this.posAccuracy );
				values.put( DbHelper.C_POS_FIXAGE,
					TankAssitantActivity.this.posFixAge );
				
				db.insertOrThrow( dbHelper.TABLE, null, values );
				db.close( );
				
				TankAssitantActivity.this.textKilometer.setText( "" );
				TankAssitantActivity.this.textPreis.setText( "" );
				TankAssitantActivity.this.textLiter.setText( "" );
				TankAssitantActivity.this.buttonSave.setEnabled( false );
				
				Toast.makeText( TankAssitantActivity.this, "Gespeichert...", Toast.LENGTH_LONG ).show( );
			}
		} );
    	
        this.buttonSucheTankstelle.setOnClickListener ( new View.OnClickListener( ) {
			@Override
			public void onClick(View v) {
				Intent sucheTankstelle = new Intent( Intent.ACTION_VIEW,
						Uri.parse( "http://mobile.spritpreisrechner.at/location.html")
						);
				startActivity( sucheTankstelle );
				
			}
		} );
        

        this.buttonDisplayData.setOnClickListener( new View.OnClickListener( ) {
        	@Override
        	public void onClick( View v ) {
        		startActivity( new Intent( TankAssitantActivity.this,
            			DisplayData.class ) );
        	}
        } );
       
    }
    
    private void checkTextFieldStatesAndSetSaveButton( ) {
    	if ( this.textKilometer.length( ) > 0 && 
    			this.textLiter.length( ) > 0 && 
    			this.textPreis.length( ) > 0 ) {
    		this.buttonSave.setEnabled( true );
    	} else {
    		this.buttonSave.setEnabled( false );
    	}
    }
    
    private class GeocodingReverseLookupTask extends AsyncTask<Location, Void, String> {
    	protected String doInBackground( Location... locations ) {
    		// Runs on own thread
    		String myAddress = "";
        	try {
        		List<Address> addresses =
        				TankAssitantActivity.this.geocoder.getFromLocation( 
        						locations[0].getLatitude( ), 
        						locations[0].getLongitude( ),
        						1 );
        		if ( addresses.size( ) > 0 ) {
        			myAddress = addresses.get( 0 ).getAddressLine( 1 ) +
        					", " +
        					addresses.get( 0 ).getAddressLine( 0 );
        		}
        	} catch ( Exception e ) {
            	/*
        		Toast.makeText( this, "Exception in updateAddress: " + e,
            			Toast.LENGTH_LONG ).show( );
            	*/    		
        	}
    		return myAddress;
    	}
    	
    	protected void  onPostExecute( String myAddress ) {
    		// Runs on GUI thread
			TextView address = ( TextView ) findViewById( R.id.text_Adresse );
			address.setText( myAddress );

    	}
    }
}