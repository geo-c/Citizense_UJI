package geoc.esr06.gaiatascastellon2017;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
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
import GeoC_QuestionHierarchy.Workflow_Element;
import GeoC_QuestionHierarchy.Workflow_Element_Deserializer;

public class ListOfQuestion extends AppCompatActivity {

    String campaignConfig;
    String camConfigFromAllPublicCampaign;
    Campaign cam;
    public static String KEY1 = "key1 from ListOfQuestion";
    public static String KEY2 = "key2 from ListOfQuestion";

    String url = "http://150.128.97.32:8880/getDetailedResult";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        campaignConfig = intent.getStringExtra(Questionnaire_Done.CampaignContent);
        camConfigFromAllPublicCampaign = intent.getStringExtra(ListPublicCampaign.keyCampaignConfig);

        if (campaignConfig == null) campaignConfig = camConfigFromAllPublicCampaign;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Branch.class, new Branch_Deserializer());
        gsonBuilder.registerTypeAdapter(Workflow_Element.class, new Workflow_Element_Deserializer());
        gsonBuilder.registerTypeAdapter(Base_Question.class, new BaseQuestion_Deserializer());
        gsonBuilder.registerTypeAdapter(Campaign.class, new Campaign_Deserializer());
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeConverter());
        final Gson gson = gsonBuilder.create();

        cam = gson.fromJson(campaignConfig, Campaign.class);

        System.out.println("Campaign has " + cam.getQuestionArray().size() + " questions");
        String[] listContent = new String[cam.getQuestionArray().size()];

        String back;
        if ( Locale.getDefault().getLanguage().toString().equals("es") ) back = "Volver";
        else if ( Locale.getDefault().getLanguage().toString().equals("ca") ) back = "Tornar";
        else back = "Come back";

        for (int i=0;i< cam.getQuestionArray().size(); i++)
        {
            Base_Question temp_basequestion = gson.fromJson(gson.toJson(cam.getQuestionArray().get(i)),Base_Question.class);
            listContent[i] = temp_basequestion.getQuestionLabel()[0];
        }

        //listContent[cam.getQuestionArray().size() + 1] = back;

        System.out.println("Control: The listContent is " + Arrays.toString(listContent));

        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams llLP = new LinearLayout.LayoutParams(  LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(llLP);
        setContentView(linearLayout);
        //Change status bar color
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLUE);
        }

        ListView DynamicListView = new ListView(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (ListOfQuestion.this, android.R.layout.simple_list_item_1, listContent);

        DynamicListView.setAdapter(adapter);

        linearLayout.addView(DynamicListView);

        final Intent intent2 = new Intent(this, MainActivity.class);

        DynamicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                    Base_Question temp_basequestion = gson.fromJson(gson.toJson(cam.getQuestionArray().get(position)),Base_Question.class);

                    System.out.println("Select ques: " + temp_basequestion.getQuestionLabel()[0]);
                    boolean connectionStatus;
                    connectionStatus = internetConnectionAvailable(1000);
                    if (connectionStatus == true)
                    {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("campaignID", cam.getID());
                        map.put("quesID", temp_basequestion.getQuestionID());

                        String param = null;
                        try {
                            param = convertMaptoParam(map);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Parameter of the request is " + map.toString());

                        new getRepliesFromQuesID(temp_basequestion.getQuestionID()).execute(url + "?" + param);
                    }
                    else Toast.makeText(getBaseContext(), R.string.noInternet, Toast.LENGTH_SHORT).show();
            }
        });



    }

    private class getRepliesFromQuesID extends AsyncTask<String, Void, String> {

        public getRepliesFromQuesID(String str)
        {

        }

        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Intent intent = new Intent(getApplicationContext(), DetailResult.class);
            intent.putExtra(KEY1, result);
            //intent.putExtra(KEY2, quesType);
            startActivity(intent);
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
