package at.linuxhacker.tankassistant;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class LicenseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView( R.layout.license_view );
		WebView licenseView = (WebView) findViewById( R.id.licenseView );
		licenseView.loadUrl( "file:///android_asset/licence.html" );
	}

}
