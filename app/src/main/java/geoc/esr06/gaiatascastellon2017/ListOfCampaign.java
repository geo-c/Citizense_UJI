package geoc.esr06.gaiatascastellon2017;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import GeoC_QuestionHierarchy.BaseQuestion_Deserializer;
import GeoC_QuestionHierarchy.Base_Question;
import GeoC_QuestionHierarchy.Branch;
import GeoC_QuestionHierarchy.Branch_Deserializer;
import GeoC_QuestionHierarchy.Campaign;
import GeoC_QuestionHierarchy.Campaign_Deserializer;
import GeoC_QuestionHierarchy.DateTimeConverter;
import GeoC_QuestionHierarchy.IncentiveType;
import GeoC_QuestionHierarchy.IncentiveType_Deserializer;
import GeoC_QuestionHierarchy.Workflow_Element;
import GeoC_QuestionHierarchy.Workflow_Element_Deserializer;

public class ListOfCampaign extends AppCompatActivity {
    public static String campaignConfig = "content of the selected campaign";
    SharedPreferences pref;
    String message = null;
    String message2 = null;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    boolean connectionStatus;
    static String URL_UpdateOpenValue = "http://150.128.97.32:8880/updateOpenCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_list_of_campaign);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        if (android.os.Build.VERSION.SDK_INT >= 23)
        {


            if (ContextCompat.checkSelfPermission(ListOfCampaign.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("Requesting permission for recording AUDIO");
                ActivityCompat.requestPermissions(ListOfCampaign.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            }

        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Branch.class, new Branch_Deserializer());
        gsonBuilder.registerTypeAdapter(Workflow_Element.class, new Workflow_Element_Deserializer());
        gsonBuilder.registerTypeAdapter(Base_Question.class, new BaseQuestion_Deserializer());
        gsonBuilder.registerTypeAdapter(Campaign.class, new Campaign_Deserializer());
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeConverter());
        gsonBuilder.registerTypeAdapter(IncentiveType.class, new IncentiveType_Deserializer());
        final Gson gson = gsonBuilder.create();

        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.KEY1);
        System.out.println("Activity received " + message);
        message2 = intent.getStringExtra(MainActivity.proceed_ListOfCampaign_offline);

        if (message == null) message = message2;

        pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("downloaded",message);
        editor.commit();
        System.out.println("Downloaded content is " + pref.getString("downloaded", null));

        final Campaign[] Campaign_Array = gson.fromJson(message, Campaign[].class);

        System.out.println("The size of Campaign_Array is " + Campaign_Array.length);

        List<Integer> permutation = new ArrayList<Integer>();
        for (int i=0;i< Campaign_Array.length;i++)
            permutation.add(i);
        java.util.Collections.shuffle(permutation);

        System.out.println("The permutation is ");
        for (int item: permutation)
        {
            System.out.println(item);
        }


        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams llLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(llLP);

        //Change status bar color
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLUE);
        }

        final Button[] buttonArray = new Button[Campaign_Array.length];
        final Intent intent2 = new Intent(this, CampaignDescription.class);

        Drawable logoUJI60 = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.uji60);
        Drawable euro = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.euro);
        Drawable gift = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.gift);
        Drawable castellon = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.castellon);

        for (int i=0;i<Campaign_Array.length;i++)
        {
            buttonArray[i] = new Button(this);
            buttonArray[i].setText(Campaign_Array[permutation.get(i)].getID());

            if ( Campaign_Array[permutation.get(i)].getIncentiveBoolean() == true ) // Campaign with incentive, either display gift or money
            {
                if (    ((IncentiveType)  Campaign_Array[permutation.get(i)].getIncentiveType().get(0)).getTypeNumber().equals("2") || ((IncentiveType)  Campaign_Array[permutation.get(i)].getIncentiveType().get(0)).getTypeNumber().equals("3")        )
                { //incentive is flat payment or average payment ==> display money icon
                    if ( Campaign_Array[permutation.get(i)].getAuthorCode() == 1 )  // Campaign from UJI, display UJI logo
                    {
                        buttonArray[i].setCompoundDrawablesWithIntrinsicBounds(logoUJI60, null, euro, null);
                    }
                    else if ( Campaign_Array[permutation.get(i)].getAuthorCode() == 2 )  // Campaign from Castellon authority, display Castellon logo
                    {
                        buttonArray[i].setCompoundDrawablesWithIntrinsicBounds(castellon, null, euro, null);
                    }
                    else if ( Campaign_Array[permutation.get(i)].getAuthorCode() == 50 )  // Campaign from normal author
                    {
                        buttonArray[i].setCompoundDrawablesWithIntrinsicBounds(null, null, euro, null);
                    }

                }
                else if ( ((IncentiveType)  Campaign_Array[permutation.get(i)].getIncentiveType().get(0)).getTypeNumber().equals("1") )
                { // incentive is prize ==> display prize icon
                    if ( Campaign_Array[permutation.get(i)].getAuthorCode() == 1 )  // Campaign from UJI, display UJI logo
                    {
                        buttonArray[i].setCompoundDrawablesWithIntrinsicBounds(logoUJI60, null, gift, null);
                    }
                    else if ( Campaign_Array[permutation.get(i)].getAuthorCode() == 2 )  // Campaign from Castellon authority, display Castellon logo
                    {
                        buttonArray[i].setCompoundDrawablesWithIntrinsicBounds(castellon, null, gift, null);
                    }
                    else if ( Campaign_Array[permutation.get(i)].getAuthorCode() == 50 )  // Campaign from normal author
                    {
                        buttonArray[i].setCompoundDrawablesWithIntrinsicBounds(null, null, gift, null);
                    }
                }
            }
            else // Campaign without incentive,
            {
                if ( Campaign_Array[permutation.get(i)].getAuthorCode() == 1 )  // Campaign from UJI, display UJI logo
                {
                    buttonArray[i].setCompoundDrawablesWithIntrinsicBounds(logoUJI60, null, null, null);
                }
                else if ( Campaign_Array[permutation.get(i)].getAuthorCode() == 2 )  // Campaign from Castellon authority, display Castellon logo
                {
                    buttonArray[i].setCompoundDrawablesWithIntrinsicBounds(castellon, null, null, null);
                }
            }

            final int index = permutation.get(i);
            final int finalI = i;
            buttonArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    connectionStatus = internetConnectionAvailable(1000);
                    if (connectionStatus == true)
                    {
                        if (Campaign_Array[index].getQuestionArray().isEmpty() != true)
                        {
                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put("campaignID", buttonArray[finalI].getText().toString());

                            String param = null;
                            try {
                                param = convertMaptoParam(map);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            String URL = URL_UpdateOpenValue + "?" + param;

                            new GET_Request().execute(URL);
                        }

                    }

                    if (Campaign_Array[index].getQuestionArray().isEmpty() == true)
                    {
                        System.out.println("++++++++++++++++++++++ This campaign is NULL");
                        Toast.makeText(getBaseContext(), R.string.locationNotAvailable, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        System.out.println(gson.toJson(Campaign_Array[index]));
                        intent2.putExtra(campaignConfig,gson.toJson(Campaign_Array[index]));
                        startActivity(intent2);
                    }

                }
            });

            ll.addView(buttonArray[i]);
        }
        /*
        for (int i=0;i<Campaign_Array.length;i++)
        {
            buttonArray[i] = new Button(this);
            buttonArray[i].setText(Campaign_Array[i].getID());
            final int index = i;
            buttonArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Campaign_Array[index].getQuestionArray().isEmpty() == true)
                    {
                        System.out.println("++++++++++++++++++++++ This campaign is NULL");
                        Toast.makeText(getBaseContext(), R.string.locationNotAvailable, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        System.out.println(gson.toJson(Campaign_Array[index]));
                        intent2.putExtra(campaignConfig,gson.toJson(Campaign_Array[index]));
                        startActivity(intent2);
                    }

                }
            });

            ll.addView(buttonArray[i]);
        }
        */

        ScrollView scrollView = new ScrollView(getApplication());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        scrollView.addView(ll);
        setContentView(scrollView);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("Permission for GALLERY is granted for the first time");
            }
            else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                System.out.println("Permission is denied");
                if (ActivityCompat.shouldShowRequestPermissionRationale(ListOfCampaign.this, Manifest.permission.RECORD_AUDIO))
                {
                    System.out.println("Enter IF branch");
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important to get access to microphone.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(ListOfCampaign.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                        }
                    });
                    ActivityCompat.requestPermissions(ListOfCampaign.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                }
                else
                {
                    //Never ask again and handle your app without permission.
                }
            }
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

    private class GET_Request extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

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