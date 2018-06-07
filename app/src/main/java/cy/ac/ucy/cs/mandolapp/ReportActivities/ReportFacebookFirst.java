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

package cy.ac.ucy.cs.mandolapp.ReportActivities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tooleap.sdk.Tooleap;
import com.tooleap.sdk.TooleapActivities;
import com.tooleap.sdk.TooleapMiniApp;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import cy.ac.ucy.cs.mandolapp.Database.DatabaseHelper;
import cy.ac.ucy.cs.mandolapp.PlanetAdapter;
import cy.ac.ucy.cs.mandolapp.R;


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
public class ReportFacebookFirst extends TooleapActivities.Activity implements PlanetAdapter.OnItemClickListener {
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;


    private long appId;
    private Tooleap tooleap;
    private TooleapMiniApp miniApp;
    private Context my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_facebook_first);

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
        super.onCreate(savedInstanceState);
        //myDb = new DatabaseHelper(this);
        appId = this.getTooleapAppId();
        tooleap = Tooleap.getInstance(this);
        miniApp = tooleap.getMiniApp(appId);


        SharedPreferences sharedPref = this.getSharedPreferences("MandolappSettings",Context.MODE_PRIVATE);

        boolean isToggleChecked = sharedPref.getBoolean("ReportGuidelines", true);


        if(!isToggleChecked){
            pl.droidsonroids.gif.GifTextView gif = (pl.droidsonroids.gif.GifTextView)findViewById(R.id.gifviewFacebook);
            gif.setVisibility(View.GONE);
            TextView textView = (TextView)findViewById(R.id.txtFacebookFirst);
            textView.setText("Please copy the link of a public facebook post that you wish to report");
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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


    private static boolean isValidURL(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception exception)
        {
            return false;
        }
    }




    // Uses an AsyncTask to download a Twitter user's timeline
//    private class CheckAppListener extends AsyncTask<String, Void, String> {
//
//
//        @Override
//        protected String doInBackground(String... screenNames) {
//
//            String result = null;
//            System.err.println("IN AsyncTask");
//            while(true){
//                if(isCancelled()){
//                    return "Exit";
//                }
//                System.err.println("inside while loop");
//                String currentApp = "NULL";
//                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    UsageStatsManager usm = (UsageStatsManager) my.getSystemService(Context.USAGE_STATS_SERVICE);
//                    long time = System.currentTimeMillis();
//                    List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
//                    if (appList != null && appList.size() > 0) {
//                        SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
//                        for (UsageStats usageStats : appList) {
//                            mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
//                        }
//                        if (mySortedMap != null && !mySortedMap.isEmpty()) {
//                            currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
//                        }
//                    }
//                } else {
//                    ActivityManager am = (ActivityManager)my.getSystemService(Context.ACTIVITY_SERVICE);
//                    List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
//                    currentApp = tasks.get(0).processName;
//                }
////                String[] app = currentApp.split("\\.");
//                System.err.println(currentApp);
////                System.err.println(app[0]);
//                System.err.println("ReportFacebookFirst");
//                if(currentApp!=null && currentApp!="") {
//                    if (currentApp.equals("com.twitter.android")) {
//                        return "twitter";
//                    }
//
//                    else if(currentApp.equals("com.android.chrome") || currentApp.equals("com.android.browser") || currentApp.equals("org.mozilla.firefox") || currentApp.equals("com.opera.browser") || currentApp.equals("com.UCMobile.intl")){
//                        return "browser";
//                    }
//                    else{
//                        if(!(currentApp.equals("com.example.myapp") || currentApp.equals("com.facebook.katana") || currentApp.equals("com.android.systemui"))){
//                            return "else";
//                        }
//                    }
//                }
//
//
//
//                try{
//                    Thread.sleep(1000);
//
//                }
//                catch (InterruptedException e){
//                    //Swallow exception
//                }
//            }
//        }
//
//
//        // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
//        @Override
//        protected void onPostExecute(String result) {
//            if(result.equals("Exit")){
//                return;
//            }
//            if(result.equals("twitter")){
//                Intent intent = new Intent(my,ReportTwitterFirst.class);
//                miniApp.setContentIntent(intent);
//                miniApp.notificationText="Hello";
//                miniApp.notificationBadgeNumber=1;
//                tooleap.updateMiniAppAndNotify(appId,miniApp);
//                tooleap.showMiniApp(appId);
//                ((Activity)my).finish();
//            }
//            else if(result.equals("browser")){
//                Intent intent = new Intent(my,ReportBrowserFirst.class);
//                miniApp.setContentIntent(intent);
//                miniApp.notificationText="Hello";
//                miniApp.notificationBadgeNumber=1;
//                tooleap.updateMiniAppAndNotify(appId,miniApp);
//                tooleap.showMiniApp(appId);
//                ((Activity)my).finish();
//            }
//            else if(result.equals("else")){
//                Intent intent = new Intent(my,NavigationDrawerActivity.class);
//                miniApp.setContentIntent(intent);
//                miniApp.notificationText="Hello";
//                miniApp.notificationBadgeNumber=1;
//                tooleap.updateMiniAppAndNotify(appId,miniApp);
//                tooleap.showMiniApp(appId);
//                ((Activity)my).finish();
//
//            }
//
//        }
//
//        @Override
//        protected void onCancelled(String result){
//            return;
//
//        }
//
//
//    }


}