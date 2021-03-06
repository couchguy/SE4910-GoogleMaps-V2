package com.example.googlemapsv2;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;

public class MainActivity extends Activity {
	Criteria criteria;
	LocationManager locationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// obtain reference to LocationManager
		
		
		String svcName = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(svcName);
		
		//Set location criteria
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);
		
		// Obtain reference to location provider
		String provider = locationManager.getBestProvider(criteria, true);
		Location l = locationManager.getLastKnownLocation(provider);
		updateWithNewLocation(l);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		String provider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
	}
	
	@Override
	public void onDestroy(){
		locationManager.removeUpdates(locationListener);
	}

	private void updateWithNewLocation(Location location) {
		TextView myLocationText;
		myLocationText = (TextView) findViewById(R.id.myLocationText);
		String latLongString = "No location found";
		String addressString = "No address found";
		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			
			GoogleMap mMap=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
			mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
			mMap.animateCamera( CameraUpdateFactory.zoomTo(17.0f));
			
			mMap.addMarker(new MarkerOptions()
				.position(new LatLng(latitude, longitude))
				.title("Current Position"));
			
			
			
			latLongString = "Lat:" + latitude + "\nLong:" + longitude;
			Geocoder gc = new Geocoder(this, Locale.getDefault());
			if (!Geocoder.isPresent())
				addressString = "No geocoder available";
			else {
				try {
					List<Address> addresses = gc.getFromLocation(latitude,
							longitude, 1);
					StringBuilder sb = new StringBuilder();
					if (addresses.size() > 0) {
						Address address = addresses.get(0);
						for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
							sb.append(address.getAddressLine(i)).append("\n");
						sb.append(address.getLocality()).append("\n");
						sb.append(address.getPostalCode()).append("\n");
						sb.append(address.getCountryName());
					}
					addressString = sb.toString();
				} catch (IOException e) {
					Log.d("TRACK ME", "IO Exception", e);
				}
			}
		}
		myLocationText.setText("Your Current Position is:\n" + latLongString
				+ "\n\n" + addressString);
	}


private final LocationListener locationListener = new LocationListener() {
	@Override
	public void onLocationChanged(Location location) {
		updateWithNewLocation(location);
	}
	@Override
	public void onProviderDisabled(String arg0) {}
	@Override
	public void onProviderEnabled(String arg0) {}
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
};
}