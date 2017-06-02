package geoc.esr06.gaiatascastellon2017;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.io.InputStream;
import java.net.URL;

import GeoC_QuestionHierarchy.BaseQuestion_Deserializer;
import GeoC_QuestionHierarchy.Base_Question;
import GeoC_QuestionHierarchy.Branch;
import GeoC_QuestionHierarchy.Branch_Deserializer;
import GeoC_QuestionHierarchy.Campaign;
import GeoC_QuestionHierarchy.Campaign_Deserializer;
import GeoC_QuestionHierarchy.DateTimeConverter;
import GeoC_QuestionHierarchy.IncentiveType;
import GeoC_QuestionHierarchy.Workflow_Element;
import GeoC_QuestionHierarchy.Workflow_Element_Deserializer;

public class CampaignDescription extends AppCompatActivity {

    public static String key = "key of CampaignDescription.class";
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= 23)
        {
            if (ContextCompat.checkSelfPermission(CampaignDescription.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("Requesting permission for accessing galery");
                ActivityCompat.requestPermissions(CampaignDescription.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
            }
        }

        Intent intent = getIntent();
        final String message = intent.getStringExtra(ListOfCampaign.campaignConfig);

        System.out.println("The campaign config is " + message);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Branch.class, new Branch_Deserializer());
        gsonBuilder.registerTypeAdapter(Workflow_Element.class, new Workflow_Element_Deserializer());
        gsonBuilder.registerTypeAdapter(Base_Question.class, new BaseQuestion_Deserializer());
        gsonBuilder.registerTypeAdapter(Campaign.class, new Campaign_Deserializer());
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeConverter());
        final Gson gson = gsonBuilder.create();

        Campaign cam = gson.fromJson(message, Campaign.class);
        System.out.println("The campaign description is " + cam.getDescription());

        Drawable logoUJI60 = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.uji60);
        Drawable euro = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.euro);
        Drawable gift = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.gift);
        Drawable city = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.castellon);

        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams llLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(llLP);



        TextView tv = new TextView(this);
        tv.setText(cam.getDescription());
        ll.addView(tv);

        TextView space = new TextView(this);
        space.setText("");
        ll.addView(space);

        if (cam.getExpiry() == true)  //------------ Display time restriction of the campaign
        {
            TextView camPeriodTextView = new TextView(this);
            //camPeriodTextView.setText("This campaign is available in the following time period");
            camPeriodTextView.setText(R.string.info_CampaignTime);
            ll.addView(camPeriodTextView);

            TextView camPeriodValue = new TextView(this);
            camPeriodValue.setText(cam.getStartDate() + " - " + cam.getEndDate());
            ll.addView(camPeriodValue);
        }
        else
        {
            TextView camPeriodTextView = new TextView(this);
            //camPeriodTextView.setText("This campaign is available permanently");
            camPeriodTextView.setText(R.string.info_CampaignPermanent);
            ll.addView(camPeriodTextView);
        }

        TextView space2 = new TextView(this);
        space2.setText("");
        ll.addView(space2);

        if (cam.getOnetimeValue() == true)  //------------ Display mode of completion of the campaign
        {
            TextView modeOfCompletionMessage = new TextView(this);
            modeOfCompletionMessage.setText(R.string.info_CampaignOneTime);
            ll.addView(modeOfCompletionMessage);
        }
        else
        {
            TextView modeOfCompletionMessage = new TextView(this);
            modeOfCompletionMessage.setText(R.string.info_CampaignMultipleTime);
            ll.addView(modeOfCompletionMessage);
        }

        TextView space3 = new TextView(this);
        space3.setText("");
        ll.addView(space3);

        if ( cam.getShowResultBoolean() == true )
        {
            TextView showResultMessage = new TextView(this);
            showResultMessage.setText(R.string.info_CampaignPublic);
            ll.addView(showResultMessage);
        }
        else
        {
            TextView showResultMessage = new TextView(this);
            showResultMessage.setText(R.string.info_CampaignPrivate);
            ll.addView(showResultMessage);
        }

        TextView space4 = new TextView(this);
        space4.setText("");
        ll.addView(space4);

        if ( cam.getAuthorCode() == 1 )  // Campaign from UJI
        {
            TextView authorityLogoExplain = new TextView(this);
            authorityLogoExplain.setText(R.string.info_UJI);
            authorityLogoExplain.setCompoundDrawablesWithIntrinsicBounds(logoUJI60, null, null, null);
            ll.addView(authorityLogoExplain);
        }
        else if ( cam.getAuthorCode() == 2 )  // Campaign from city authority
        {
            TextView authorityLogoExplain = new TextView(this);
            authorityLogoExplain.setText(R.string.info_Castellon);
            authorityLogoExplain.setCompoundDrawablesWithIntrinsicBounds(city, null, null, null);
            ll.addView(authorityLogoExplain);
        }

        TextView space5 = new TextView(this);
        space5.setText("");
        ll.addView(space5);

        if ( cam.getIncentiveBoolean() == true )
        {
            if (   ((IncentiveType) cam.getIncentiveType().get(0)).getTypeNumber().equals("2") ) // Flatpayment incentive
            {
                TextView incentiveMoneyExplain = new TextView(this);
                incentiveMoneyExplain.setText(R.string.info_Type2);
                incentiveMoneyExplain.setCompoundDrawablesWithIntrinsicBounds(euro, null, null, null);
                ll.addView(incentiveMoneyExplain);

                TextView incentiveAmount = new TextView(this);
                incentiveAmount.setText(((IncentiveType) cam.getIncentiveType().get(0)).getParameter().get(0));
                ll.addView(incentiveAmount);
            }
            else if ( ((IncentiveType) cam.getIncentiveType().get(0)).getTypeNumber().equals("3") )  // Average payment incentive, to be decided after the campaign expires
            {
                TextView incentiveMoneyExplain = new TextView(this);
                incentiveMoneyExplain.setText(R.string.info_Type3);
                incentiveMoneyExplain.setCompoundDrawablesWithIntrinsicBounds(euro, null, null, null);
                ll.addView(incentiveMoneyExplain);

                TextView camDeadline = new TextView(this);
                camDeadline.setText(cam.getEndDate());
                ll.addView(camDeadline);
            }
            else if ( ((IncentiveType) cam.getIncentiveType().get(0)).getTypeNumber().equals("1") )  // Prize incentive
            {
                TextView prizeExplain = new TextView(this);
                prizeExplain.setText(R.string.info_Type1);
                prizeExplain.setCompoundDrawablesWithIntrinsicBounds(gift, null, null, null);
                ll.addView(prizeExplain);

                TextView prizeDescriptionLabel  = new TextView(this);
                prizeDescriptionLabel.setText(((IncentiveType) cam.getIncentiveType().get(0)).getParameter().get(1));
                ll.addView(prizeDescriptionLabel);

                TextView camDeadlineLabel = new TextView(this);
                camDeadlineLabel.setText(R.string.info_CampaignDeadline);
                ll.addView(camDeadlineLabel);

                TextView camDeadline = new TextView(this);
                camDeadline.setText(cam.getEndDate());
                ll.addView(camDeadline);



            }
        }

        TextView space6 = new TextView(this);
        space6.setText("");
        ll.addView(space6);

        final Intent intent2 = new Intent(this, ActualCampaign.class);

        if (cam.getShowAuthor() == true)
        {
            String firstName = cam.getAuthorFirstName();



            TextView campaignCreatedBy = new TextView(this);
            campaignCreatedBy.setText(R.string.info_CampaignCreated);
            ll.addView(campaignCreatedBy);

            TextView authorFirstName = new TextView(this);
            authorFirstName.setText(firstName);
            ll.addView(authorFirstName);

            String authorPic = cam.getAuthorLinkPic();

            if (    (!authorPic.isEmpty()) && (  authorPic.endsWith("jpg") || authorPic.endsWith("jpeg") || authorPic.endsWith("JPEG")
                    || authorPic.endsWith("JPG")  || authorPic.endsWith("PNG")  || authorPic.endsWith("png")  )    )
            {
                final String formatted_authorPic = authorPic.replace(" ", "%20");

                final ImageView displayAuthorPic = new ImageView(this);

                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        try {
                            System.out.println("From AsyncTask, the image URL is " + formatted_authorPic);
                            InputStream in = new URL(formatted_authorPic).openStream();
                        /*bmp[0] =*/ return BitmapFactory.decodeStream(in);
                        } catch (Exception e) {  }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Bitmap result) {
                        if (/*bmp[0]*/ result!= null)
                        {
                            displayAuthorPic.setImageBitmap(result /* bmp[0]*/);

                            ll.addView(displayAuthorPic);

                            //----------------
                            Button anotherNext = new Button(getApplicationContext());
                            anotherNext.setText(R.string.button_Next);
                            ll.addView(anotherNext);
                            anotherNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    intent2.putExtra(key, message);
                                    startActivity(intent2);
                                }
                            });
                            //----------------
                        }
                        else
                        {
                            Button anotherNext = new Button(getApplicationContext());
                            anotherNext.setText(R.string.button_Next);
                            ll.addView(anotherNext);
                            anotherNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    intent2.putExtra(key, message);
                                    startActivity(intent2);
                                }
                            });
                        }
                    }
                }.execute();

            }
            else
            {
                Button anotherNext = new Button(getApplicationContext());
                anotherNext.setText(R.string.button_Next);
                ll.addView(anotherNext);
                anotherNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        intent2.putExtra(key, message);
                        startActivity(intent2);
                    }
                });
            }
        }
        else
        {
            TextView anonymousAuthorMessage = new TextView(this);
            anonymousAuthorMessage.setText(R.string.info_CampaignCreatedByAnonymous);
            ll.addView(anonymousAuthorMessage);

            Button anotherNext = new Button(getApplicationContext());
            anotherNext.setText(R.string.button_Next);
            ll.addView(anotherNext);
            anotherNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    intent2.putExtra(key, message);
                    startActivity(intent2);
                }
            });
        }


        /*
        Button next = new Button(this);
        next.setText(R.string.button_Next);
        ll.addView(next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent2.putExtra(key, message);
                startActivity(intent2);
            }
        });
        */

        ScrollView scrollView = new ScrollView(getApplication());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        scrollView.addView(ll);
        setContentView(scrollView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("Permission for GALLERY is granted for the first time");
            }
            else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                System.out.println("Permission is denied");
                if (ActivityCompat.shouldShowRequestPermissionRationale(CampaignDescription.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    System.out.println("Enter IF branch");
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important to get access to gallery.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(CampaignDescription.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    ActivityCompat.requestPermissions(CampaignDescription.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                }
                else
                {
                    //Never ask again and handle your app without permission.
                }
            }
        }



    }

}
