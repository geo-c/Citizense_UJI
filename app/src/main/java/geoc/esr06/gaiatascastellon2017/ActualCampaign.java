package geoc.esr06.gaiatascastellon2017;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.joda.time.DateTime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import GeoC_QuestionHierarchy.AudioSensor;
import GeoC_QuestionHierarchy.BaseQuestion_Deserializer;
import GeoC_QuestionHierarchy.Base_Question;
import GeoC_QuestionHierarchy.Branch;
import GeoC_QuestionHierarchy.Branch_Deserializer;
import GeoC_QuestionHierarchy.Campaign;
import GeoC_QuestionHierarchy.Campaign_Deserializer;
import GeoC_QuestionHierarchy.ContRange;
import GeoC_QuestionHierarchy.DateTimeConverter;
import GeoC_QuestionHierarchy.DateWrapFactory;
import GeoC_QuestionHierarchy.FreeNumericSingle;
import GeoC_QuestionHierarchy.FreeTextMulti;
import GeoC_QuestionHierarchy.FreeTextSingle;
import GeoC_QuestionHierarchy.MultipleChoiceMulti;
import GeoC_QuestionHierarchy.MultipleChoiceSingle;
import GeoC_QuestionHierarchy.MyAnswerWrapper;
import GeoC_QuestionHierarchy.TextDisplay;
import GeoC_QuestionHierarchy.UploadPhoto;
import GeoC_QuestionHierarchy.Workflow_Element;
import GeoC_QuestionHierarchy.Workflow_Element_Deserializer;

public class ActualCampaign extends AppCompatActivity {
    public static String FreeTextSingle = "FreeTextSingle";
    public static String FreeTextMulti = "FreeTextMulti";
    public static String MultipleChoiceSingle = "MultipleChoiceSingle";
    public static String MultipleChoiceMulti = "MultipleChoiceMulti";
    public static String ContRange = "ContRange";
    public static String AudioSensor = "AudioSensor";
    public static String TextDisplay = "TextDisplay";
    public static String FreeNumericSingle = "FreeNumericSingle";
    public static String UploadPhoto = "UploadPhoto";

    public static String CONTENT = "content from ArrayList that stores Answer objects";
    public static String CampaignID = "the ID of the current campaign";
    public static String CampaignConfig = "The JSON config of the campaign";

    public static final int REQUEST_READ_EXTERNAL_STORAGE = 10;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 11;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String serverAddress = "http://www.citizense.uji.es/userUpload.php";
    public static String serverPost = "http://150.128.97.32:8880/send_result";

    boolean hasImage = false;

    LinearLayout ll;

    public static final int REQUEST_CAMERA = 100;

    ArrayList<String> tag_list = new ArrayList();

    int count = 0; //control index for answer_array

    public Button space;
    public Button back;
    public Button bt;
    public Button finish;

    String temp = null;
    String message = null;

    String lat = "0.000000";
    String lon = "0.000000";

    final List sequence = new ArrayList();

    ImageView imageView;
    TextView tv;
    //EditText fileName;
    Button upload;
    Button takePhoto;
    String nameOfFile="";
    boolean newPhoto = false;

    private static String root = null;
    private static String imageFolderPath = null;
    private String tempImage = null;
    private static Uri fileUri = null;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    //---------Encryption parameter
    public static String IV = "AAAAAAAAAAAAAAAA";
    public static String encryptionKey = "0123456789abcdef";

    ArrayList<Answer> tobesent = new ArrayList<Answer>();

    //---------------Audio sensor variables
    Button start, stop;
    TextView measuredValue;
    boolean hasMeasurement = false;

    double sum = 0.0;
    double average = 0;
    double max =  0;
    double min = Double.MAX_VALUE;
    int sample_number = 0;
    long startTime;
    long stopTime;
    long duration;
    Thread runner;
    final Runnable updater = new Runnable(){

        public void run(){
            updateTv();
        };
    };
    final Handler mHandler = new Handler();
    int sampling_frequency = 500;

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;

    boolean mStartRecording = true;
    private MediaRecorder mRecorder = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    //---------------Audio sensor variables END


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        space = new Button(this);
        space.setText("This text should be invisible");
        space.setVisibility(View.INVISIBLE);

        // Record to the external cache directory for visibility
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        pref = getSharedPreferences("pref.xml", MODE_PRIVATE);

        final Display display = getWindowManager().getDefaultDisplay();
        int width =  ((display.getWidth()*50)/100);
        int height = ((display.getHeight()*50)/100);
        final LinearLayout.LayoutParams imageViewParam = new LinearLayout.LayoutParams(width,height);

        System.out.println("Width is " + width + " and height is " + height);

        System.out.println("Andriod version is " + android.os.Build.VERSION.SDK_INT);
        if (android.os.Build.VERSION.SDK_INT >= 23)
        {
            if (ContextCompat.checkSelfPermission(ActualCampaign.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("Requesting permission for accessing galery");
                ActivityCompat.requestPermissions(ActualCampaign.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
            }

            if (ContextCompat.checkSelfPermission(ActualCampaign.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("Requesting permission for accessing CAMERA");
                ActivityCompat.requestPermissions(ActualCampaign.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }

            if (ContextCompat.checkSelfPermission(ActualCampaign.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("Requesting permission for recording AUDIO");
                ActivityCompat.requestPermissions(ActualCampaign.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            }
        }

        final ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

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

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Branch.class, new Branch_Deserializer());
        gsonBuilder.registerTypeAdapter(Workflow_Element.class, new Workflow_Element_Deserializer());
        gsonBuilder.registerTypeAdapter(Base_Question.class, new BaseQuestion_Deserializer());
        gsonBuilder.registerTypeAdapter(Campaign.class, new Campaign_Deserializer());
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeConverter());
        final Gson gson = gsonBuilder.create();

        Intent intent = getIntent();
        //final String message = intent.getStringExtra(MainActivity.KEY1);
        message = intent.getStringExtra(CampaignDescription.key);
        temp = intent.getStringExtra(ListFavoriteCampaign.CONTENT_FAV_CAMPAIGN);

        if (message == null) message = temp;

        System.out.println("Value of temp string is " + temp);

        System.out.println(message);

        final Campaign campaign_obj = gson.fromJson(message, Campaign.class);
        //final Campaign[] Campaign_Array = gson.fromJson(message, Campaign[].class);

        String startQuestion = campaign_obj.getStartQuestion();
        List<Base_Question> ques_array = campaign_obj.getQuestionArray();

        final List<Workflow_Element> workflow_element = campaign_obj.getWorkflow();
        for(int i=0;i<workflow_element.size();i++)
        {
            System.out.println(workflow_element.get(i).getID());
            for(int j=0;j<workflow_element.get(i).getCondition().size();j++)
            {
                System.out.println(       ((Branch) workflow_element.get(i).getCondition().get(j)).getExpression() + " ---> " + ((Branch) workflow_element.get(i).getCondition().get(j)).getNext()   );
            }
        }

        final String[] pointer = new String[]{startQuestion};
        System.out.println("First question is -----------> " + pointer[0]);

        final Map<String , String> map = new HashMap<String, String>();

        int number_of_question = ques_array.size();
        final String[] variable_name = new String[number_of_question];
        for (int i=0;i<ques_array.size();i++)
        {
            Base_Question temp_basequestion = gson.fromJson(gson.toJson(ques_array.get(i)),Base_Question.class);
            variable_name[i] = temp_basequestion.getQuestionID();
            map.put(temp_basequestion.getQuestionID(),temp_basequestion.getQuestionType());
        }
        for(String key: map.keySet()) System.out.println(key + " - " + map.get(key));

        System.out.println("The variable names are " + Arrays.toString(variable_name));

        final Answer[] answer_array = new Answer[number_of_question];
        List<Object> blanklist = new ArrayList<Object>();
        for(int i=0;i<number_of_question;i++) answer_array[i] = new Answer("blank","blank","blank",blanklist,new DateTime());

        final Answer defaultAnswer = new Answer("blank","blank","blank",blanklist,new DateTime());

        //final ArrayList<Answer> tobesent = new ArrayList<Answer>();

        count = 0; //control index for answer_array

        ll = new LinearLayout(this);
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

        imageView = new ImageView(this);
        imageView.setLayoutParams(imageViewParam);
        imageView.setBackgroundColor(Color.rgb(66, 197, 244));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("ImageView clicked");
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        tv = new TextView(this);
        tv.setText(R.string.message_clickImageView);

        //fileName = new EditText(this);

        upload = new Button(this);
        upload.setText("Upload picture");
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("About to upload picture");
                Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                //System.out.println("The file name is " + fileName.getText().toString());

                DateTime moment = new DateTime();

                pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
                nameOfFile = String.valueOf(moment.getMillis());


                new UploadImageTask(image, nameOfFile).execute();
            }
        });

        takePhoto = new Button(this);
        takePhoto.setText(R.string.button_TakePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("About to TAKE PHOTO");
                Toast.makeText(getApplicationContext(), "For performance reasons, please take photos at the medium level of resolution , preferably 6M", Toast.LENGTH_SHORT).show();

                if(Build.VERSION.SDK_INT>=24){
                    try{
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                newPhoto = true;

                if (ContextCompat.checkSelfPermission(ActualCampaign.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Requesting permission for accessing CAMERA");
                    ActivityCompat.requestPermissions(ActualCampaign.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                } else
                {
                    root = Environment.getExternalStorageDirectory().toString()  + "/Khoi_Folder";
                    // Creating folders for Image
                    imageFolderPath = root + "/saved_images";
                    File imagesFolder = new File(imageFolderPath);
                    imagesFolder.mkdirs();

                    // Generating file name
                    tempImage = "test.png";

                    File image = new File(imageFolderPath, tempImage);

                    fileUri = Uri.fromFile(image);

                    imageView.setTag(imageFolderPath + File.separator + tempImage);

                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
                }
            }
        });

        start = new Button(this);
        start.setText(R.string.button_StartMeasuringNoise);
        //ll.addView(start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //Reset variable
                sum = 0;
                average = 0;
                max = 0;
                min = Double.MAX_VALUE;
                sample_number = 0;

                System.out.println("Start recording");
                System.out.println("Variable mStartRecording is " + mStartRecording);
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
                System.out.println("After click, variable mStartRecording is " + mStartRecording);
                System.out.println("---------------------");

                //Hide Start button
                start.setVisibility(View.INVISIBLE);
                stop.setVisibility(View.VISIBLE);

                startTime = System.currentTimeMillis();
                runner = new Thread() {
                    public void run() {
                        while (runner != null) {
                            try {
                                Thread.sleep(sampling_frequency);
                                Log.i("Noise", "Tock");
                            } catch (InterruptedException e) {
                            }
                            mHandler.post(updater);
                        }
                    }
                };
                runner.start();
                Log.d("Noise", "start runner()");

            }
        });

        stop = new Button(this);
        stop.setText(R.string.button_Stop);
        //ll.addView(stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("Stop recording");
                System.out.println("Variable mStartRecording is " + mStartRecording);
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
                System.out.println("After click, variable mStartRecording is " + mStartRecording);
                System.out.println("---------------------");

                hasMeasurement = true;

                stop.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);

                stopTime = System.currentTimeMillis();
                duration = stopTime - startTime;
                average = sum / sample_number;
                System.out.println("Sum is " + sum);
                System.out.println("Duration is " + duration);
                System.out.println("Number of sample is " + sample_number);
                System.out.println("Average is " + average);

                Toast.makeText(getBaseContext(), "Average is " + average + ", min is " + min + ", max is " + max + ", duration is " + duration + ", sample is " + sample_number, Toast.LENGTH_SHORT).show();

                long threadID = runner.getId();
                System.out.println("Value of ThreadID is " + threadID);
                runner.currentThread().interrupt();
                runner = null;
            }
        });

        measuredValue = new TextView(this);
        measuredValue.setText("0 dB");
        //ll.addView(measuredValue);

        //show(pointer[0], campaign, ll, tag_list);
        final Displayer myDisplay = new Displayer("khoi", getApplicationContext());
        myDisplay.show(pointer[0], campaign_obj, ll, this, tag_list);
        if (map.get(pointer[0]).equals(UploadPhoto))
        {
            ll.addView(imageView);
            ll.addView(tv);
            //ll.addView(fileName);
            //ll.addView(upload);
            ll.addView(takePhoto);
        }
        sequence.add(pointer[0]);
        System.out.println("The sequence is ");
        for (int i=0;i<sequence.size();i++)
            System.out.println(sequence.get(i));


        final Intent intent2 = new Intent(this, Questionnaire_Done.class);

        bt = new Button(this);
        bt.setText(R.string.button_Next);
        bt.setBackgroundColor(Color.GREEN);
        ll.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean proceedNext = true;

                List<Object> replies = new ArrayList<Object>();
                String tag = tag_list.get(0);
                String quesID = tag.substring(0, tag.indexOf("_"));

                String quesType = map.get(quesID);
                System.out.println("quesType is " + quesType);

                for (int i = 0; i < tag_list.size(); i++)
                {
                    String full_tag = tag_list.get(i);
                    String short_tag = full_tag.substring(0, full_tag.indexOf("_"));
                    System.out.println("Full tag is " + full_tag + " short tag is " + short_tag);
                    v = ll.findViewWithTag(full_tag);

                    if (map.get(short_tag).equals(FreeTextSingle))
                    {

                        EditText et = (EditText) v.findViewWithTag(full_tag);
                        System.out.println("Print from NEXT button " + et.getText().toString());
                        replies.add(et.getText().toString());

                        //Hide keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                    }
                    else if (map.get(short_tag).equals(FreeTextMulti))
                    {

                        EditText et = (EditText) v.findViewWithTag(full_tag);
                        System.out.println("Print from NEXT button " + et.getText().toString());
                        replies.add(et.getText().toString());

                        //Hide keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);

                    }
                    else if (map.get(short_tag).equals(MultipleChoiceSingle))
                    {
                        String result;

                        RadioGroup radioGroup = (RadioGroup) v.findViewWithTag(full_tag);

                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        View myview = radioGroup.findViewById(selectedId);
                        int position = radioGroup.indexOfChild(myview);
                        RadioButton clickedRadioButton = (RadioButton) radioGroup.getChildAt(position);
                        if (clickedRadioButton == null)
                        {
                            proceedNext = false;
                            System.out.println("-----------User didnt select any radio button");
                            Toast.makeText(getApplicationContext(), "Please select an option", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            result = clickedRadioButton.getText().toString();
                            replies.add(result);
                            System.out.println(result);
                        }

                    }
                    else if (map.get(short_tag).equals(MultipleChoiceMulti)    )
                    {

                        CheckBox checkbox = (CheckBox) v.findViewWithTag(full_tag);
                        if (checkbox.isChecked())
                        {
                            System.out.println(checkbox.getText().toString());
                            replies.add(checkbox.getText().toString());
                        }

                    }
                    else if (map.get(short_tag).equals(ContRange))
                    {

                        SeekBar my_seekbar = (SeekBar) v.findViewWithTag(full_tag);
                        System.out.println("The max value of this seekbar is " + my_seekbar.getMax());

                        replies.add(myDisplay.seekbar_value);
                        System.out.println("Value of seekbar is " + myDisplay.seekbar_value);

                    }
                    else if (map.get(short_tag).equals(AudioSensor))
                    {
                        if (hasMeasurement == true)
                        {
                            replies.add(min);
                            replies.add(average);
                            replies.add(max);
                            replies.add(duration);
                            replies.add(sampling_frequency);
                            replies.add(lat);
                            replies.add(lon);
                        }
                        /*
                        TextView tv = (TextView) v.findViewWithTag(full_tag);
                        start.setVisibility(View.VISIBLE);
                        stop.setVisibility(View.VISIBLE);
                        currentValue.setVisibility(View.VISIBLE);
                        replies.add(min);
                        replies.add(average);
                        replies.add(max);
                        //start.setVisibility(View.INVISIBLE);
                        //stop.setVisibility(View.INVISIBLE);
                        System.out.println("AudioSensor: Lat is " + lat);
                        System.out.println("AudioSensor: Long is " + lon);
                        replies.add(lat);
                        replies.add(lon);
                        */
                    }
                    else if (map.get(short_tag).equals(TextDisplay))
                    {
                        System.out.println("Print from NEXT button: NULL because of TextDisplay " );
                        replies.add(null);
                    }
                    else if (map.get(short_tag).equals(FreeNumericSingle))
                    {
                        EditText et = (EditText) v.findViewWithTag(full_tag);
                        System.out.println("Print from NEXT button " + et.getText().toString());
                        if ( !et.getText().toString().isEmpty() )
                        {
                            replies.add(   Integer.parseInt(et.getText().toString())   );
                        }
                        else
                        {
                            proceedNext = false;
                            System.out.println("User didnt type the numeric input");
                            Toast.makeText(getApplicationContext(), "Please type the value for this task", Toast.LENGTH_LONG).show();
                        }


                        //Hide keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                    }
                    else if (map.get(short_tag).equals(UploadPhoto))
                    {
                        if (hasImage == true)
                        {
                            //------------------------ uploadButton
                            System.out.println("About to upload picture");
                            Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            //System.out.println("The file name is " + fileName.getText().toString());
                            DateTime moment = new DateTime();
                            pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
                            nameOfFile = String.valueOf(moment.getMillis());
                            new UploadImageTask(image, nameOfFile).execute();
                            //------------------------
                            hasImage = false;

                            replies.add(nameOfFile);
                            if (newPhoto == true)
                            {
                                replies.add(lat);
                                replies.add(lon);
                            }
                        }

                        System.out.println("Name of file uploaded is " + nameOfFile);
                        newPhoto = false;
                    }

                }

                if (proceedNext == true)
                {
                    Answer ans = new Answer(campaign_obj.getID(),quesType,quesID, replies, new DateTime());
                    System.out.println("Answer for question " + quesID + " has been collected");
                    System.out.println("Size of replies is" + replies.size());
                    tobesent.add(ans);
                    System.out.println("==================== tobesent is " + gson.toJson(tobesent));
                    //ans.print();
                    System.out.println("Current count is " + count);
                    answer_array[count] = ans;
                    count++;
                    System.out.println("After increment, count is " + count);

                    //for (int i = 0; i < answer_array.length; i++)
                    //    answer_array[i].print();

                    try {
                        System.out.println("After " + campaign_obj.getStartQuestion() + " next question should be " + flow(pointer[0],answer_array,workflow_element,variable_name));
                        pointer[0] = flow(pointer[0],answer_array,workflow_element,variable_name);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    /*
                    if (hasImage == true)
                    {
                        upload.performClick();
                        hasImage = false;
                    }
                    */

                    if (pointer[0] != null)
                    {
                        ll.removeAllViews();

                        if (map.get(pointer[0]).equals(AudioSensor))
                        {
                            ll.addView(start);
                            ll.addView(measuredValue);
                            ll.addView(stop);
                            //start.setVisibility(View.VISIBLE);
                            //stop.setVisibility(View.VISIBLE);
                            //currentValue.setVisibility(View.VISIBLE);
                        }
                        else if (map.get(pointer[0]).equals(UploadPhoto))
                        {


                            ll.addView(imageView);
                            ll.addView(tv);
                            //ll.addView(fileName);
                            //ll.addView(upload);
                            //upload.setVisibility(View.INVISIBLE);
                            ll.addView(takePhoto);

                        }
                        else
                        {
                            //start.setVisibility(View.INVISIBLE);
                            //stop.setVisibility(View.INVISIBLE);
                            //currentValue.setVisibility(View.INVISIBLE);
                        }

                        tag_list = new ArrayList<String>();
                        //show(pointer[0], campaign, ll, tag_list);
                        myDisplay.show(pointer[0], campaign_obj, ll, getApplicationContext(), tag_list);
                        sequence.add(pointer[0]);
                        System.out.println("The sequence is ");
                        for (int i=0;i<sequence.size();i++)
                            System.out.println(sequence.get(i));

                        if (count > 0)
                        {
                            ll.addView(space);
                            ll.addView(back);
                        }


                        List workflow = campaign_obj.getWorkflow();
                        for (int i = 0;i < workflow.size();i++)
                        {
                            Workflow_Element workflow_element = (Workflow_Element) workflow.get(i);
                            if (workflow_element.getID().equals(pointer[0]))
                            {

                                if (workflow_element.getCondition().isEmpty())
                                {
                                    System.out.println("This is the last question of the campaign");
                                    ll.addView(finish);
                                }
                                else
                                {
                                    ll.addView(bt);
                                }
                            }
                        }

                        //ll.addView(bt);
                    }
                    else
                    {
                        System.out.println("QUESTIONNAIRE is DONE !!!");
                        String tobesent_text = gson.toJson(tobesent);
                        intent2.putExtra(CONTENT, tobesent_text);
                        intent2.putExtra(CampaignID, campaign_obj.getID());
                        intent2.putExtra(CampaignConfig, message);
                        startActivity(intent2);
                    }

                    boolean checkNetwork = internetConnectionAvailable(1000);
                    //Toast.makeText(ActualCampaign.this, "Check network is " + checkNetwork, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //----------------------------------------------------------Send the results LATER
        final Button sendLater = new Button(this);
        sendLater.setText("Send later");
        //ll.addView(sendLater);
        sendLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
                final SharedPreferences.Editor editor = pref.edit();
                Set<String> tempSet = pref.getStringSet("pref.xml", null);
                tempSet.add(gson.toJson(tobesent));
                editor.putStringSet(MainActivity.sendLater_String, tempSet);
                editor.commit();

                startActivity(intent2);
            }
        });

        //----------------------------------------------------------Send the results NOW
        final Button sendNow = new Button(this);
        sendNow.setText("Send results");
        //ll.addView(bt);
        sendNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int plaintext_length_without_padding = gson.toJson(tobesent).length();
                String tobesent_with_padding = gson.toJson(tobesent);
                final int padding_size = ((plaintext_length_without_padding/16)+1)*16 - plaintext_length_without_padding ;
                System.out.println("The padding size is " + padding_size);
                for (int i=0;i<padding_size;i++)
                {
                    tobesent_with_padding += "0";
                }
                byte[] cipher = new byte[0];
                try {
                    cipher = encrypt(tobesent_with_padding, encryptionKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String hexString = byteArrayToHexString(cipher);

                boolean connectionStatus = internetConnectionAvailable(1000);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("paddingSize", String.valueOf(padding_size));
                map.put("CampaignID", campaign_obj.getID());
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

                String myURL = serverPost + /*campaignID +*/ "?" + param;

                new SendPostRequest().execute(myURL, hexString);
                System.out.println("/////////////////////////////////////Finally, tobesent is " + gson.toJson(tobesent));
                System.out.println("The parameters are " + myURL);
                startActivity(intent2);
            }
        });

        finish = new Button(this);
        finish.setText(R.string.button_FinishLastQuestion);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                readResult(view, ll, map, myDisplay, campaign_obj, tobesent, answer_array);

                System.out.println("QUESTIONNAIRE DONE from finish button !!!");

                boolean connectionStatus = internetConnectionAvailable(1000);

                System.out.println("Finish button clicked, Connection status is " + connectionStatus);
                //Toast.makeText(ActualCampaign.this, "Connection is " + connectionStatus, Toast.LENGTH_SHORT).show();

                if (connectionStatus == true) //Send the results now
                {
                    sendNow.performClick();
                    System.out.println("Connection is " + connectionStatus);
                } else //Send the result later when network is available
                {
                    sendLater.performClick();
                }

                String tobesent_text = gson.toJson(tobesent);
                intent2.putExtra(CONTENT, tobesent_text);
                intent2.putExtra(CampaignID, campaign_obj.getID());
                intent2.putExtra(CampaignConfig, message);
                startActivity(intent2);

            }
        });

        back = new Button(this);
        back.setText(R.string.button_Back);
        if (count > 0) ll.addView(back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String prevQues = tobesent.get( tobesent.size()-1  ).getID();
                System.out.println("Will display " + prevQues);
                ll.removeAllViews();
                tag_list = new ArrayList<String>();

                if (map.get(prevQues).equals(UploadPhoto))
                {
                    ll.addView(imageView);
                    ll.addView(tv);
                    //ll.addView(fileName);
                    //ll.addView(upload);
                    ll.addView(takePhoto);
                }
                else if ( map.get(prevQues).equals(AudioSensor) )
                {
                    ll.addView(start);
                    ll.addView(measuredValue);
                    ll.addView(stop);
                }

                myDisplay.show(prevQues, campaign_obj, ll, getApplicationContext(), tag_list);
                tobesent.remove(tobesent.size() - 1);
                sequence.remove( sequence.size()-1 );
                count = count - 1;
                answer_array[count] = defaultAnswer;
                pointer[0] = (String) sequence.get(count);
                System.out.println("pointer[0] now is " + pointer[0]);

                if (count > 0)
                {
                    ll.addView(space);
                    ll.addView(back);
                }
                ll.addView(bt);

            }
        });

        scrollView.addView(ll);
        setContentView(scrollView);
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public double getAmplitudeKhoi() {
        if (mRecorder != null)
            return       20 * Math.log10(mRecorder.getMaxAmplitude());
        else
            return 0;
    }
    public void updateTv(){

        double value = getAmplitudeKhoi();

        measuredValue.setText(String.valueOf(value) + " dB");
        if (value >0) {
            sum = sum + value;
            if (value > max) max = value;
            if (value < min) min = value;
            sample_number++;
        }
    }

    public void readResult( View v, LinearLayout ll, Map<String , String> map, Displayer myDisplay, Campaign campaign_obj, ArrayList<Answer> tobesent, Answer[] answer_array) {


        List<Object> replies = new ArrayList<Object>();
        String tag = tag_list.get(0);
        String quesID = tag.substring(0, tag.indexOf("_"));
        String quesType = map.get(quesID);

        for (int i = 0; i < tag_list.size(); i++)
        {
            String full_tag = tag_list.get(i);
            String short_tag = full_tag.substring(0, full_tag.indexOf("_"));
            System.out.println("Full tag is " + full_tag + " short tag is " + short_tag);
            v = ll.findViewWithTag(full_tag);

            if (map.get(short_tag).equals(FreeTextSingle))
            {

                EditText et = (EditText) v.findViewWithTag(full_tag);
                System.out.println("Print from NEXT button " + et.getText().toString());
                replies.add(et.getText().toString());
            }
            else if (map.get(short_tag).equals(FreeTextMulti))
            {

                EditText et = (EditText) v.findViewWithTag(full_tag);
                System.out.println("Print from NEXT button " + et.getText().toString());
                replies.add(et.getText().toString());
            }
            else if (map.get(short_tag).equals(MultipleChoiceSingle))
            {

                RadioGroup radioGroup = (RadioGroup) v.findViewWithTag(full_tag);

                int selectedId = radioGroup.getCheckedRadioButtonId();
                View myview = radioGroup.findViewById(selectedId);
                int position = radioGroup.indexOfChild(myview);
                RadioButton clickedRadioButton = (RadioButton) radioGroup.getChildAt(position);
                String result = clickedRadioButton.getText().toString();

                replies.add(result);

                System.out.println(result);
            }
            else if (map.get(short_tag).equals(MultipleChoiceMulti)    )
            {

                CheckBox checkbox = (CheckBox) v.findViewWithTag(full_tag);
                if (checkbox.isChecked())
                {
                    System.out.println(checkbox.getText().toString());
                    replies.add(checkbox.getText().toString());
                }

            }
            else if (map.get(short_tag).equals(ContRange))
            {

                SeekBar my_seekbar = (SeekBar) v.findViewWithTag(full_tag);
                System.out.println("The max value of this seekbar is " + my_seekbar.getMax());

                replies.add(myDisplay.seekbar_value);
                System.out.println("Value of seekbar is " + myDisplay.seekbar_value);

            }
            else if (map.get(short_tag).equals(AudioSensor))
            {
                if (hasMeasurement == true)
                {
                    replies.add(min);
                    replies.add(average);
                    replies.add(max);
                    replies.add(duration);
                    replies.add(sampling_frequency);
                    replies.add(lat);
                    replies.add(lon);
                }
            }
            else if (map.get(short_tag).equals(FreeNumericSingle))
            {

                EditText et = (EditText) v.findViewWithTag(full_tag);
                System.out.println("Print from NEXT button " + et.getText().toString());
                replies.add(Integer.parseInt(et.getText().toString()));
            }
            else if (map.get(short_tag).equals(UploadPhoto))
            {

                if (hasImage == true)
                {
                    //------------------------ uploadButton
                    System.out.println("About to upload picture");
                    Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    //System.out.println("The file name is " + fileName.getText().toString());
                    DateTime moment = new DateTime();
                    pref = getSharedPreferences("pref.xml", MODE_PRIVATE);
                    nameOfFile = String.valueOf(moment.getMillis());
                    new UploadImageTask(image, nameOfFile).execute();
                    //------------------------
                    hasImage = false;

                    replies.add(nameOfFile);
                    if (newPhoto == true)
                    {
                        replies.add(lat);
                        replies.add(lon);
                    }
                }

                System.out.println("Name of file uploaded is " + nameOfFile);
                newPhoto = false;

                /* original version
                replies.add(nameOfFile);
                System.out.println("Name of file uploaded is " + nameOfFile);
                */
            }

        }
        Answer ans = new Answer(campaign_obj.getID(), quesType, quesID, replies, new DateTime());
        System.out.println("Answer for question " + quesID + " has been collected");
        tobesent.add(ans);
        //ans.print();
        System.out.println("Current count is " + count);
        answer_array[count] = ans;
        count++;
        System.out.println("After increment, count is " + count);
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

        }
    }

    public static Object check(String expression, Answer[] array, String[] var_name) throws IllegalAccessException, InstantiationException, InvocationTargetException, InvocationTargetException {
        Object result = null;

        //for (int i=0;i<namelist.size();i++) System.out.println(namelist.get(i));
        org.mozilla.javascript.Context cx = org.mozilla.javascript.Context.enter();
        cx.setOptimizationLevel(-1);
        DateWrapFactory wrapFactory = new DateWrapFactory();
        //wrapFactory.setJavaPrimitiveWrap(false);

        cx.setWrapFactory(wrapFactory);

        try {
            Scriptable scope = cx.initStandardObjects();
            ScriptableObject.defineClass(scope, MyAnswerWrapper.class);

            MyAnswerWrapper[] scriptable_array = new MyAnswerWrapper[var_name.length];

            for (int i=0;i<var_name.length;i++)
            {
                String ID = var_name[i];
                List<Object> temp = null;
                for (int j=0;j< array.length;j++)
                {
                    if (array[j].getID().equals(ID)) temp = array[j].getValues();
                }
                Answer temp_answer = new Answer("everything","anything",ID,temp,new DateTime());
                Object[] arguments = new Object[]{temp_answer};
                scriptable_array[i] = (MyAnswerWrapper) cx.newObject(scope, "MyWrapperAnswer",arguments);

                scope.put(ID, scope, scriptable_array[i]);
            }

            result =  cx.evaluateString(scope, expression, "anything", 1, null);
            //.out.println(result);
        } finally {
            org.mozilla.javascript.Context.exit();
        }
        System.out.println("Expression is " + expression);
        System.out.println(expression + " --> " + result);
        return result;
    }

    public static String flow(String quesID, Answer[] answer_array, List<Workflow_Element> workflow_arraylist, String[] var_name) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        String result = null;
        for (int i=0;i< workflow_arraylist.size();i++)
        {
            Workflow_Element element;
            if (workflow_arraylist.get(i).getID().equals(quesID))
            {
                element = workflow_arraylist.get(i);
                ArrayList<Branch> branch_list = element.getCondition();
                if (branch_list.size() >1)
                {
                    String[] expression = new String[branch_list.size()];

                    for (int j=0;j<branch_list.size();j++)
                    {
                        expression[j] = branch_list.get(j).getExpression();
                        if ( ((boolean) check(expression[j],answer_array,var_name)) == true   )
                            result = branch_list.get(j).getNext();
                    }
                }
                else if (branch_list.size()==1)
                {
                    result = branch_list.get(0).getNext();
                }
            }
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("Permission for GALLERY is granted for the first time");

                if (ContextCompat.checkSelfPermission(ActualCampaign.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    System.out.println("Requesting permission for accessing CAMERA");
                    ActivityCompat.requestPermissions(ActualCampaign.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }

                if (ContextCompat.checkSelfPermission(ActualCampaign.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                {
                    System.out.println("Requesting permission for recording AUDIO");
                    ActivityCompat.requestPermissions(ActualCampaign.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                }

            }
            else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                System.out.println("Permission is denied");
                if (ActivityCompat.shouldShowRequestPermissionRationale(ActualCampaign.this, Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    System.out.println("Enter IF branch");
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important to get access to gallery.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(ActualCampaign.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    ActivityCompat.requestPermissions(ActualCampaign.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                }
                else
                {
                    //Never ask again and handle your app without permission.
                }
            }
        }
        else if (requestCode == REQUEST_CAMERA)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("Permission for CAMERA is granted for the first time");

                if (ContextCompat.checkSelfPermission(ActualCampaign.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    System.out.println("Requesting permission for WRITING to external storage");
                    ActivityCompat.requestPermissions(ActualCampaign.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                }

                if (ContextCompat.checkSelfPermission(ActualCampaign.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                {
                    System.out.println("Requesting permission for recording AUDIO");
                    ActivityCompat.requestPermissions(ActualCampaign.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                }

            }
            else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                System.out.println("Permission for CAMERA is denied");
                if (ActivityCompat.shouldShowRequestPermissionRationale(ActualCampaign.this, Manifest.permission.CAMERA))
                {
                    System.out.println("Enter IF branch");
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important to get access to CAMERA.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(ActualCampaign.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                        }
                    });
                    ActivityCompat.requestPermissions(ActualCampaign.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }
                else
                {
                    //Never ask again and handle your app without permission.
                }
            }
        }
        else if ( requestCode == REQUEST_RECORD_AUDIO_PERMISSION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("Permission for RECORDING AUDIO is granted for the first time");


            }
            else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            {

            }

        }

    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null )
        {
            upload.setVisibility(View.VISIBLE);
            Uri selectedIamge = data.getData();
            imageView.setImageURI( selectedIamge );
            hasImage = true;

            System.out.println("The image is " + selectedIamge.toString());

        }

        else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK)
        {
            upload.setVisibility(View.VISIBLE);

            //Uri selectedIamge = data.getData();
            Uri selectedIamge = fileUri;
            imageView.setImageURI(selectedIamge);
            hasImage = true;

            //System.out.println("The image is " + selectedIamge.toString());

            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            //imageView.setImageBitmap(photo);
        }
    }

    private class UploadImageTask extends AsyncTask<Void, Void, Void>
    {

        Bitmap image;
        String fileName;

        public UploadImageTask( Bitmap image, String name )
        {
            this.image = image;
            this.fileName = name;
        }

        @Override
        protected void onPreExecute()
        {
            //bt.setVisibility(View.INVISIBLE);


        }

        @Override
        protected Void doInBackground(Void... params) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add( new BasicNameValuePair("image", encodedImage) );
            dataToSend.add( new BasicNameValuePair("name", fileName) );

            HttpParams httpRequestParams = getHttprequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post  =new HttpPost(serverAddress);

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            imageView.setImageResource(0);
            //bt.setVisibility(View.VISIBLE);

            Toast.makeText(ActualCampaign.this, R.string.toaat_UploadPhotoSuccessfully, Toast.LENGTH_LONG).show();
            System.out.println("Done");
            //return aVoid;
        }
    }

    private HttpParams getHttprequestParams()
    {
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000 * 30);
        return httpRequestParams;
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

    public static byte[] encrypt(String plainText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
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

            if (result.contains("OK"))
            {
                Toast.makeText(getApplicationContext(), R.string.submissionOK, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), R.string.sumbissionNotOK, Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}

class Displayer {
    String name;
    Context context;
    int seekbar_value;
    MainActivity mainActivity = new MainActivity();



    public Displayer(String name, android.content.Context context)    {
        this.name = name;
        this.context = context;
    }

    public void show(String ID, Campaign cam, LinearLayout layout, android.content.Context context, ArrayList tag_list) {
        /*String FreeTextSingle = "FreeTextSingle";
        String FreeTextMulti = "FreeTextMulti";
        String MultipleChoiceSingle = "MultipleChoiceSingle";
*/
        FreeTextSingle temp_FreeTextSingle = null;
        FreeTextMulti temp_FreeTextMulti = null;
        MultipleChoiceSingle temp_MultipleChoiceSingle = null;
        MultipleChoiceMulti temp_MultipleChoiceMulti = null;
        ContRange temp_ContRange = null;
        AudioSensor temp_AudioSensor = null;
        TextDisplay temp_TextDisplay = null;
        FreeNumericSingle temp_FreeNumericSingle = null;
        UploadPhoto temp_UploadPhoto = null;

        ArrayList<Base_Question> question_array = (ArrayList) cam.getQuestionArray();
        for (int i=0;i<question_array.size();i++)
        {
            Base_Question base_question = question_array.get(i);
            Base_Question temp_basequestion = base_question;

            if (temp_basequestion.getQuestionID().equals(ID))
            {
                if ((base_question instanceof FreeTextSingle))
                {
                    temp_FreeTextSingle = (FreeTextSingle) base_question;
                    display(temp_FreeTextSingle, layout, context, tag_list);
                }

                else if ( base_question instanceof FreeTextMulti)
                {
                    temp_FreeTextMulti = (FreeTextMulti) base_question;
                    display(temp_FreeTextMulti, layout, context, tag_list);
                }

                else if (base_question instanceof MultipleChoiceSingle)
                {
                    temp_MultipleChoiceSingle = (MultipleChoiceSingle) base_question;
                    display(temp_MultipleChoiceSingle, layout, context, tag_list);
                }

                else if (base_question instanceof MultipleChoiceMulti)
                {
                    temp_MultipleChoiceMulti = (MultipleChoiceMulti) base_question;
                    display(temp_MultipleChoiceMulti, layout, context, tag_list);
                }
                else if (base_question instanceof ContRange)
                {
                    temp_ContRange = (ContRange) base_question;
                    display(temp_ContRange, layout, context, tag_list);
                }
                else if (base_question instanceof AudioSensor)
                {
                    temp_AudioSensor = (AudioSensor) base_question;
                    display(temp_AudioSensor, layout, context, tag_list);
                }
                else if (base_question instanceof TextDisplay)
                {
                    temp_TextDisplay = (TextDisplay) base_question;
                    display(temp_TextDisplay, layout, context, tag_list);
                }
                else if (base_question instanceof FreeNumericSingle)
                {
                    temp_FreeNumericSingle = (FreeNumericSingle) base_question;
                    display(temp_FreeNumericSingle, layout, context, tag_list);
                }


                else if (base_question instanceof UploadPhoto)
                {
                    temp_UploadPhoto = (UploadPhoto) base_question;
                    display(temp_UploadPhoto, layout, context, tag_list);
                }
            }
        }

    }

    public void common_display(Base_Question obj, final LinearLayout layout, android.content.Context context) {
        System.out.println(obj.getQuestionID() + " " + obj.getQuestionType() + " " + Arrays.toString(obj.getQuestionLabel()));
        String[] quesLabel = obj.getQuestionLabel();
        TextView tv = new TextView(context);
        tv.setText(quesLabel[0]);
        tv.setTextColor(Color.BLACK);
        layout.addView(tv);



        if (    (!quesLabel[1].isEmpty()) && (  quesLabel[1].endsWith("jpg") || quesLabel[1].endsWith("jpeg") || quesLabel[1].endsWith("JPEG") || quesLabel[1].endsWith("JPG")
                || quesLabel[1].endsWith("PNG")  || quesLabel[1].endsWith("png")  )    )
        {
            final String image_URL = quesLabel[1];
            System.out.println("Image URL is " + image_URL);

            final String formatted_image_URL = image_URL.replace(" ", "%20");

            //final Bitmap[] bmp = new Bitmap[1];
            final ImageView imageview = new ImageView(context);

            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        System.out.println("From AsyncTask, the image URL is " + formatted_image_URL);
                        InputStream in = new URL(formatted_image_URL).openStream();
                        /*bmp[0] =*/ return BitmapFactory.decodeStream(in);
                    } catch (Exception e) {  }
                    return null;
                }
                @Override
                protected void onPostExecute(Bitmap result) {
                    if (/*bmp[0]*/ result!= null)
                    {
                        imageview.setImageBitmap(result /* bmp[0]*/);

                        layout.addView(imageview);
                    }
                }
            }.execute();
        }
    }

    public void display(FreeTextSingle obj, final LinearLayout layout, android.content.Context context, ArrayList tag_list )    {
        common_display(obj, layout, context);

        final EditText edittext = new EditText(context);
        edittext.setText("");
        edittext.setTextColor(Color.BLACK);
        edittext.requestFocus();
        //edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        String tag = obj.getQuestionID() + "_";
        edittext.setTag(tag);
        tag_list.add(tag);
        layout.addView(edittext);
    }

    public void display(FreeTextMulti obj, final LinearLayout layout, android.content.Context context, ArrayList tag_list )    {
        common_display(obj, layout, context);

        String[] subcomponent = obj.getComponent();
        int size = subcomponent.length;
        TextView[] textview = new TextView[size];
        EditText[] edittext = new EditText[size];
        for (int i=0;i<subcomponent.length;i++)
        {
            textview[i] = new TextView(context);
            textview[i].setText(subcomponent[i]);
            textview[i].setTextColor(Color.BLACK);
            layout.addView(textview[i]);

            edittext[i] = new EditText(context);
            edittext[i].setText("");
            edittext[i].setTextColor(Color.BLACK);
            if (i==0) edittext[i].requestFocus();
            String tag = obj.getQuestionID() + "_" + String.valueOf(i);
            edittext[i].setTag(tag);
            tag_list.add(tag);
            layout.addView(edittext[i]);
        }
    }

    public void display(MultipleChoiceSingle obj, final LinearLayout layout, android.content.Context context, ArrayList tag_list )    {
        common_display(obj, layout, context);

        String[] subcomponent = obj.getComponent();
        int size = subcomponent.length;
        RadioGroup radiogroup = new RadioGroup(context);
        String tag = obj.getQuestionID() + "_";
        radiogroup.setTag(tag);
        tag_list.add(tag);
        for (int i=0;i<size;i++)
        {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(subcomponent[i]);
            radioButton.setTextColor(Color.BLACK);
            radiogroup.addView(radioButton);
        }
        layout.addView(radiogroup);
    }

    public void display(MultipleChoiceMulti obj, final LinearLayout layout, android.content.Context context, ArrayList tag_list) {
        common_display(obj, layout, context);

        String[] subcomponent = obj.getComponent();
        int size = subcomponent.length;
        CheckBox[] checkbox_array = new CheckBox[size];
        for (int i=0;i<size;i++)
        {
            checkbox_array[i] = new CheckBox(context);
            checkbox_array[i].setText(subcomponent[i]);
            checkbox_array[i].setTextColor(Color.BLACK);
            String tag = obj.getQuestionID() + "_" + String.valueOf(i);
            checkbox_array[i].setTag(tag);
            tag_list.add(tag);
            layout.addView(checkbox_array[i]);
        }
    }

    public void display(ContRange obj, final LinearLayout layout, final android.content.Context context, ArrayList tag_list) {
        common_display(obj, layout, context);

        String[] subcomponent = obj.getComponent();
        final int min = Integer.parseInt(subcomponent[0]);
        int max = Integer.parseInt(subcomponent[1]);

        SeekBar my_seekbar = new SeekBar(context);
        my_seekbar.setMax(max - min);


        final TextView textView_of_seekBar = new TextView(context);
        textView_of_seekBar.setText("Covered: " + my_seekbar.getProgress() + "/" + (my_seekbar.getMax() + min) );
        layout.addView(textView_of_seekBar);


        my_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = min;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue + min;
                seekbar_value = progressValue + min;
                //Toast.makeText(context, "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(context, "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView_of_seekBar.setText("");
                textView_of_seekBar.setText("Covered: " + (seekBar.getProgress() + min) + "/" + (seekBar.getMax() + min));
                Toast.makeText(context, "You have selected " + String.valueOf(progress), Toast.LENGTH_SHORT).show();
            }
        });

        String tag = obj.getQuestionID() + "_";
        my_seekbar.setTag(tag);
        tag_list.add(tag);
        layout.addView(my_seekbar);
    }

    public void display(AudioSensor obj, final LinearLayout layout, final android.content.Context context, ArrayList tag_list)
    {
        common_display(obj, layout, context);

        TextView hiddenTextView = new TextView(context);
        hiddenTextView.setText("This is a hidden TextView");
        String tag = obj.getQuestionID() + "_";
        hiddenTextView.setTag(tag);
        tag_list.add(tag);
        //layout.addView(hiddenTextView);
    }

    public void display(TextDisplay obj, final LinearLayout layout, android.content.Context context, ArrayList tag_list )
    {
        common_display(obj, layout, context);

        //edittext.setInputType(InputType.TYPE_CLASS_DATETIME);
        String tag = obj.getQuestionID() + "_";
        tag_list.add(tag);
    }

    public void display(FreeNumericSingle obj, final LinearLayout layout, android.content.Context context, ArrayList tag_list )    {
        common_display(obj, layout, context);

        final EditText edittext = new EditText(context);
        edittext.setText("");
        edittext.setTextColor(Color.BLACK);
        edittext.requestFocus();
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        String tag = obj.getQuestionID() + "_";
        edittext.setTag(tag);
        tag_list.add(tag);
        layout.addView(edittext);
    }

    public void display(UploadPhoto obj, final LinearLayout layout, android.content.Context context, ArrayList tag_list)   {
        common_display(obj, layout, context);

        String tag = obj.getQuestionID() + "_";
        tag_list.add(tag);
    }
}