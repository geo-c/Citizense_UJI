package geoc.esr06.gaiatascastellon2017;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import GeoC_QuestionHierarchy.BaseQuestion_Deserializer;
import GeoC_QuestionHierarchy.Base_Question;
import GeoC_QuestionHierarchy.Branch;
import GeoC_QuestionHierarchy.Branch_Deserializer;
import GeoC_QuestionHierarchy.Campaign;
import GeoC_QuestionHierarchy.Campaign_Deserializer;
import GeoC_QuestionHierarchy.DateTimeConverter;
import GeoC_QuestionHierarchy.Workflow_Element;
import GeoC_QuestionHierarchy.Workflow_Element_Deserializer;

public class ListPublicCampaign extends AppCompatActivity {
    String message;
    public static String keyCampaignConfig = "keyCoampaignConfig";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Branch.class, new Branch_Deserializer());
        gsonBuilder.registerTypeAdapter(Workflow_Element.class, new Workflow_Element_Deserializer());
        gsonBuilder.registerTypeAdapter(Base_Question.class, new BaseQuestion_Deserializer());
        gsonBuilder.registerTypeAdapter(Campaign.class, new Campaign_Deserializer());
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeConverter());
        final Gson gson = gsonBuilder.create();

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.keyViewPublicCampaign);

        System.out.println("Received message is " + message);

        final Campaign[] Campaign_Array = gson.fromJson(message, Campaign[].class);
        System.out.println("Number of public campaigns is " + Campaign_Array.length);

        String[] listContent = new String[Campaign_Array.length];

        for (int i=0;i < Campaign_Array.length;i++)
            listContent[i] = Campaign_Array[i].getID();

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (ListPublicCampaign.this, android.R.layout.simple_list_item_1, listContent);

        DynamicListView.setAdapter(adapter);

        linearLayout.addView(DynamicListView);

        DynamicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                System.out.println("ListView item " + position + " was clicked");
                Intent intent1 = new Intent(getApplicationContext(), ListOfQuestion.class);
                intent1.putExtra(keyCampaignConfig, gson.toJson(Campaign_Array[position]));
                startActivity(intent1);
            }
        });



    }

}
