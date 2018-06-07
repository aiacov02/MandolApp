package cy.ac.ucy.cs.mandolapp;//package com.example.myapp;
//
//import android.content.Context;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.webkit.WebView;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.tooleap.sdk.TooleapActivities;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class MyTooleapActivity2 extends TooleapActivities.Activity {
//
//
//    MultiSelectionSpinner spinner;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.miniapp2);
//
//        // Multi spinner
//        spinner = (MultiSelectionSpinner) findViewById(R.id.mySpinner1);
//
//        Button bt = (Button) findViewById(R.id.getSelected);
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String s = spinner.getSelectedItemsAsString();
//                Log.e("getSelected", s);
//
//            }
//        });
//
//    }
//
//
//
//}
//
