package com.triladroid.gps;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements GoogleMap.OnMyLocationChangeListener{
  private TextView latituteField;
  private TextView longitudeField;
  private TextView markerLat;
  private TextView markerLng;
  private Button findMeButton;
  private GoogleMap map;
  private Location mylocationlocation;
  private Marker customlocationmarker;
  private String sharetext;
  private LatLng pointt;
  private SharedPreferences.OnSharedPreferenceChangeListener prefList;
  private String messageString;
  private String messageStringMarker;
  private int n;
  
  //private Location gpslocation;

  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    latituteField = (TextView) findViewById(R.id.TextView02);
    longitudeField = (TextView) findViewById(R.id.TextView04);

      final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);


      SetUpMapIfNeeded();

      boolean showZoom = preferences.getBoolean("zoomPref", false);
      View zoomLayout = findViewById(R.id.zoomLayout);

      if (showZoom)
      {
          zoomLayout.setVisibility(View.VISIBLE);
      }
      else
      {
          zoomLayout.setVisibility(View.GONE);
      }


      String  ss = preferences.getString("shareMessage", "3");
      n = Integer.valueOf(ss);

          prefList = new SharedPreferences.OnSharedPreferenceChangeListener() {
          @Override
          public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
              if (s.equals("listPref"))
              {
                  String  mapType = preferences.getString("listPref", "1");
                  map.setMapType(Integer.valueOf(mapType));

                  if (mapType.equals("2") || mapType.equals("4"))
                  {
                      ((TextView) findViewById(R.id.TextView02)).setTextColor(Color.WHITE);
                      ((TextView) findViewById(R.id.TextView04)).setTextColor(Color.WHITE);
                      ((TextView) findViewById(R.id.TextView05)).setTextColor(Color.WHITE);
                      ((TextView) findViewById(R.id.TextView06)).setTextColor(Color.WHITE);

                  }
                  else
                  {
                      ((TextView) findViewById(R.id.TextView02)).setTextColor(Color.BLACK);
                      ((TextView) findViewById(R.id.TextView04)).setTextColor(Color.BLACK);
                      ((TextView) findViewById(R.id.TextView05)).setTextColor(Color.BLACK);
                      ((TextView) findViewById(R.id.TextView06)).setTextColor(Color.BLACK);

                  }

              }

              if (s.equals("zoomPref"))
              {
                  boolean showZoom = preferences.getBoolean("zoomPref", false);
                  View zoomLayout = findViewById(R.id.zoomLayout);

                  if (showZoom)
                  {
                      zoomLayout.setVisibility(View.VISIBLE);
                  }
                  else
                  {
                      zoomLayout.setVisibility(View.GONE);
                  }

              }

              if (s.equals("shareMessage")) {
                  String messageType = preferences.getString("shareMessage", "3");
                  n = Integer.valueOf(messageType);
              }
          }
      };
     preferences.registerOnSharedPreferenceChangeListener(prefList);


    //share
    Button getButton = (Button)findViewById(R.id.sh);
    getButton.setOnClickListener(ShareListener);
    //share

    //share marker
      Button shareMarkerButton = (Button)findViewById(R.id.sh_marker);
      shareMarkerButton.setOnClickListener(ShareMarkerListener);
    //share marker

    //find me
    findMeButton = (Button) findViewById(R.id.location);
    findMeButton.setOnClickListener(FindMeListener);

    findMeButton.getBackground().setVisible(true, true);

    //find me

    //settings
    Button settingsButton = (Button) findViewById(R.id.settings);
    settingsButton.setOnClickListener(SettingsListener);
    //settings



    //ads
    AdView ad = (AdView) findViewById(R.id.adView);
    ad.loadAd(new AdRequest.Builder().build());
    //ads

  }

    @Override
    public void onResume()
    {
        super.onResume();
        SetUpMapIfNeeded();
    }

  private OnClickListener ShareListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {

          if (mylocationlocation != null) {
              TextView latitudetext = (TextView) findViewById(R.id.TextView02);
              TextView longitutetext = (TextView) findViewById(R.id.TextView04);
              String strlatitude = latitudetext.getText().toString();
              String strlongitute = longitutetext.getText().toString();

              Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
              emailIntent.setType("text/plain");

              if (n == 1) {
                  sharetext = getString(R.string.linkMap) + " " + "http://maps.google.com/maps?&z=10&q=" + mylocationlocation.getLatitude()
                          + "+" + mylocationlocation.getLongitude() + "(Pool+Location)&mrt=yp";
              }
              if (n == 2) {
                  sharetext = getString(R.string.I_am_at_this) + " " + strlatitude + " " + getString(R.string.and_this) + " " + strlongitute;
              }
              if (n == 3) {
                  sharetext = getString(R.string.I_am_at_this) + " " + strlatitude + " " + getString(R.string.and_this) +
                          " " + strlongitute + " " + getString(R.string.and_this_is_link_to_the_map) + " " +
                          "http://maps.google.com/maps?&z=10&q=" + mylocationlocation.getLatitude() + "+" +
                          mylocationlocation.getLongitude() + "(Pool+Location)&mrt=yp";
              }


              emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, sharetext);

              startActivity(emailIntent);
          }
            else
          {
              Toast.makeText(MainActivity.this, getString(R.string.determining_location), Toast.LENGTH_SHORT).show();
          }
		}  
	};

    private OnClickListener ShareMarkerListener = new OnClickListener()
    {


        @Override
        public void onClick(View v) {

            TextView latitudetextM = (TextView)findViewById(R.id.TextView05);
            TextView longitutetextM = (TextView)findViewById(R.id.TextView06);
            String strlatitudeM = latitudetextM.getText().toString();
            String strlongituteM = longitutetextM.getText().toString();
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("text/plain");

            if (n==1)
            {
                sharetext = getString(R.string.I_want_to_share_this_location_with_you) + " " +
                        "http://maps.google.com/maps?&z=10&q=" + pointt.latitude + "+" + pointt.longitude +  "(Pool+Location)&mrt=yp";
            }
            if (n==2)
            {
                sharetext = getString(R.string.I_want_to_share_this_location_with_you) + " " + getString(R.string.Latitude)
                        + MyConvert(pointt.latitude)  + " " + getString(R.string.Longitude) + MyConvert(pointt.longitude);
            }
            if (n==3)
            {
                sharetext = getString(R.string.I_want_to_share_this_location_with_you) + " "
                        + getString(R.string.Latitude)+ " " + MyConvert(pointt.latitude)  + " "
                        + getString(R.string.Longitude) + " " + MyConvert(pointt.longitude) + "" +
                        " "+getString(R.string.and_this_is_link_to_the_map) + " " + "http://maps.google.com/maps?&z=10&q="
                        + pointt.latitude + "+" + pointt.longitude+  "(Pool+Location)&mrt=yp";

            }


            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, sharetext);
            startActivity(emailIntent);

        }
    };

    private void zoom(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 17));
    }

 
	private OnClickListener FindMeListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//map.clear();
			if (customlocationmarker != null)
			{
				customlocationmarker.remove();
				customlocationmarker = null;
                findViewById(R.id.share_marker_layout).setVisibility(View.GONE);

			}
			//mylocation = new LatLng(latitude, longitude);
		    //mylocationmarker.setPosition(mylocation);
		    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 17));

            if (mylocationlocation == null) {
                Toast.makeText(MainActivity.this, getString(R.string.determining_location), Toast.LENGTH_SHORT).show();
            } else {
                zoom(mylocationlocation);
            }
		}  
	};
	
	private GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener()
	{
		@Override
        public void onMapClick(LatLng point) {
		//map.clear();
			if (customlocationmarker != null)
			{
		customlocationmarker.remove();
		customlocationmarker = null;
		
		
			}
		customlocationmarker = map.addMarker(new MarkerOptions()
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
		.position(point));
		
		
		customlocationmarker.setPosition(point);
		pointt = point;
		
		String markerLatText = getString(R.string.Marker_Latitude) + " " + MyConvert(pointt.latitude);
        String markerLngText = getString(R.string.Marker_Longitude) + " " + MyConvert(pointt.longitude);
		
		markerLat = (TextView) findViewById(R.id.TextView05);
		markerLat.setText(markerLatText);

		markerLng = (TextView) findViewById(R.id.TextView06);
		markerLng.setText(markerLngText);

		findViewById(R.id.share_marker_layout).setVisibility(View.VISIBLE);
		
		}
	
	};
	
	private GoogleMap.OnMarkerClickListener blueMarkerListener = new GoogleMap.OnMarkerClickListener()
	{

		@Override
		public boolean onMarkerClick(Marker arg0) {
			if (customlocationmarker != null)
		    {
                customlocationmarker.remove();
                customlocationmarker = null;

                findViewById(R.id.share_marker_layout).setVisibility(View.GONE);
		    }
			return false;
		}
		
		
	};

    private OnClickListener SettingsListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new PrefActivity())
                    .addToBackStack("settings")
                    .commit();

        }
    };

	private String MyConvert(double value)
	{
//		String converttosec = Location.convert(value , Location.FORMAT_SECONDS);
//
//		 if (converttosec.indexOf('.') != -1)
//		    {
//			 converttosec = converttosec.substring(0, converttosec.indexOf('.'));
//		    }
//
//		 return converttosec;

        int deg = (int) value;
        value = Math.abs((value - deg)*60);
        int min = (int) value;
        value = (value - min)*60;
        int sec = (int) value;
        return String.format("%d°%02d'%02d\"", deg, min, sec);
		
	};

    private void initZoomButtons() {
        final Button zoomInButton = (Button) findViewById(R.id.zoom_in);
        final Button zoomOutButton = (Button) findViewById(R.id.zoom_out);

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                zoomInButton.setEnabled(map.getCameraPosition().zoom < map.getMaxZoomLevel());
                zoomOutButton.setEnabled(map.getCameraPosition().zoom > map.getMinZoomLevel());
            }
        });
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (mylocationlocation == null) {
            findMeButton.setBackgroundResource(R.drawable.button_location_found);
            zoom(location);
        }

        mylocationlocation = location;

        String strLongitude = getString(R.string.Longitude) + " " + MyConvert(location.getLongitude());
        String strLatitude = getString(R.string.Latitude) + " " + MyConvert(location.getLatitude());

        latituteField.setText(strLatitude);
        longitudeField.setText(strLongitude);
    }

    private void SetUpMapIfNeeded()
    {
        if (map==null)
        {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            if (map!=null)
            {
                MapsInitializer.initialize(this);
                initZoomButtons();
                map.setMyLocationEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);

                map.setOnMyLocationChangeListener(this);

                // map type
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String  mapType = preferences.getString("listPref", "1");
                map.setMapType(Integer.valueOf(mapType));
                // map type

                if (mapType.equals("2") || mapType.equals("4"))
                {
                    ((TextView) findViewById(R.id.TextView02)).setTextColor(Color.WHITE);
                    ((TextView) findViewById(R.id.TextView04)).setTextColor(Color.WHITE);
                    ((TextView) findViewById(R.id.TextView05)).setTextColor(Color.WHITE);
                    ((TextView) findViewById(R.id.TextView06)).setTextColor(Color.WHITE);

                }
                else
                {
                    ((TextView) findViewById(R.id.TextView02)).setTextColor(Color.BLACK);
                    ((TextView) findViewById(R.id.TextView04)).setTextColor(Color.BLACK);
                    ((TextView) findViewById(R.id.TextView05)).setTextColor(Color.BLACK);
                    ((TextView) findViewById(R.id.TextView06)).setTextColor(Color.BLACK);

                }

                //map listener
                map.setOnMapClickListener(mapClickListener);
                //map listener

                //marker listener
                map.setOnMarkerClickListener(blueMarkerListener);
                //marker listener
             }
        }
    }
}
