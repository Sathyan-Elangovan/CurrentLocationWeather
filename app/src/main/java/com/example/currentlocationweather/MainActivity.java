package com.example.currentlocationweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView Name, Date, Temp, Des, lat,Altitude;
    private LocationManager locationManager;
    double latitude, longitude;
    private double altitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Name = (TextView) findViewById(R.id.Name);
        Date = (TextView) findViewById(R.id.Date);
        Temp = (TextView) findViewById(R.id.Temp);
        Des = (TextView) findViewById(R.id.Description);
        lat = (TextView) findViewById(R.id.latlng);
        Altitude=(TextView)findViewById(R.id.alti);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        getLocation();
        find_weather();
    }

    private void getLocation() {
        try {
            LocationManager locmgr = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String locprovider = locmgr.getBestProvider(criteria, false);
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            //assert locprovider != null;
            Location loc = locmgr.getLastKnownLocation(locprovider);
            boolean isGPSEnabled=locmgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNWEnabled=locmgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!isGPSEnabled && isNWEnabled)
            {
                Toast.makeText(this, "Both GPS and NW not enabled", Toast.LENGTH_SHORT).show();
            }
            else
                {
                if(isNWEnabled)
                {
                    if (locmgr!=null)
                    {
                        loc=locmgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    if (isGPSEnabled)
                    {
                        if (loc==null)
                        {
                            if (locmgr !=null)
                            {
                                loc=locmgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            }
                        }
                    }
                }
            }
            latitude=loc.getLatitude();
            longitude=loc.getLongitude();
            altitude=loc.getAltitude();
            Altitude.setText(String.valueOf(altitude));
            lat.setText(String.valueOf(latitude)+" "+String.valueOf(longitude));

        }
        catch (NullPointerException ne)
        {
            Log.e("Current Location","Current lat lng is null");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void find_weather()
    {
        String url="http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=0fc4c06e1014b1ffb30b1730639130d3&units=Imperial";

        JsonObjectRequest jor=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject mainob=response.getJSONObject("main");
                    JSONArray jsonArray=response.getJSONArray("weather");
                    JSONObject object=jsonArray.getJSONObject(0);
                    String temp=String.valueOf(mainob.getDouble("temp"));
                    String descrip=object.getString("description");
                    String city=response.getString("name");
                    Name.setText(city);
                    Des.setText(descrip);
                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat sdf=new SimpleDateFormat("EEEE-MM-dd");
                    String fromatteddate=sdf.format(calendar.getTime());
                    Date.setText(fromatteddate);
                    double tempint= Double.parseDouble(temp);
                    double centi=(tempint-32)/1.8000;
                    centi=Math.round(centi);
                    int i=(int)centi;
                    Temp.setText(String.valueOf(i));

                }catch (JSONException e)
                {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(jor);

    }
/*** if(ActivityCompat.checkSelfPermission(
 MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED &&
 ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
 {
 ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
 }
 else
 {
 Location locationGPS=locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
 if (locationGPS!=null)
 {
 latitude=locationGPS.getLatitude();
 longitude=locationGPS.getLongitude();
 lat.setText(String.valueOf(latitude)+"  "+String.valueOf(longitude));
 }
 }
 *
 *
 */


}
