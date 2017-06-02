package geoc.esr06.gaiatascastellon2017;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaredrummler.android.device.DeviceName;

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

import GeoC_QuestionHierarchy.Answer;
import GeoC_QuestionHierarchy.Answer_Deserializer;
import GeoC_QuestionHierarchy.DateTimeConverter;

public class MainActivity extends AppCompatActivity {
    String campaign_configuration = null;
    public static String KEY1 = "key1 of MainActivity.java";
    public static String showPoint = "show expPoint";
    public static String keyViewPublicCampaign = "key viewPublicCampaign";
    String lat = "0.00000000";
    String lon = "0.00000000";
    float accuracy = 0;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences pref;
    public static String sendLater_String = "to be sent later";
    public static String favCampaign = "my favorite Campaigns";
    Set<String> sendLater = null;
    Set<String> favoriteCampaign = null;
    public static String setCreated_boolean = "boolean status of sendLater";
    boolean setCreated = false;
    String user_ID = null;
    String secretCode = null;
    String myURL_postID = "http://150.128.97.32:8880/postid";
    String URL_checkPoint = "http://150.128.97.32:8880/getExpPoint";
    //10.40.145.217
    String userID_typed = null;
    boolean connectionStatus;
    public static String proceed_ListOfCampaign_offline = "Go to ListOfCampaign in offline mode";
    static String base_URL = "http://150.128.97.32:8880/getCampaign";
    String My_URL_GET = base_URL;
    public long connectStart, connectEnd;

    static String viewResult_URL = "http://www.citizense.uji.es:8880/getPublicResult";

    static String URL_UpdateSecretCode = "http://150.128.97.32:8880/updateParticipantSecretCode";

    public static final int REQUEST_LOCATION = 202;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        System.out.println("The current language setting of the phone is " + Locale.getDefault().getLanguage().toString());
        System.out.println("The current get default of the phone is " + Locale.getDefault().toString());

        pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
        sendLater          = pref.getStringSet("pref.xml", null);
        favoriteCampaign   = pref.getStringSet("fav.xml", null);
        setCreated = pref.getBoolean(setCreated_boolean, false);

        secretCode = pref.getString("secretCode", null);
        System.out.println("From MainActivity, secretCode is " + secretCode);

        user_ID = pref.getString("userID", null);
        if (user_ID == null)
        {

            System.out.println("Andriod version is " +  Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT >= 23)
            {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    System.out.println("Requesting permission for location");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
            }

            System.out.println("User_ID is NULL");
            final LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams llLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            ll.setLayoutParams(llLP);
            //ll.setBackgroundColor(Color.parseColor("#b5d6e1"));

            //Change status bar color
            Window window = this.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.BLUE);
            }

            TextView notice = new TextView(this);
            notice.setText(R.string.message_Register);
            ll.addView(notice);

            TextView space = new TextView(this);
            space.setText("");
            ll.addView(space);


            TextView tv = new TextView(this);
            tv.setText(R.string.typeUserID);
            ll.addView(tv);

            final EditText et =  new EditText(this);
            ll.addView(et);

            TextView emailTextview = new TextView(this);
            emailTextview.setText(R.string.typeEmail);
            ll.addView(emailTextview);

            final EditText emailEdittext = new EditText(this);
            ll.addView(emailEdittext);

            final RadioGroup radiogroup = new RadioGroup(this);

            RadioButton sex1 = new RadioButton(this);
            sex1.setText(R.string.male);
            sex1.setTextColor(Color.BLACK);
            radiogroup.addView(sex1);

            RadioButton sex2 = new RadioButton(this);
            sex2.setText(R.string.female);
            sex2.setTextColor(Color.BLACK);
            radiogroup.addView(sex2);

            ll.addView(radiogroup);

            TextView YoB_TextView = new TextView(this);
            YoB_TextView.setText(R.string.typeYearOfBirth);
            ll.addView(YoB_TextView);

            final EditText YoB_EditText = new EditText(this);
            YoB_EditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            ll.addView(YoB_EditText);

            Button submit = new Button(this);
            submit.setText(R.string.button_register);
            ll.addView(submit);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    userID_typed = et.getText().toString();

                    if (userID_typed.isEmpty()) {
                        Toast.makeText(getBaseContext(), R.string.messageNoEmptyUserID, Toast.LENGTH_LONG).show();
                    } else if (YoB_EditText.getText().toString().isEmpty()) {
                        Toast.makeText(getBaseContext(), "Please type Year of Birth", Toast.LENGTH_LONG).show();
                    } else {
                        int selectedId = radiogroup.getCheckedRadioButtonId();
                        View myview = radiogroup.findViewById(selectedId);
                        int position = radiogroup.indexOfChild(myview);
                        RadioButton clickedRadioButton = (RadioButton) radiogroup.getChildAt(position);

                        String deviceModel = DeviceName.getDeviceName();


                        HashMap<String, String> personalInfo = new HashMap<String, String>();
                        personalInfo.put("userID", userID_typed);
                        personalInfo.put("userEmail", emailEdittext.getText().toString());
                        personalInfo.put("gender", String.valueOf(selectedId));
                        personalInfo.put("yearOfBirth", YoB_EditText.getText().toString());
                        personalInfo.put("device", deviceModel);
                        personalInfo.put("APK version", String.valueOf(Build.VERSION.SDK_INT));
                        //personalInfo.put("participantSecretCode", secretCode);
                        String participantParam = null;
                        try {
                            participantParam = convertMaptoParam(personalInfo);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        String finalURL = myURL_postID + "?" + participantParam;
                        new SubmitUserID().execute(finalURL, et.getText().toString());
                    }

                }
            });

            Button notAgree = new Button(this);
            notAgree.setText(R.string.button_notAgree);
            ll.addView(notAgree);
            notAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), DontAgree.class);
                    startActivity(intent);
                }
            });

            Button hiddenSpace1 = new Button(this);
            hiddenSpace1.setText("");
            hiddenSpace1.setVisibility(View.INVISIBLE);
            ll.addView(hiddenSpace1);

            Button hiddenSpace2 = new Button(this);
            hiddenSpace2.setText("");
            hiddenSpace2.setVisibility(View.INVISIBLE);
            ll.addView(hiddenSpace2);

            Button hiddenSpace3 = new Button(this);
            hiddenSpace3.setText("");
            hiddenSpace3.setVisibility(View.INVISIBLE);
            ll.addView(hiddenSpace3);

            Button hiddenSpace4 = new Button(this);
            hiddenSpace4.setText("");
            hiddenSpace4.setVisibility(View.INVISIBLE);
            ll.addView(hiddenSpace4);

            Button hiddenSpace5 = new Button(this);
            hiddenSpace5.setText("");
            hiddenSpace5.setVisibility(View.INVISIBLE);
            ll.addView(hiddenSpace5);

            Button hiddenSpace6 = new Button(this);
            hiddenSpace6.setText("");
            hiddenSpace6.setVisibility(View.INVISIBLE);
            ll.addView(hiddenSpace6);

            //setContentView(ll);

            ScrollView scrollView = new ScrollView(getApplication());
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            scrollView.addView(ll);
            setContentView(scrollView);
        }

        //User_ID already exists, proceed as normal
        else
        {
            System.out.println("From MainActivity, userID is " + user_ID);
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

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
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
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }
                Location location = locationManager.getLastKnownLocation(provider);
                updateWithNewLocation(location);

                locationManager.requestLocationUpdates(provider, 60000, 150, locationListener);
                //=================Code for obtaining location - END
            }

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

            Button favorite = new Button(this);
            favorite.setText(R.string.button_Favorite);
            ll.addView(favorite);

            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goto_ListFavoriteCampaign = new Intent(getApplicationContext(), ListFavoriteCampaign.class);
                    startActivity(goto_ListFavoriteCampaign);
                }
            });

            //Go to Campaign
            Button bt = new Button(this);
            bt.setText(R.string.button_goToCampaign);
            ll.addView(bt);

            final String finalMy_URL = My_URL_GET;
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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
                    System.out.println("Button clicked, Latitude is " + lat);
                    System.out.println("Button clicked, Longtitude is " + lon);

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("lat", lat);
                    map.put("lon", lon);
                    map.put("language", Locale.getDefault().getLanguage().toString());
                    map.put("userID", user_ID);
                    map.put("secretCode", secretCode);

                    String param = null;
                    try {
                        param = convertMaptoParam(map);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    My_URL_GET = My_URL_GET + "?" + param;
                    System.out.println("Button clicked, URL is " + My_URL_GET);


                /*
                if (campaign_configuration != null) {
                    //Intent intent = new Intent(getApplicationContext(), ActualCampaign.class);
                    Intent intent = new Intent(getApplicationContext(), ListOfCampaign.class);
                    intent.putExtra(KEY1, campaign_configuration);
                    startActivity(intent);
                }*/
                    connectionStatus = internetConnectionAvailable(1000);
                    if (connectionStatus == true) {
                        Toast.makeText(getBaseContext(), "Internet connection is ON", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getBaseContext(), "On resume, Lat is " + lat + " and Long is " + lon + " accuracy is " + accuracy + " URL is " + My_URL_GET, Toast.LENGTH_LONG).show();
                        new HttpAsyncGET().execute(My_URL_GET);
                        connectStart = new DateTime().getMillis();
                        My_URL_GET = base_URL;
                        System.out.println("URL after reset is " + My_URL_GET);
                    } else {
                        Toast.makeText(getBaseContext(), "Internet connection is OFF", Toast.LENGTH_LONG).show();
                        System.out.println("In offline mode, will displayed downloaded content");
                        pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
                        String downloaded_content = pref.getString("downloaded", null);
                        System.out.println("In offline mode, downloaded content is " + downloaded_content);
                        Intent goto_ListOfCampaign = new Intent(getApplicationContext(), ListOfCampaign.class);
                        goto_ListOfCampaign.putExtra(proceed_ListOfCampaign_offline, downloaded_content);
                        startActivity(goto_ListOfCampaign);
                    }
                }
            });

            Button checkExpPoint = new Button(this);
            checkExpPoint.setText(R.string.button_checkPoint);
            ll.addView(checkExpPoint);
            checkExpPoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectionStatus = internetConnectionAvailable(1000);
                    if (connectionStatus == true) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("userID", user_ID);

                        String param = null;
                        try {
                            param = convertMaptoParam(map);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        String finalURL = URL_checkPoint + "?" + param;
                        new queryExpPoint().execute(finalURL);


                    } else {
                        Toast.makeText(getBaseContext(), R.string.noInternet, Toast.LENGTH_SHORT).show();
                    }

                }
            });

            Button viewPublicResult = new Button(this);
            viewPublicResult.setText(R.string.button_ViewResult);
            ll.addView(viewPublicResult);
            viewPublicResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectionStatus = internetConnectionAvailable(1000);
                    if (connectionStatus == true) {
                        System.out.println("Will view public result now");
                        new viewResultHttpGet().execute(viewResult_URL);

                    } else {
                        Toast.makeText(getBaseContext(), R.string.noInternet, Toast.LENGTH_SHORT).show();
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


            Button send = new Button(this);
            send.setText("Send the saved results");
            //ll.addView(send);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectionStatus = internetConnectionAvailable(1000);
                    if (connectionStatus == true) {
                        System.out.println("Send saved clicked, connection status is " + connectionStatus);
                        sendLater = pref.getStringSet("pref.xml", null);
                        System.out.println("The size of sendLater is " + sendLater.size());
                        System.out.println(Questionnaire_Done.server);

                        Iterator<String> it = sendLater.iterator();
                        while (it.hasNext()) {
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeConverter());
                            gsonBuilder.registerTypeAdapter(Answer.class, new Answer_Deserializer());
                            Gson gson = gsonBuilder.create();

                            String content = it.next();

                            Answer[] answer_array = gson.fromJson(content, Answer[].class);

                            String content_with_padding = content;
                            int plaintext_length_without_padding = content.length();
                            int padding_size = ((plaintext_length_without_padding / 16) + 1) * 16 - plaintext_length_without_padding;
                            for (int i = 0; i < padding_size; i++) {
                                content_with_padding += "0";
                            }
                            byte[] cipher = new byte[0];
                            try {
                                cipher = encrypt(content_with_padding, Questionnaire_Done.encryptionKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String hexString = byteArrayToHexString(cipher);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("paddingSize", String.valueOf(padding_size));
                            map.put("CampaignID", answer_array[0].getCampaignID());
                            map.put("userID", pref.getString("userID", null));
                            map.put("submissionMode", String.valueOf(false));

                            String param = null;
                            try {
                                param = convertMaptoParam(map);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            final String myURL = Questionnaire_Done.server + "?" + param;

                            new SendPostRequest().execute(myURL, hexString);
                        }
                        sendLater.removeAll(sendLater);
                        pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
                        final SharedPreferences.Editor editor = pref.edit();
                        editor.putStringSet(MainActivity.sendLater_String, sendLater);
                        editor.commit();
                        System.out.println("Now size of sendLater is " + sendLater.size());
                    }
                }
            });

            connectionStatus = internetConnectionAvailable(1000);
            System.out.println("From homepage, Connection status is " + connectionStatus);

            if (connectionStatus == true)
            {
                System.out.println("Connection status is " + connectionStatus + ", send the saved results now");
                send.performClick();
            }


            //setContentView(ll);
            ScrollView scrollView = new ScrollView(getApplication());
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            scrollView.addView(ll);
            setContentView(scrollView);

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
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
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

            locationManager.requestLocationUpdates(provider, 10000, 20,  locationListener);
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

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
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
            //Toast.makeText(getApplicationContext(), "Waiting time is " + (connectEnd - connectStart) + " milisec", Toast.LENGTH_LONG).show();
            campaign_configuration = result;
            System.out.println("Asynctask received " + result);
            System.out.println("Campaign config is " + campaign_configuration);


            Intent intent = new Intent(getApplicationContext(), ListOfCampaign.class);
            intent.putExtra(KEY1, campaign_configuration);
            startActivity(intent);
        }
    }

    private class queryExpPoint extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Intent intent = new Intent(getApplicationContext(), ShowExpPoint.class);
            intent.putExtra(showPoint, result);
            startActivity(intent);
        }
    }

    private class updateSecretCode extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

        }
    }

    private class viewResultHttpGet extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Intent intent = new Intent(getApplicationContext(), ListPublicCampaign.class);
            intent.putExtra(keyViewPublicCampaign, result);
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

            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }

    public class SubmitUserID extends AsyncTask<String, Void, String> {

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

            if (result.equals("bad"))
            {
                Toast.makeText(getApplicationContext(), R.string.messageUserExisted, Toast.LENGTH_LONG).show();
            }

            else /*  if (result.equals("good")) */
            {
                /*
                int min = 0;
                int max = 100;

                Random r = new Random();
                int randomNumber = r.nextInt(max - min + 1) + min;
                System.out.println("The secret code is " + randomNumber);
                */
                System.out.println("From server, the result (the secret code) is " + result);
                int randomNumber = Integer.parseInt(result);

                pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("userID",userID_typed);
                editor.putString("secretCode", String.valueOf(randomNumber));
                editor.commit();

                /*
                //------------------- Update secret code
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("userID", userID_typed);
                map.put("participantSecretCode", String.valueOf(randomNumber));

                System.out.println("The map is " + map.toString());


                String param = null;
                try {
                    param = convertMaptoParam(map);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String finalURL = URL_UpdateSecretCode + "?" + param;
                System.out.println("Update participantSecretCode: " + finalURL);
                new queryExpPoint().execute(finalURL);
                //------------------- Update secret code END
                */


                Intent intent = new Intent(getApplicationContext(), FrontPage.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == REQUEST_LOCATION)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("Permission is granted for the first time");
            }
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                System.out.println("Permission is denied");
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    System.out.println("Enter IF branch");
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important to get location.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        }
                    });
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
                else
                {
                    //Never ask again and handle your app without permission.
                }
            }
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
