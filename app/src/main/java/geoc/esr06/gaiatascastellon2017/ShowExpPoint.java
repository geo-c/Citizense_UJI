package geoc.esr06.gaiatascastellon2017;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import GeoC_QuestionHierarchy.Participant;
import GeoC_QuestionHierarchy.Participant_Deserializer;

public class ShowExpPoint extends AppCompatActivity {
    String message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Participant.class, new Participant_Deserializer());
        final Gson gson = gsonBuilder.create();

        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.showPoint);
        System.out.println("ShowExpPoint activity receives " + message);

        final Participant participant = gson.fromJson(message, Participant.class);

        System.out.println("userID is " + participant.getUserID() + " and expPoint is " + participant.getExPPoint());

        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams llLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(llLP);
        setContentView(ll);

        //Change status bar color
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLUE);
        }

        List<String> listCampaignPrize = participant.getListPrize();
        String listCampaignPrize_Text = "";
        for (int i=0;i< listCampaignPrize.size();i++)
            listCampaignPrize_Text += ( listCampaignPrize.get(i) + ", ");

        TextView tv = new TextView(this);
        tv.setText("userID is " + participant.getUserID() + " and expPoint is " + participant.getExPPoint() + ", next rank is " + participant.getNextRank()
                + ", money is  " + participant.getMoney() + "\n" + listCampaignPrize_Text);
        //ll.addView(tv);

        TextView tv1 = new TextView(this);
        tv1.setText(R.string.yourUserID);
        ll.addView(tv1);
        TextView tv1_value = new TextView(this);
        tv1_value.setText(participant.getUserID());
        ll.addView(tv1_value);

        TextView tv2 = new TextView(this);
        tv2.setText(R.string.yourExpPoint);
        ll.addView(tv2);
        TextView tv2_value = new TextView(this);
        tv2_value.setText(String.valueOf(participant.getExPPoint()));
        ll.addView(tv2_value);

        TextView tv3 = new TextView(this);
        tv3.setText(R.string.yourNextRank);
        ll.addView(tv3);
        TextView tv3_value = new TextView(this);
        tv3_value.setText(String.valueOf(participant.getNextRank()));
        ll.addView(tv3_value);

        TextView tv4 = new TextView(this);
        tv4.setText(R.string.yourMoney);
        tv4.setTextIsSelectable(true);
        ll.addView(tv4);
        TextView tv4_value = new TextView(this);
        tv4_value.setText(String.valueOf(participant.getMoney()));
        ll.addView(tv4_value);

        TextView tv5 = new TextView(this);
        tv5.setText(R.string.yourPrize);
        ll.addView(tv5);
        TextView tv5_value = new TextView(this);
        tv5_value.setText(listCampaignPrize_Text);
        ll.addView(tv5_value);


        Button homepage = new Button(this);
        homepage.setText(R.string.button_homepage);
        ll.addView(homepage);
        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });





    }

}
