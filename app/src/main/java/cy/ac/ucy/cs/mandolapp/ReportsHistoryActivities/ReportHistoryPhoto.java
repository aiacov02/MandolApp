/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cy.ac.ucy.cs.mandolapp.ReportsHistoryActivities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tooleap.sdk.Tooleap;
import com.tooleap.sdk.TooleapActivities;
import com.tooleap.sdk.TooleapMiniApp;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import cy.ac.ucy.cs.mandolapp.APIurls;
import cy.ac.ucy.cs.mandolapp.Database.ArrayString;
import cy.ac.ucy.cs.mandolapp.Database.DatabaseHelper;
import cy.ac.ucy.cs.mandolapp.Database.DbBitmapUtility;
import cy.ac.ucy.cs.mandolapp.PlanetAdapter;
import cy.ac.ucy.cs.mandolapp.R;
import cy.ac.ucy.cs.mandolapp.ReportActivities.NavigationDrawerActivity;
import cy.ac.ucy.cs.mandolapp.ReportActivities.Report;
import cy.ac.ucy.cs.mandolapp.adapters.DropDownListAdapterPhoto;
import cy.ac.ucy.cs.mandolapp.adapters.DropDownListAdapterPhotoAuth;

/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class ReportHistoryPhoto extends TooleapActivities.Activity implements PlanetAdapter.OnItemClickListener {
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;


    private long appId;
    private Tooleap tooleap;
    private TooleapMiniApp miniApp;
    private Report myReport;
    public DatabaseHelper myDb;
    public DatabaseHelper DB;
    private Context my;

    private String Text;

    private PopupWindow pw;
    private boolean expanded; 		//to  store information whether the selected values are displayed completely or in shortened representatn
    public static boolean[] checkSelected;	// store select/unselect information about the values in the list
    public static boolean[] checkSelectedAuth;	// store select/unselect information about the values in the list
    private ArrayList<String> Categories;
    private ArrayList<String> Authorities;
    private Bitmap Picture;
    private Report report;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporthistory_photo);

        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new PlanetAdapter(mPlanetTitles, this));
        // enable ActionBar app icon to behave as action to toggle nav drawer


        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,/* DrawerLayout object */

                R.drawable.ic_drawer_resized,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }


        my = this;
        appId = this.getTooleapAppId();
        tooleap = Tooleap.getInstance(this);
        miniApp = tooleap.getMiniApp(appId);
        String ReportID = getIntent().getExtras().get("id").toString();

        DatabaseHelper DB = null;
        try {
            DB = new DatabaseHelper(my);
            Cursor res = DB.getReportSent(ReportID);
            if(res.getCount()>0){
              while(res.moveToNext()){
                  String id = res.getString(0);
                  String app = res.getString(1);
                  String description = res.getString(3);
                  String[] categories = ArrayString.convertStringToArray(res.getString(4));
                  String[] authorities = ArrayString.convertStringToArray(res.getString(5));
                  String date = res.getString(6);
                  byte[] image = res.getBlob(7);
                  String location = res.getString(8);
                  this.report = new Report(id,app,image,location,description,categories,authorities,date);
              }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(DB!=null){
                DB.close();
            }
        }


        this.Picture = DbBitmapUtility.getImage(this.report.getImage());
        ImageView iv = (ImageView)findViewById(R.id.imgPhoto);
        iv.setImageBitmap(this.Picture);

        TextView txtDescription = (TextView) findViewById(R.id.txtDescription);
        if(this.report.getDescription()!=null && !this.report.getDescription().equals("")){
            txtDescription.setText(this.report.getDescription());
        }else{
            txtDescription.setText("-");
        }


        String Categories = "";
        for(int i=0; i<report.getCategories().length; i++){
            Categories += report.getCategories()[i] + "\n";
        }

        String Authorities = "";
        for(int i=0; i<report.getAuthorities().length; i++){
            Authorities += report.getAuthorities()[i] + "\n";
        }

        TextView txtCategories = (TextView)findViewById(R.id.txtCategory);
        txtCategories.setText(Categories);

        TextView txtAuthorities = (TextView)findViewById(R.id.txtAuthority);
        txtAuthorities.setText(Authorities);

        TextView txtDate = (TextView)findViewById(R.id.txtDate);
        txtDate.setText(this.report.getDate());



        Button btnClear = (Button)findViewById(R.id.btnClearPhoto);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper DB=null;
                try{
                    DB = new DatabaseHelper(my);
                    DB.deleteDataSent(report);

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(my,"Couldn't clear report",Toast.LENGTH_LONG).show();
                    if(DB!=null){
                        DB.close();
                    }
                    return;
                }
                finally {
                    if(DB!=null){
                        DB.close();
                    }
                }
                Toast.makeText(my,"Report cleared",Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });


    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(my,ReportHistory.class);
        miniApp.setContentIntent(intent);
        tooleap.updateMiniAppAndNotify(appId,miniApp);
        tooleap.showMiniApp(appId);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* The click listener for RecyclerView in the navigation drawer */
    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = NavigationDrawerActivity.PlanetFragment.newInstance(position);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

        // update selected item title, then close the drawer
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);

        if(my!=null){
            String activityToStart = "";
            if(mPlanetTitles[position].equals("Report")){
                activityToStart = "cy.ac.ucy.cs.mandolapp.ReportActivities.NavigationDrawerActivity";
            }else if (mPlanetTitles[position].equals("About")) {
                activityToStart = "cy.ac.ucy.cs.mandolapp.AboutActivities.About";
            }else if(mPlanetTitles[position].equals("Help")){
                activityToStart = "cy.ac.ucy.cs.mandolapp.HelpActivities.Help";
            }
            else if (mPlanetTitles[position].equals("Saved Reports")) {
                activityToStart = "cy.ac.ucy.cs.mandolapp.SavedReportsActivities.SavedReports";
            }
            else if(mPlanetTitles[position].equals("Report History")){
                activityToStart = "cy.ac.ucy.cs.mandolapp.ReportsHistoryActivities.ReportHistory";
            }
            else if(mPlanetTitles[position].equals("Statistics")){
                activityToStart = "cy.ac.ucy.cs.mandolapp.StatisticsActivities.Statistics";
            }
            else if(mPlanetTitles[position].equals("Settings")){
                activityToStart = "cy.ac.ucy.cs.mandolapp.SettingsActivities.Settings";
            }
            try {
                Class<?> c = Class.forName(activityToStart);
                Intent intent = new Intent(my,c);
                miniApp.setContentIntent(intent);
                tooleap.updateMiniAppAndNotify(appId,miniApp);
                tooleap.showMiniApp(appId);
                ((Activity)my).finish();
            } catch (ClassNotFoundException ignored) {
                ignored.printStackTrace();
            }

        }


    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        public static Fragment newInstance(int position) {
            Fragment fragment = new PlanetFragment();
            Bundle args = new Bundle();
            args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.planets_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
            ImageView iv = ((ImageView) rootView.findViewById(R.id.image));
            iv.setImageResource(imageId);

            getActivity().setTitle(planet);
            return rootView;
        }
    }

}