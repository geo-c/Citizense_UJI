package geoc.esr06.gaiatascastellon2017;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

public class FrontPage extends AppCompatActivity {
    String campaign_configuration = null;
    String lat = "0.00000000";
    String lon = "0.00000000";
    float accuracy = 0;
    SharedPreferences pref;
    Set<String> sendLater = null;
    Set<String> favoriteCampaign = null;
    boolean setCreated = false;
    public static String setCreated_boolean = "boolean status of sendLater";
    boolean connectionStatus;
    String user_ID = null;
    String secretCode = null;
    static String base_URL = "http://150.128.97.32:8880/getCampaign";
    String My_URL = base_URL;
    public long connectStart, connectEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        System.out.println("Here is FrontPage");

        System.out.println("The current language setting of the phone is " + Locale.getDefault().getLanguage().toString());
        System.out.println("The current get default of the phone is " + Locale.getDefault().toString());

        pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
        sendLater          = pref.getStringSet("pref.xml", null);
        favoriteCampaign   = pref.getStringSet("fav.xml", null);
        setCreated = pref.getBoolean(setCreated_boolean, false);

        user_ID = pref.getString("userID", null);
        secretCode = pref.getString("secretCode", null);
        System.out.println("User_ID is " + user_ID);
        System.out.println("From FrontPage, secretCode is " + secretCode);

        {
            if (setCreated == false)
            {
                sendLater = new HashSet<String>();

                SharedPreferences.Editor editor = pref.edit();
                editor.putStringSet("pref.xml", sendLater);

                favoriteCampaign = new HashSet<String>();
                editor.putStringSet("fav.xml", favoriteCampaign);

                setCreated = true;
                editor.putBoolean(setCreated_boolean, setCreated);
                System.out.println("Create a new set for SendLater and favoriteCampaign");
                System.out.println("The size of sendLater is " + sendLater.size());
                System.out.println("The size of favoriteCampaign is " + favoriteCampaign.size());
                //Toast.makeText(getBaseContext(), "Create a new set for SendLater and favoriteCampaign", Toast.LENGTH_LONG).show();

                editor.commit();
            }
            else
            {
                System.out.println("SendLater and favoriteCampaign already exists");
                //Toast.makeText(getBaseContext(), "SendLater and favoriteCampaign already exists", Toast.LENGTH_LONG).show();
                sendLater = pref.getStringSet("pref.xml", null);
                favoriteCampaign = pref.getStringSet("fav.xml", null);

                System.out.println("The size of the existing sendLater is " + sendLater.size());
                System.out.println("The size of the existing favoriteCampaign is " + favoriteCampaign.size());

                Iterator<String> it = sendLater.iterator();
                while(it.hasNext()){
                    System.out.println(it.next());
                }

            }

            //=================Code for obtaining location
            LocationManager locationManager;
            String context = Context.LOCATION_SERVICE;
            locationManager = (LocationManager) getSystemService(context);

            Criteria crta = new Criteria();
            crta.setAccuracy(Criteria.ACCURACY_FINE);
            crta.setAltitudeRequired(false);
            crta.setBearingRequired(false);
            crta.setCostAllowed(true);
            crta.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(crta, true);

            //String provider = LocationManager.GPS_PROVIDER;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            updateWithNewLocation(location);

            locationManager.requestLocationUpdates(provider, 10000, 20,  locationListener);
            //=================Code for obtaining location - END

            System.out.println("Latitude is " + lat);
            System.out.println("Longtitude is " + lon);

            //new HttpAsyncGET().execute(My_URL);

            final LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams llLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            ll.setLayoutParams(llLP);

            //Change status bar color
            Window window = this.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.BLUE);
            }

            /*
            Button favorite = new Button(this);
            favorite.setText("Favorite campaigns");
            ll.addView(favorite);

            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goto_ListFavoriteCampaign = new Intent(getApplicationContext(), ListFavoriteCampaign.class);
                    startActivity(goto_ListFavoriteCampaign);
                }
            });
            */

            Button bt = new Button(this);
            bt.setText(R.string.button_goToCampaign);
            ll.addView(bt);

            final String finalMy_URL = My_URL;
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                /*
                if (campaign_configuration != null) {
                    //Intent intent = new Intent(getApplicationContext(), ActualCampaign.class);
                    Intent intent = new Intent(getApplicationContext(), ListOfCampaign.class);
                    intent.putExtra(KEY1, campaign_configuration);
                    startActivity(intent);
                }*/

                    //=================Code for obtaining location

                    LocationManager locationManager;
                    String context = Context.LOCATION_SERVICE;
                    locationManager = (LocationManager) getSystemService(context);

                    Criteria crta = new Criteria();
                    crta.setAccuracy(Criteria.ACCURACY_FINE);
                    crta.setAltitudeRequired(false);
                    crta.setBearingRequired(false);
                    crta.setCostAllowed(true);
                    crta.setPowerRequirement(Criteria.POWER_LOW);
                    String provider = locationManager.getBestProvider(crta, true);

                    //String provider = LocationManager.GPS_PROVIDER;
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling

                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(provider);
                    updateWithNewLocation(location);

                    locationManager.requestLocationUpdates(provider, 10000, 20,   locationListener);
                    //=================Code for obtaining location - END
                    System.out.println("Button clicked, Latitude is " + lat);
                    System.out.println("Button clicked, Longtitude is " + lon);

                    HashMap<String , String> map = new HashMap<String, String>();
                    map.put("lat", lat);
                    map.put("lon", lon);
                    map.put("language", Locale.getDefault().getLanguage().toString());
                    map.put("userID", user_ID);
                    map.put("secretCode",secretCode);

                    String param = null;
                    try {
                        param = convertMaptoParam(map);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    My_URL = My_URL + "?" + param;
                    System.out.println("Button clicked, URL is " + My_URL);

                    connectionStatus = internetConnectionAvailable(1000);

                    if (connectionStatus == true)
                    {
                        Toast.makeText(getBaseContext(), R.string.internetOn, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getBaseContext(), "On resume, Lat is " + lat + " and Long is " + lon + " accuracy is " + accuracy + " URL is " + My_URL, Toast.LENGTH_LONG).show();
                        new HttpAsyncGET().execute(My_URL);
                        connectStart = new DateTime().getMillis();
                        My_URL = base_URL;
                        System.out.println("After RESET, URL is " + My_URL);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), R.string.internetOff, Toast.LENGTH_SHORT).show();
                    }
                }
            });



            TextView space = new TextView(this);
            ll.addView(space);
            TextView space2 = new TextView(this);
            ll.addView(space2);
            TextView space3 = new TextView(this);
            ll.addView(space3);


            TextView reminder = new TextView(this);
            reminder.setText(R.string.message_CreateCampaign);
            reminder.setTextColor(Color.BLUE);
            ll.addView(reminder);



            Button about = new Button(this);
            about.setText(R.string.button_About);
            ll.addView(about);
            about.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intentAboutExperiment = new Intent(getApplicationContext(), AboutExperiment.class);
                    startActivity(intentAboutExperiment);

                }
            });

            setContentView(ll);

        }


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); //  remove this line
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {


        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1609.344 ;
        //return distance in meters
        return dist;
    }
    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onPause(){
        super.onPause();
        // put your code here...
        System.out.println("-----------------------------On PAUSE---------------------------");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            //=================Code for obtaining location
            LocationManager locationManager;
            String context = Context.LOCATION_SERVICE;
            locationManager = (LocationManager) getSystemService(context);

            Criteria crta = new Criteria();
            crta.setAccuracy(Criteria.ACCURACY_FINE);
            crta.setAltitudeRequired(false);
            crta.setBearingRequired(false);
            crta.setCostAllowed(true);
            crta.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(crta, true);

            //String provider = LocationManager.GPS_PROVIDER;
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            updateWithNewLocation(location);

            locationManager.requestLocationUpdates(provider, 60000, 150,  locationListener);
            //=================Code for obtaining location - END
            System.out.println("On pause, Lat is " + lat + " and Long is " + lon);
            pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("pauseLat",lat);
            editor.putString("pauseLon",lon);
            editor.commit();

            location = null;
            locationManager = null;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

        System.out.println("-----------------------------On resume---------------------------");
        pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
        String pauseLat = pref.getString("pauseLat","0.0000000");
        String pauseLon = pref.getString("pauseLon","0.0000000");

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pauseLat","0.0000000000");
        editor.putString("pauseLon", "0.0000000000");
        editor.commit();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            //=================Code for obtaining location
            LocationManager locationManager;
            String context = Context.LOCATION_SERVICE;
            locationManager = (LocationManager) getSystemService(context);

            Criteria crta = new Criteria();
            crta.setAccuracy(Criteria.ACCURACY_FINE);
            crta.setAltitudeRequired(false);
            crta.setBearingRequired(false);
            crta.setCostAllowed(true);
            crta.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(crta, true);

            //String provider = LocationManager.GPS_PROVIDER;
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            updateWithNewLocation(location);

            locationManager.requestLocationUpdates(provider, 10000, 20, locationListener);
            //=================Code for obtaining location - END

            double distanceSincePause = distance(Double.parseDouble(pauseLat),Double.parseDouble(pauseLon),Double.parseDouble(lat),Double.parseDouble(lon));
            System.out.println("On resume, Lat is " + lat + " and Long is " + lon);
            //Toast.makeText(getBaseContext(), "On resume, Lat is " + lat + " and Long is " + lon +" Distance is " + distanceSincePause + " accuracy is " + accuracy, Toast.LENGTH_LONG).show();
        }
    }

    private class HttpAsyncGET extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Campaign downloaded!", Toast.LENGTH_LONG).show();

            connectEnd = new DateTime().getMillis();
            Toast.makeText(getApplicationContext(), "Waiting time is " + (connectEnd - connectStart) + " milisec", Toast.LENGTH_LONG).show();

            campaign_configuration = result;
            System.out.println("Campaign config is " + campaign_configuration);

            Intent intent = new Intent(getApplicationContext(), ListOfCampaign.class);
            intent.putExtra(MainActivity.KEY1, campaign_configuration);
            startActivity(intent);
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = null;
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            updateWithNewLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            updateWithNewLocation(null);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }
    };

    private void updateWithNewLocation(Location location)
    {

        if (location != null)
        {
            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());
            accuracy = location.getAccuracy();
        }
    }

    public static String convertMaptoParam(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    public static byte[] encrypt(String plainText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(Questionnaire_Done.IV.getBytes("UTF-8")));
        return cipher.doFinal(plainText.getBytes("UTF-8"));
    }

    public static String byteArrayToHexString(byte[] array) {
        StringBuffer hexString = new StringBuffer();
        for (byte b : array) {
            int intVal = b & 0xff;
            if (intVal < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg) {

            try {
                String text = arg[0];

                URL url = new URL(text); // here is your URL path

                System.out.println("URL is " + url.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(arg[1]);

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean internetConnectionAvailable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress!=null && !inetAddress.equals("");
    }

}