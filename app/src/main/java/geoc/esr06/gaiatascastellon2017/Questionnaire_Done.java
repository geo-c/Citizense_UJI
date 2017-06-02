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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

import GeoC_QuestionHierarchy.BaseQuestion_Deserializer;
import GeoC_QuestionHierarchy.Base_Question;
import GeoC_QuestionHierarchy.Branch;
import GeoC_QuestionHierarchy.Branch_Deserializer;
import GeoC_QuestionHierarchy.Campaign;
import GeoC_QuestionHierarchy.Campaign_Deserializer;
import GeoC_QuestionHierarchy.DateTimeConverter;
import GeoC_QuestionHierarchy.Workflow_Element;
import GeoC_QuestionHierarchy.Workflow_Element_Deserializer;

public class Questionnaire_Done extends AppCompatActivity {
    //public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences pref;

    public static String server = "http://150.128.97.32:8880/send_result";
    public static String IV = "AAAAAAAAAAAAAAAA";
    public static String encryptionKey = "0123456789abcdef";

    public static String CampaignContent = "The JSON config of the campaign";

    String campaign = null;
    String participantID;
    String baseURL = "http://150.128.97.32:8880/updateAddFavorite";

    String lat = "0.000000";
    String lon = "0.000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire__done);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        locationManager.requestLocationUpdates(provider, 60000, 150, locationListener);
        //=================Code for obtaining location - END

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

        TextView thankMessage = new TextView(this);
        thankMessage.setText(R.string.message_ThankYou);
        ll.addView(thankMessage);

        Intent intent = getIntent();
        /*
        final String tobesent_text = intent.getStringExtra(ActualCampaign.CONTENT);
        String tobesent_with_padding = tobesent_text;
        int plaintext_length_without_padding = tobesent_text.length();
        final int padding_size = ((plaintext_length_without_padding/16)+1)*16 - plaintext_length_without_padding ;
        for (int i=0;i<padding_size;i++)
        {
            tobesent_with_padding += "0";
        }
        System.out.println(tobesent_with_padding);
        byte[] cipher = new byte[0];
        try {
            cipher = encrypt(tobesent_with_padding, encryptionKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String hexString = byteArrayToHexString(cipher);
        System.out.println(byteArrayToHexString(cipher));
        */


        final String campaignID = intent.getStringExtra(ActualCampaign.CampaignID);
        final String campaignConfig = intent.getStringExtra(ActualCampaign.CampaignConfig);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Branch.class, new Branch_Deserializer());
        gsonBuilder.registerTypeAdapter(Workflow_Element.class, new Workflow_Element_Deserializer());
        gsonBuilder.registerTypeAdapter(Base_Question.class, new BaseQuestion_Deserializer());
        gsonBuilder.registerTypeAdapter(Campaign.class, new Campaign_Deserializer());
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeConverter());
        final Gson gson = gsonBuilder.create();

        Campaign cam = gson.fromJson(campaignConfig, Campaign.class);


        System.out.println(campaignID + " has been done!");

        pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
        participantID = pref.getString("userID", null);

        /*
        HashMap<String , String> map = new HashMap<String, String>();
        map.put("paddingSize", String.valueOf(padding_size));
        map.put("CampaignID",campaignID);
        map.put("userID",pref.getString("userID",null));
        map.put("lat",lat);
        System.out.println("Submission latitiude is " + lat);
        map.put("lon", lon);
        System.out.println("Submisison longitude is " + lon);

        String param = null;
        try {
            param = convertMaptoParam(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String myURL = server +  "?" + param;
        */

        final Intent intent2 = new Intent(this, MainActivity.class);

        Button addFavorite = new Button(this);
        addFavorite.setText(R.string.button_AddFavorite);
        ll.addView(addFavorite);
        addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
                final SharedPreferences.Editor editor = pref.edit();
                Set<String> tempFavCampaign = pref.getStringSet("fav.xml",null);
                System.out.println("Size of favoriteCampaign is " + tempFavCampaign.size());
                tempFavCampaign.add(campaignConfig);
                System.out.println("After adding a new campaign, size of favoriteCampaign is " + tempFavCampaign.size());
                editor.putStringSet(MainActivity.favCampaign, tempFavCampaign);
                editor.commit();

                boolean connectionStatus = internetConnectionAvailable(1000);

                if (connectionStatus == true)
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("campaignID", campaignID);
                    map.put("participantID",participantID);

                    String param = null;
                    try {
                        param = convertMaptoParam(map);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    String finalURL = baseURL + "?" + param;
                    new UpdateCampaignStatistics_FavList().execute(finalURL);
                }
                Toast.makeText(getBaseContext(), "Campaign added to your favorite list", Toast.LENGTH_SHORT).show();
            }
        });

        //Send the results LATER
        /*
        final Button sendLater = new Button(this);
        sendLater.setText("Send later");
        //ll.addView(sendLater);
        sendLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
                final SharedPreferences.Editor editor = pref.edit();
                Set<String> tempSet = pref.getStringSet("pref.xml", null);
                tempSet.add(tobesent_text);
                editor.putStringSet(MainActivity.sendLater_String, tempSet);
                editor.commit();


                startActivity(intent2);
            }
        });
        */

        //Send the results NOW
        /*
        final Button bt = new Button(this);
        bt.setText("Send results");
        //ll.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean connectionStatus = internetConnectionAvailable(1000);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("paddingSize", String.valueOf(padding_size));
                map.put("CampaignID", campaignID);
                map.put("userID", pref.getString("userID", null));
                map.put("lat", lat);
                System.out.println("Submission latitiude is " + lat);
                map.put("lon", lon);
                System.out.println("Submisison longitude is " + lon);
                map.put("submissionMode", String.valueOf(connectionStatus));

                String param = null;
                try {
                    param = convertMaptoParam(map);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String myURL = server + "?" + param;


                new SendPostRequest().execute(myURL, hexString);
                System.out.println("The parameters are " + myURL);
                startActivity(intent2);
            }
        });
        */



        /*
        final Button finish = new Button(this);
        finish.setText(R.string.button_SubmitResult);
        ll.addView(finish);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean connectionStatus = internetConnectionAvailable(1000);

                System.out.println("Finish button clicked, Connection status is " + connectionStatus);

                if (connectionStatus == true) //Send the results now
                {
                    bt.performClick();
                } else //Send the result later when network is available
                {
                    sendLater.performClick();
                }
            }
        });
        */

        if ( cam.getShowResultBoolean() == true )
        {
            TextView notice = new TextView(this);
            notice.setText("For better visibility, it's recommended to view the result of this campaign using a PC or laptop. The result is available at:");
            ll.addView(notice);

            TextView link = new TextView(this);
            String resultULR = "www.citizense.uji.es/webResultViewer.html?campaign=" + campaignID;
            resultULR = resultULR.replaceAll(" ","%20");

            System.out.println("New result URL is " + resultULR);

            link.setText(resultULR);
            //link.setText("www.citizense.uji.es/mobileResultViewer.html?campaign=" + campaignID);
            link.setTextIsSelectable(true);
            link.setLinksClickable(true);
            Linkify.addLinks(link, Linkify.WEB_URLS);
            ll.addView(link);
            //----------------------------------------
            final Intent intent3 = new Intent(this, ListOfQuestion.class);

            Button submitAndView = new Button(this);
            submitAndView.setText(R.string.button_ViewOtherResult);
            ll.addView(submitAndView);
            submitAndView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //finish.performClick();
                    intent3.putExtra(CampaignContent, campaignConfig);
                    startActivity(intent3);
                }
            });

        }

        final Intent intentHomepage = new Intent(this, MainActivity.class);

        Button goHomePage = new Button(this);
        goHomePage.setText(R.string.button_homepage);
        ll.addView(goHomePage);
        goHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(intentHomepage);
                //finish();
            }
        });



        setContentView(ll);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); //  remove this line
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

                    System.out.println("The response code is " + responseCode);

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

    private class UpdateCampaignStatistics_FavList extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

        }
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

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
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
        cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
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

    //----------------------------Location
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

    //----------------------------Location
    private void updateWithNewLocation(Location location)
    {

        if (location != null)
        {
            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());

        }
    }

}