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

package cy.ac.ucy.cs.mandolapp.SavedReportsActivities;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.tooleap.sdk.Tooleap;
import com.tooleap.sdk.TooleapActivities;
import com.tooleap.sdk.TooleapMiniApp;

import cy.ac.ucy.cs.mandolapp.Database.ArrayString;
import cy.ac.ucy.cs.mandolapp.Database.DatabaseHelper;
import cy.ac.ucy.cs.mandolapp.PlanetAdapter;
import cy.ac.ucy.cs.mandolapp.R;
import cy.ac.ucy.cs.mandolapp.ReportActivities.Report;
import cy.ac.ucy.cs.mandolapp.ReportActivities.ReportTwitterNew;
import cy.ac.ucy.cs.mandolapp.adapters.CustomAdapter;
import cy.ac.ucy.cs.mandolapp.adapters.SearchAppAdapter;
import cy.ac.ucy.cs.mandolapp.adapters.SearchAppSavedReportsAdapter;
import cy.ac.ucy.cs.mandolapp.adapters.SearchFieldAdapter;
import cy.ac.ucy.cs.mandolapp.adapters.SearchFieldSavedReportsAdapter;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
public class SavedReports extends TooleapActivities.Activity implements PlanetAdapter.OnItemClickListener {
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;


    private long appId;
    private Tooleap tooleap;
    private TooleapMiniApp miniApp;
    private DatabaseHelper myDb;
    private Context my;

    public static boolean[] checkSelectedApp;	// store select/unselect information about the values in the list
    public static boolean[] checkSelectedField;	// store select/unselect information about the values in the list
    public ArrayList<String> Apps;
    public ArrayList<String> Fields;
    private PopupWindow pw;

    ArrayList<Report> dataModels;
    ListView listView;
    private static CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedreports);

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
                getActionBar().setTitle("Saved Reports");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }



        my = this;
        super.onCreate(savedInstanceState);
        appId = this.getTooleapAppId();
        tooleap = Tooleap.getInstance(this);
        miniApp = tooleap.getMiniApp(appId);

        listView=(ListView)findViewById(R.id.list);
        listView.setItemsCanFocus(false);


        dataModels= new ArrayList<>();
        Cursor res=null;
        try{
            myDb = new DatabaseHelper(this);


            res = myDb.getAllData();

            if(res.getCount()!=0){
                while (res.moveToNext()) {
                    String id = res.getString(0);
                    String app = res.getString(1);
                    String url = res.getString(2);
                    String description = res.getString(3);
                    String[] categories = ArrayString.convertStringToArray(res.getString(4));
                    String[] authorities = ArrayString.convertStringToArray(res.getString(5));
                    String date = res.getString(6);
                    dataModels.add(new Report(id,app,url,description,categories,authorities,date));
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (res != null && !res.isClosed()) res.close();
            myDb.close();

        }



        adapter= new CustomAdapter(dataModels,my);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("CLICKED");
                Report report= dataModels.get(position);
                try{
                    if(report.getApp().equals("twitter")){
                        Intent intent = new Intent(my,SavedReportsTwitter.class);
                        intent.putExtra("Report", report);
                        miniApp.setContentIntent(intent);
                        tooleap.updateMiniApp(appId,miniApp);
                        tooleap.showMiniApp(appId);
                        finish();
                    }else if(report.getApp().equals("facebook")){
                        Intent intent = new Intent(my,SavedReportsFacebook.class);
                        intent.putExtra("Report", report);
                        miniApp.setContentIntent(intent);
                        tooleap.updateMiniApp(appId,miniApp);
                        tooleap.showMiniApp(appId);
                        finish();
                    }
                    else if(report.getApp().equals("browser")){
                        Intent intent = new Intent(my,SavedReportsBrowser.class);
                        intent.putExtra("Report", report);
                        miniApp.setContentIntent(intent);
                        tooleap.updateMiniApp(appId,miniApp);
                        tooleap.showMiniApp(appId);
                        finish();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    myDb.close();
                }

            }
        });

        //data source for drop-down list
        final List<String> items_app = Arrays.asList(getResources().getStringArray(R.array.search_app_array));
        this.Apps = new ArrayList<String>(items_app);
        final ArrayList<String> Apps1 = Apps;

        final List<String> items_field = Arrays.asList(getResources().getStringArray(R.array.search_field_array));
        this.Fields = new ArrayList<String>(items_field);
        final ArrayList<String> Fields1 = Fields;



        checkSelectedApp = new boolean[Apps.size()];
        //initialize all values of list to 'unselected' initially
        for (int i = 0; i < checkSelectedApp.length; i++) {
            checkSelectedApp[i] = false;
        }

        checkSelectedField = new boolean[Fields.size()];
        //initialize all values of list to 'unselected' initially
        for (int i = 0; i < checkSelectedField.length; i++) {
            checkSelectedField[i] = false;
        }

        //onClickListener to initiate the dropDown list
        Button btnSearchApp = (Button)findViewById(R.id.sprSearchApp);
        btnSearchApp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                initiatePopUp(Apps1,true);
            }
        });

        //onClickListener to initiate the dropDown list
        Button btnSearchField = (Button)findViewById(R.id.sprSearchField);
        btnSearchField.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                initiatePopUp(Fields1,false);
            }
        });

        ImageButton btnSearch = (ImageButton)findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                SearchReports();
            }
        });

    }

    private void SearchReports(){
        EditText txtSearchText = (EditText)findViewById(R.id.txtSearch);
        String searchPattern = txtSearchText.getText().toString();

        DatabaseHelper DB=null;
        try{
            DB = new DatabaseHelper(my);
            boolean areAllAppsFalse = true;
            for(int i=0; i<this.Apps.size(); i++){
                if(checkSelectedApp[i]){
                    if(areAllAppsFalse){
                        areAllAppsFalse =  false;
                        dataModels.clear();
                    }
                    Cursor res=null;
                    if(checkSelectedField[0]){
                        try{
                            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                            date.parse(searchPattern);
                            res= DB.getAllDataSearchByDate(this.Apps.get(i),searchPattern);
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(my,"Date must be in the format of yyyy-MM-dd (eg. 2018-12-20)",Toast.LENGTH_LONG).show();
                        }
                    }
                    else if(checkSelectedField[1]){
                        res = DB.getAllData(this.Apps.get(i),searchPattern);
                    }
                    else{
                        res = DB.getAllData(this.Apps.get(i));
                    }
                    if(res.getCount()!=0){
                        while (res.moveToNext()) {
                            String id = res.getString(0);
                            String app = res.getString(1);
                            String url = res.getString(2);
                            String description = res.getString(3);
                            String[] categories = ArrayString.convertStringToArray(res.getString(4));
                            String[] authorities = ArrayString.convertStringToArray(res.getString(5));
                            String date = res.getString(6);
                            dataModels.add(new Report(id,app,url,description,categories,authorities,date));
                        }
                    }
                }
            }
            if(areAllAppsFalse){
                dataModels.clear();
                Cursor res=null;
                if(checkSelectedField[0]){
                    try{
                        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                        date.parse(searchPattern);
                        res= DB.getAllDataSearchByDate(searchPattern);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(my,"Date must be in the format of yyyy-MM-dd (eg. 2018-12-20)",Toast.LENGTH_LONG).show();
                    }

                }
                else if(checkSelectedField[1]){
                    res = DB.getAllDataSearchBy(searchPattern);
                }
                else{
                    res = DB.getAllData();
                }
                if(res.getCount()!=0){
                    while (res.moveToNext()) {
                        String id = res.getString(0);
                        String app = res.getString(1);
                        String url = res.getString(2);
                        String description = res.getString(3);
                        String[] categories = ArrayString.convertStringToArray(res.getString(4));
                        String[] authorities = ArrayString.convertStringToArray(res.getString(5));
                        String date = res.getString(6);
                        dataModels.add(new Report(id, app, url, description, categories, authorities, date));
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(DB!=null){
                DB.close();
            }
        }

    }

    /*
     * Function to set up the pop-up window which acts as drop-down list
     * */
    private void initiatePopUp(ArrayList<String> items, boolean isAboutCategory){

        LayoutInflater inflater = (LayoutInflater)my.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //get the pop-up window i.e.  drop-down layout
        View layout = inflater.inflate(R.layout.pop_up_window, (ViewGroup)findViewById(R.id.PopUpView));


        LinearLayout layout1;
        if(isAboutCategory){
            //get the view to which drop-down layout is to be anchored
            layout1 = (LinearLayout)findViewById(R.id.linearLayoutSearch);
        }
        else{
            layout1 = (LinearLayout)findViewById(R.id.linearLayoutSearch);
        }

        pw = new PopupWindow(layout, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

//        pw.showAsDropDown(layout1, 0, 0, Gravity.CENTER);

        //Pop-up window background cannot be null if we want the pop-up to listen touch events outside its window
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setTouchable(true);

        //let pop-up be informed about touch events outside its window. This  should be done before setting the content of pop-up
        pw.setOutsideTouchable(true);
        pw.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        //dismiss the pop-up i.e. drop-down when touched anywhere outside the pop-up
        pw.setTouchInterceptor(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });

        //provide the source layout for drop-down
        pw.setContentView(layout);

        //anchor the drop-down to bottom-left corner of 'layout1'
        pw.showAsDropDown(layout1);

        Button btn;
        if(isAboutCategory){
            btn = (Button)findViewById(R.id.sprSearchApp);
        }
        else{
            btn = (Button)findViewById(R.id.sprSearchField);
        }
        //populate the drop-down list
        final ListView list = (ListView) layout.findViewById(R.id.dropDownList);
        if(isAboutCategory){
            SearchAppSavedReportsAdapter adapter = new SearchAppSavedReportsAdapter(my, items, btn);
            list.setAdapter(adapter);
        }
        else{
            SearchFieldSavedReportsAdapter adapter = new SearchFieldSavedReportsAdapter(my, items, btn);
            list.setAdapter(adapter);
        }
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


    }

    public void DeleteReport(Report report, int position){
        DatabaseHelper DB = null;
        try{
            DB = new DatabaseHelper(my);
            DB.deleteData(report);
            dataModels.remove(position);
            adapter.notifyDataSetChanged();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(DB!=null) DB.close();
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
        Fragment fragment = PlanetFragment.newInstance(position);

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
                return;
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
        setTitle("Saved Reports");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
        setTitle("Saved Reports");
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