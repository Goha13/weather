package com.example.kagis.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
MyTask myTask;
MyTask myTask2;
Marker marker;
private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?q=Almaty,KZ&units=metric";
    private static final String OPEN_WEATHER_MAP_API2 =
            "http://api.openweathermap.org/data/2.5/weather?q=Balkhash,KZ&units=metric";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myTask=new MyTask();
        myTask.execute(OPEN_WEATHER_MAP_API);
        myTask2=new MyTask();
        myTask2.execute(OPEN_WEATHER_MAP_API2);

    }
class MyTask extends AsyncTask<String,Void,JSONObject>{

        @Override
    protected void onPreExecute(){
            super.onPreExecute();

        }
        @Override
    protected JSONObject doInBackground(String ... urls){
            try {
                URL url=new URL(urls[0]);
//                URL url = new URL(String.format(OPEN_WEATHER_MAP_API, "Balkhash,KZ"));
                HttpURLConnection connection =
                        (HttpURLConnection)url.openConnection();

                connection.addRequestProperty("x-api-key",
                        MapsActivity.this.getString(R.string.open_weather_maps_app_id));

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuffer json = new StringBuffer(1024);
                String tmp="";
                while((tmp=reader.readLine())!=null)
                    json.append(tmp).append("\n");
                reader.close();

                JSONObject data = new JSONObject(json.toString());

                // This value will be 404 if the request was not
                // successful
                if(data.getInt("cod") != 200){
                    return null;
                }

                return data;
            }catch(Exception e){
                return null;
            }
        }
        @Override
    protected void onPostExecute(JSONObject jsonObject){
        super.onPostExecute(jsonObject);
        if(jsonObject!=null){
            try{
                String lon=jsonObject.getJSONObject("coord").getString("lon");
                String lat=jsonObject.getJSONObject("coord").getString("lat");
//                String place=jsonObject.getJSONObject("sys").getString("name");
                String temp=jsonObject.getJSONObject("main").getString("temp");
                String pressure=jsonObject.getJSONObject("main").getString("pressure");
                JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);
                int weatherId=details.getInt("id")/100;
                String icon="";
                switch (weatherId){
                    case 8 : icon=MapsActivity.this.getString(R.string.weather_cloudy);
                }
                Toast.makeText(MapsActivity.this,lat,Toast.LENGTH_LONG).show();
                double lon2 = Double.parseDouble(lon);
                double lat2=Double.parseDouble(lat);
                LatLng sydney = new LatLng(lat2,lon2 );
                Log.d("Lat==========",lat);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                marker=mMap.addMarker(new MarkerOptions().position(sydney).title(temp+" ℃"+pressure+"  hPa").icon(bitmapDescriptorFromVector(MapsActivity.this,R.drawable.sunny)));
//                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//                    @Override
//                    public View getInfoWindow(Marker marker) {
//                        if(marker.getId().equals(MapsActivity.this.marker.getId())){
//                            TextView textView=new TextView(MapsActivity.this);
//                            textView.setText("Ya ne znayu chto ya delayu");
//                            textView.setTextColor(Color.RED);
//                            return textView;
//                        }else
//                            return null;
//                    }
//
//                    @Override
//                    public View getInfoContents(Marker marker) {
//                        return null;
//                    }
//                });
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
        }
        }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.sunny);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(55, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
