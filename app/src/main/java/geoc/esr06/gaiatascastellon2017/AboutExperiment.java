package geoc.esr06.gaiatascastellon2017;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AboutExperiment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.BLUE);
        }

        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams llLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(llLP);

        TextView message1 = new TextView(this);
        message1.setText(R.string.about_1);
        ll.addView(message1);

        TextView space1 = new TextView(this);
        space1.setText("");
        ll.addView(space1);

        TextView message2 = new TextView(this);
        message2.setText(R.string.about_2);
        ll.addView(message2);

        TextView space2 = new TextView(this);
        space2.setText("");
        ll.addView(space2);

        TextView message3 = new TextView(this);
        message3.setText(R.string.about_3);
        ll.addView(message3);

        TextView space3 = new TextView(this);
        space3.setText("");
        ll.addView(space3);

        TextView message4 = new TextView(this);
        message4.setText(R.string.about_4);
        ll.addView(message4);

        TextView space4 = new TextView(this);
        space4.setText("");
        ll.addView(space4);

        TextView message5 = new TextView(this);
        message5.setText(R.string.about_5);
        ll.addView(message5);

        TextView space5 = new TextView(this);
        space5.setText("");
        ll.addView(space5);

        TextView message6 = new TextView(this);
        message6.setText(R.string.about_6);
        ll.addView(message6);

        ScrollView scrollView = new ScrollView(getApplication());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        scrollView.addView(ll);
        setContentView(scrollView);

    }

}
