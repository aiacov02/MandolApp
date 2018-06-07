package cy.ac.ucy.cs.mandolapp.Intro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import cy.ac.ucy.cs.mandolapp.MyActivity;
import cy.ac.ucy.cs.mandolapp.MyApplication;
import cy.ac.ucy.cs.mandolapp.R;

public class IntroActivity extends AppIntro2 {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(IntroFragment2.newInstance("Welcome", getResources().getString(R.string.intro_slide1), R.drawable.ic_launcher));
        addSlide(IntroFragment2.newInstance("Tutorial",getResources().getString(R.string.intro_slide3)));
        addSlide(IntroFragment.newInstance("Tutorial",getResources().getString(R.string.intro_slide4),R.drawable.t1));
        addSlide(IntroFragment.newInstance("Tutorial",getResources().getString(R.string.intro_slide5),R.drawable.t2));
        addSlide(IntroFragment.newInstance("Tutorial",getResources().getString(R.string.intro_slide5_1),R.drawable.t3));
        addSlide(IntroFragment.newInstance("Tutorial",getResources().getString(R.string.intro_slide6),R.drawable.t4));
        addSlide(IntroFragment.newInstance("Tutorial",getResources().getString(R.string.intro_slide6_1),R.drawable.t5));
        addSlide(IntroFragment.newInstance("Tutorial",getResources().getString(R.string.intro_slide7),R.drawable.t6));
        addSlide(IntroFragment.newInstance("Tutorial",getResources().getString(R.string.intro_slide8),R.drawable.t7));








        // Override bar/separator color
        //setBarColor(Color.parseColor("#3F51B5"));
        //setSeparatorColor(Color.parseColor("#2196F3"));

        // SHOW or HIDE the statusbar
        showStatusBar(true);

        // Edit the color of the nav bar on Lollipop+ devices
        setNavBarColor(R.color.dark_grey);

        // Hide Skip/Done button
        //showSkipButton(true);
        showDoneButton(true);

        // Animations -- use only one of the below. Using both could cause errors.
        setFadeAnimation(); // OR
        //setZoomAnimation(); // OR
        //setFlowAnimation(); // OR
        //setSlideOverAnimation(); // OR
        //setDepthAnimation(); // OR
        //setCustomTransformer(yourCustomTransformer);


    }

    @Override
    public void onSkipPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to skip the intro tutorial? You can find it any time in the help section")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sharedPref = MyApplication.getAppContext().getSharedPreferences("MandolappSettings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("FirstTime", false);
                        editor.apply();
                        Intent intent = new Intent(IntroActivity.this, MyActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        // Do something when users tap on Skip button.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        SharedPreferences sharedPref = MyApplication.getAppContext().getSharedPreferences("MandolappSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("FirstTime", false);
        editor.apply();
        Intent intent = new Intent(IntroActivity.this, MyActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when slide is changed
    }



}


