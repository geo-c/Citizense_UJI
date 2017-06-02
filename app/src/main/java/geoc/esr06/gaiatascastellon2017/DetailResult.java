package geoc.esr06.gaiatascastellon2017;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import GeoC_QuestionHierarchy.Answer;
import GeoC_QuestionHierarchy.Answer_Deserializer;
import GeoC_QuestionHierarchy.DateTimeConverter;

public class DetailResult extends AppCompatActivity {

    String reply;

    public static String FreeTextSingle = "FreeTextSingle";
    public static String FreeTextMulti = "FreeTextMulti";
    public static String MultipleChoiceSingle = "MultipleChoiceSingle";
    public static String MultipleChoiceMulti = "MultipleChoiceMulti";
    public static String ContRange = "ContRange";
    public static String AudioSensor = "AudioSensor";
    public static String TextDisplay = "TextDisplay";
    public static String FreeNumericSingle = "FreeNumericSingle";
    public static String UploadPhoto = "UploadPhoto";

    //LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        reply = intent.getStringExtra(ListOfQuestion.KEY1);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Answer.class, new Answer_Deserializer());
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeConverter());
        final Gson gson = gsonBuilder.create();

        System.out.println("Reply is " + reply);

        Answer[] answerArray = gson.fromJson(reply, Answer[].class);
        System.out.println("It contains " + answerArray.length + " elements");

        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams llLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(llLP);
        setContentView(ll);


        if (answerArray.length > 0)
        {
            String quesType = answerArray[0].getQuestionType();

            if (quesType.equals(FreeTextSingle))
            {
                String[] content = new String[answerArray.length];
                for (int i=0;i < answerArray.length;i++)
                {
                    content[i] = (String) answerArray[i].getList().get(0);
                }

                ListView DynamicListView = new ListView(this);

                ArrayAdapter<String> adapter = new ArrayAdapter<String> (DetailResult.this, android.R.layout.simple_list_item_1, content);

                DynamicListView.setAdapter(adapter);

                ll.addView(DynamicListView);

            }
            else if (quesType.equals(FreeTextMulti))
            {
                int numberOfSubquestion = answerArray[0].getList().size();

                String[] content = new String[answerArray.length*numberOfSubquestion];
                for (int i=0;i < answerArray.length;i++)
                {
                    for (int j=0;j<numberOfSubquestion;j++)
                    {
                        content[i*numberOfSubquestion+j] = answerArray[i].getList().get(j).toString();
                    }
                }

                ListView DynamicListView = new ListView(this);

                ArrayAdapter<String> adapter = new MyAdapter<String> (DetailResult.this, android.R.layout.simple_list_item_1, content, numberOfSubquestion);

                DynamicListView.setAdapter(adapter);

                ll.addView(DynamicListView);


            }
            else if (quesType.equals(MultipleChoiceSingle))
            {
                Set<String> optionSet = new HashSet();

                String[] content = new String[answerArray.length];
                for (int i=0;i < answerArray.length;i++)
                {
                    content[i] = (String) answerArray[i].getList().get(0);
                    optionSet.add(content[i]);
                }

                System.out.println("The set containing all options is " + optionSet.toString());

                ArrayList<String> optionList = new ArrayList(optionSet);
                System.out.println("The list containing all options is " + optionList.toString());

                int[] count = new int[optionList.size()];
                for (int x=0;x< count.length;x++) count[x] =0;

                for (int i=0;i< content.length;i++)
                {
                    for (int j=0;j<optionList.size();j++)
                    {
                        if ( content[i].equals(optionList.get(j)))
                        {
                            count[j]++;
                        }
                    }
                }

                System.out.println("Value of count array is " + Arrays.toString(count));

                for (int i=0;i< optionList.size();i++)
                {
                    TextView optionText = new TextView(this);
                    optionText.setText(optionList.get(i));
                    ll.addView(optionText);

                    TextView optionCount = new TextView(this);
                    optionCount.setText(String.valueOf(count[i]));
                    ll.addView(optionCount);
                }
            }
            else if (quesType.equals(MultipleChoiceMulti))
            {
                Set<String> optionSet = new HashSet();
                for (int i=0;i < answerArray.length;i++)
                {
                    for (int j=0;j< answerArray[i].getList().size();j++)
                    {
                        optionSet.add( (String) answerArray[i].getList().get(j) );
                    }
                }
                System.out.println("The set containing all options is " + optionSet.toString());

                ArrayList<String> optionList = new ArrayList(optionSet);
                System.out.println("The list containing all options is " + optionList.toString());

                int[] count = new int[optionList.size()];
                for (int x=0;x< count.length;x++) count[x] =0;

                for (int i=0;i < answerArray.length;i++)
                {
                    for (int j=0;j< answerArray[i].getList().size();j++)
                    {
                        for (int k=0;k< optionList.size();k++)
                        {
                            if ( answerArray[i].getList().get(j).toString().equals( optionList.get(k) ) )
                            {
                                count[k]++;
                            }
                        }
                    }
                }
                System.out.println("Value of count array is " + Arrays.toString(count));

                for (int i=0;i< optionList.size();i++)
                {
                    TextView optionText = new TextView(this);
                    optionText.setText(optionList.get(i));
                    ll.addView(optionText);

                    TextView optionCount = new TextView(this);
                    optionCount.setText(String.valueOf(count[i]));
                    ll.addView(optionCount);
                }
            }
            else if (quesType.equals(TextDisplay))
            {
                TextView tv = new TextView(this);
                tv.setText("There is no answer for this task. Please come back to see the results of other tasks");
                ll.addView(tv);
            }
            else if (quesType.equals(FreeNumericSingle))
            {
                double sum = 0,max = Double.MIN_VALUE , min = Double.MAX_VALUE;

                ArrayList valueArray = new ArrayList();

                for (int i=0;i < answerArray.length;i++)
                {
                    if ( answerArray[i].getList().size() == 0 )
                    {
                        //Skip a blank answer
                    }
                    else
                    {
                        valueArray.add( (double) answerArray[i].getList().get(0) );

                        if ( (double) answerArray[i].getList().get(0) > max ) max = (double) answerArray[i].getList().get(0);
                        if ( (double) answerArray[i].getList().get(0) < min ) min = (double) answerArray[i].getList().get(0);
                    }
                }

                for (int i=0;i < valueArray.size();i++)
                {
                    sum += (double) valueArray.get(i);
                }
                System.out.println("Size of valueArray is " + valueArray.size() + " and sum is " + sum);
                System.out.println("Max is " + max + " and min is " + min);

                double average = sum / valueArray.size();

                double temp1 =0;

                for (int i=0;i < valueArray.size();i++)
                {
                    temp1 += Math.pow( (( (double)valueArray.get(i))-average),2);
                }
                double temp2 = temp1 / valueArray.size();

                double staDev = Math.sqrt(temp2);

                TextView averageText = new TextView(this);
                averageText.setText("The average value is: ");
                ll.addView(averageText);
                TextView averageValue = new TextView(this);
                averageValue.setText(String.valueOf(average));
                ll.addView(averageValue);

                TextView maxText = new TextView(this);
                maxText.setText("The max value is: ");
                ll.addView(maxText);
                TextView maxValue = new TextView(this);
                maxValue.setText("" + String.valueOf(max));
                ll.addView(maxValue);

                TextView minText = new TextView(this);
                minText.setText("The minimum value is: ");
                ll.addView(minText);
                TextView minValue = new TextView(this);
                minValue.setText(String.valueOf(min));
                ll.addView(minValue);

                TextView staDevText = new TextView(this);
                staDevText.setText("The standard deviation is: ");
                ll.addView(staDevText);
                TextView staDevValue = new TextView(this);
                staDevValue.setText(String.valueOf(staDev));
                ll.addView(staDevValue);

                System.out.println("Max is " + max + " and min is " + min + " and average is " + average + " and staDev is " + staDev);
            }
            else if (quesType.equals(AudioSensor))
            {
                int numberOfSubcomponent = 5;
                int validAns = 0,blankAns = 0;

                String[] content = new String[answerArray.length*numberOfSubcomponent];
                for (int i=0;i < answerArray.length;i++)
                {
                    if ( answerArray[i].getList().size() == 0)
                    {
                        blankAns++;
                        for (int j=0;j<numberOfSubcomponent;j++)
                            content[i*numberOfSubcomponent+j] = "0";
                    }
                    else
                    {
                        validAns++;
                        content[i*numberOfSubcomponent+0] = "Min: " + String.valueOf( (double) answerArray[i].getList().get(0) ) + " dB";
                        content[i*numberOfSubcomponent+1] = "Average: " + String.valueOf( (double) answerArray[i].getList().get(1) ) + " dB";
                        content[i*numberOfSubcomponent+2] = "Max: " + String.valueOf( (double) answerArray[i].getList().get(2) ) + " dB";
                        content[i*numberOfSubcomponent+3] = "Duration: " + String.valueOf( (double) answerArray[i].getList().get(3) ) + " msec";
                        content[i*numberOfSubcomponent+4] = "Sampling frequency: " + String.valueOf( (double) answerArray[i].getList().get(4) ) + " msec";
                    }
                }

                ListView DynamicListView = new ListView(this);
                ArrayAdapter<String> adapter = new MyAdapter<String> (DetailResult.this, android.R.layout.simple_list_item_1, content, numberOfSubcomponent);
                DynamicListView.setAdapter(adapter);

                TextView validText = new TextView(this);
                validText.setText("The number of valid measurement is: ");
                ll.addView(validText);
                TextView validValue = new TextView(this);
                validValue.setText(String.valueOf(validAns));
                ll.addView(validValue);

                TextView blankText = new TextView(this);
                blankText.setText("The number of blank measurement is: ");
                ll.addView(blankText);
                TextView blankValue = new TextView(this);
                blankValue.setText("" + String.valueOf(blankAns));
                ll.addView(blankValue);

                ll.addView(DynamicListView);
            }
            else if (quesType.equals(ContRange))
            {
                double sum = 0,max = Double.MIN_VALUE , min = Double.MAX_VALUE;

                ArrayList valueArray = new ArrayList();

                for (int i=0;i < answerArray.length;i++)
                {
                    if ( answerArray[i].getList().size() == 0 )
                    {
                        //Skip a blank answer
                    }
                    else
                    {
                        valueArray.add( (double) answerArray[i].getList().get(0) );

                        if ( (double) answerArray[i].getList().get(0) > max ) max = (double) answerArray[i].getList().get(0);
                        if ( (double) answerArray[i].getList().get(0) < min ) min = (double) answerArray[i].getList().get(0);
                    }
                }

                for (int i=0;i < valueArray.size();i++)
                {
                    sum += (double) valueArray.get(i);
                }
                System.out.println("Size of valueArray is " + valueArray.size() + " and sum is " + sum);
                System.out.println("Max is " + max + " and min is " + min);

                double average = sum / valueArray.size();

                double temp1 =0;

                for (int i=0;i < valueArray.size();i++)
                {
                    temp1 += Math.pow( (( (double)valueArray.get(i))-average),2);
                }
                double temp2 = temp1 / valueArray.size();

                double staDev = Math.sqrt(temp2);

                TextView averageText = new TextView(this);
                averageText.setText("The average value is: ");
                ll.addView(averageText);
                TextView averageValue = new TextView(this);
                averageValue.setText(String.valueOf(average));
                ll.addView(averageValue);

                TextView maxText = new TextView(this);
                maxText.setText("The max value is: ");
                ll.addView(maxText);
                TextView maxValue = new TextView(this);
                maxValue.setText("" + String.valueOf(max));
                ll.addView(maxValue);

                TextView minText = new TextView(this);
                minText.setText("The minimum value is: ");
                ll.addView(minText);
                TextView minValue = new TextView(this);
                minValue.setText(String.valueOf(min));
                ll.addView(minValue);

                TextView staDevText = new TextView(this);
                staDevText.setText("The standard deviation is: ");
                ll.addView(staDevText);
                TextView staDevValue = new TextView(this);
                staDevValue.setText(String.valueOf(staDev));
                ll.addView(staDevValue);

                System.out.println("Max is " + max + " and min is " + min + " and average is " + average + " and staDev is " + staDev);
            }
            else if (quesType.equals(UploadPhoto))
            {
                TextView tv1_text = new TextView(this);
                tv1_text.setText("At the moment, the campaign has collected the following number of pictures");
                ll.addView(tv1_text);
                TextView tv1_value = new TextView(this);
                tv1_value.setText(String.valueOf(answerArray.length));
                ll.addView(tv1_value);

                TextView tv2_text = new TextView(this);
                tv2_text.setText("To view all the pictures, it's recommended to use the web browser on a PC or laptop at the following address:");
                ll.addView(tv2_text);
                TextView tv2_link = new TextView(this);
                tv2_link.setText("www.citizense.uji.es/webResultViewer.html?campaign=" + answerArray[0].getCampaignID());
                tv2_link.setTextColor(Color.BLUE);
                tv2_link.setTextIsSelectable(true);
                ll.addView(tv2_link);
            }

        }
        setContentView(ll);
    }

}

class MyAdapter<String> extends ArrayAdapter
{
    int blockSize;

    public MyAdapter(Context context, int resource, Object[] objects, int block) {
        super(context, resource, objects);
        this.blockSize = block;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (  (position/this.blockSize) % 2 == 1) {
            view.setBackgroundColor(Color.rgb(176,224,230));
        } else {
            view.setBackgroundColor(Color.rgb(0,153,204));
        }


        return view;
    }

}