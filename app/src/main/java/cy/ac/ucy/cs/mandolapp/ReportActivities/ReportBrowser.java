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
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import cy.ac.ucy.cs.mandolapp.MyApplication;
import cy.ac.ucy.cs.mandolapp.MyService;
import cy.ac.ucy.cs.mandolapp.PlanetAdapter;
import cy.ac.ucy.cs.mandolapp.R;
import cy.ac.ucy.cs.mandolapp.adapters.DropDownListAdapterBrowser;
import cy.ac.ucy.cs.mandolapp.adapters.DropDownListAdapterBrowserAuth;

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
public class ReportBrowser extends TooleapActivities.Activity implements PlanetAdapter.OnItemClickListener {
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
    public ProgressBar p;
    public boolean isReportClickable;
    public static int selectedAuthoritiesNumber=0;

    private String Text;

    private PopupWindow pw;
    private boolean expanded; 		//to  store information whether the selected values are displayed completely or in shortened representatn
    public static boolean[] checkSelected;	// store select/unselect information about the values in the list
    public static boolean[] checkSelectedAuth;	// store select/unselect information about the values in the list
    private ArrayList<String> Categories;
    private ArrayList<String> Authorities;
    private String Url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_browser);

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
        isReportClickable=true;


        this.Url = MyService.Url;


        final long start = System.currentTimeMillis();


        WebView webView = (WebView)findViewById(R.id.webviewBrowser);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayoutReportBrowser);
                relativeLayout.setVisibility(View.VISIBLE);
                System.out.println(System.currentTimeMillis()-start);
            }
        });
        webView.loadUrl(this.Url);


        //data source for drop-down list
        final List<String> items_cat = Arrays.asList(getResources().getStringArray(R.array.category_array));
        this.Categories = new ArrayList<String>(items_cat);
        final ArrayList<String> Categories1 = Categories;

        final List<String> items_auth = Arrays.asList(getResources().getStringArray(R.array.authority_array));
        this.Authorities = new ArrayList<String>(items_auth);
        final ArrayList<String> Authorities1 = Authorities;



        checkSelected = new boolean[Categories.size()];
        //initialize all values of list to 'unselected' initially
        for (int i = 0; i < checkSelected.length; i++) {
            checkSelected[i] = false;
        }

        SharedPreferences sharedPref = MyApplication.getAppContext().getSharedPreferences("MandolappSettings",Context.MODE_PRIVATE);

        String SelectedAuths = sharedPref.getString("DefaultAuthorities", null);
        Button chooseAuthButton = (Button)findViewById(R.id.sprAuthorityBrowser);
        if(SelectedAuths!=null){
            String[] auths = ArrayString.convertStringToArray(SelectedAuths);
            List<String> authList = new ArrayList<String>(Arrays.asList(auths));

            checkSelectedAuth = new boolean[Authorities.size()];
            //initialize all values of list to 'unselected' initially
            for (int i = 0; i < checkSelectedAuth.length; i++) {
                if(authList.contains(Authorities1.get(i))){
                    checkSelectedAuth[i]=true;
                }
                else{
                    checkSelectedAuth[i] = false;
                }
            }

            int selectedItems = authList.size();
            this.selectedAuthoritiesNumber = selectedItems;
            if(selectedAuthoritiesNumber==1 && authList.get(0).equals("")) selectedAuthoritiesNumber=0;
            if(authList.size()==1 && selectedAuthoritiesNumber==1){
                chooseAuthButton.setText(authList.get(0));
            }else if(authList.size()>1){
                chooseAuthButton.setText(authList.get(0) + " & " + (selectedItems-1) + " more");
            }

        }else{
            checkSelectedAuth = new boolean[Authorities.size()];
            //initialize all values of list to 'unselected' initially
            for (int i = 0; i < checkSelectedAuth.length; i++) {
                checkSelectedAuth[i] = false;
            }
        }


        DropDownListAdapterBrowser.selectedCount=0;
        DropDownListAdapterBrowserAuth.selectedCount=0;

        //onClickListener to initiate the dropDown list
        Button createButton = (Button)findViewById(R.id.sprCategoryBrowser);
        createButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println("CAT button");
                initiatePopUp(Categories1,true);
            }
        });

        //onClickListener to initiate the dropDown list
        chooseAuthButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println("AUTH Button");
                initiatePopUp(Authorities1,false);
            }
        });

        Button btnCancel = (Button)findViewById(R.id.btnCancelBrowser);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button btnSave = (Button)findViewById(R.id.btnSaveLaterBrowser);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean saved = saveReport();
                if(saved){
                    onBackPressed();

                }
            }
        });

        Button btnReport = (Button)findViewById(R.id.btnReportBrowser);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isReportClickable){
                    try {

                        ReportNow();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

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
            layout1 = (LinearLayout)findViewById(R.id.linearLayoutCategoryBrowser);
        }
        else{
            layout1 = (LinearLayout)findViewById(R.id.linearLayoutAuthorityBrowser);
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
            btn = (Button)findViewById(R.id.sprCategoryBrowser);
        }
        else{
            btn = (Button)findViewById(R.id.sprAuthorityBrowser);
        }
        //populate the drop-down list
        final ListView list = (ListView) layout.findViewById(R.id.dropDownList);
        if(isAboutCategory){
            DropDownListAdapterBrowser adapter = new DropDownListAdapterBrowser(my, items, btn);
            list.setAdapter(adapter);
        }
        else{
            DropDownListAdapterBrowserAuth adapter = new DropDownListAdapterBrowserAuth(my, items, btn);
            list.setAdapter(adapter);
        }

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(my,ReportBrowserFirst.class);
        miniApp.setContentIntent(intent);
        tooleap.updateMiniAppAndNotify(appId,miniApp);
        tooleap.showMiniApp(appId);
        MyService.CreateAsyncTask();

        finish();
    }

    @Override
    protected void onDestroy() {
        selectedAuthoritiesNumber=0;
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

    private boolean saveReport() {

        try{
            DB = new DatabaseHelper(this);

            String description = ((EditText)findViewById(R.id.txtDescriptionBrowser)).getText().toString();
            ArrayList<String> Categories = new ArrayList<>();
            ArrayList<String> Authorities = new ArrayList<>();
            if (checkSelected.length == this.Categories.size()) {
                for (int i = 0; i < checkSelected.length; i++) {
                    if (checkSelected[i]) {
                        Categories.add(this.Categories.get(i));
                    }
                }
            }
            if (checkSelectedAuth.length == this.Authorities.size()) {
                for (int i = 0; i < checkSelectedAuth.length; i++) {
                    if (checkSelectedAuth[i]) {
                        Authorities.add(this.Authorities.get(i));
                    }
                }
            }

            String[] CategoriesArray = Categories.toArray(new String[Categories.size()]);
            String[] AuthoritiesArray = Authorities.toArray(new String[Authorities.size()]);

            System.out.println("Cats: " + ArrayString.convertArrayToString(CategoriesArray));

            Report report = new Report("browser",this.Url,description,CategoriesArray,AuthoritiesArray);

            boolean result = DB.insertData(report);
            if (!result) {
                Toast.makeText(this, "Error. Couldn't save report", Toast.LENGTH_LONG).show();
                return false;
            }
            Toast.makeText(this, "Report saved", Toast.LENGTH_LONG).show();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            DB.close();
        }


    }

    private boolean ReportNow() {


        String description = ((EditText) findViewById(R.id.txtDescriptionBrowser)).getText().toString();
        ArrayList<String> Categories = new ArrayList<>();
        ArrayList<String> Authorities = new ArrayList<>();
        if (checkSelected.length == this.Categories.size()) {
            for (int i = 0; i < checkSelected.length; i++) {
                if (checkSelected[i]) {
                    Categories.add(this.Categories.get(i));
                }
            }
        }
        if (checkSelectedAuth.length == this.Authorities.size()) {
            for (int i = 0; i < checkSelectedAuth.length; i++) {
                if (checkSelectedAuth[i]) {
                    Authorities.add(this.Authorities.get(i));
                }
            }
        }

        if (!(Categories.size() > 0)) {
            System.out.println("cat empty");
            Toast.makeText(my, "Category field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!(Authorities.size() > 0)) {
            Toast.makeText(my, "Authority field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        isReportClickable = false;
        RelativeLayout r = (RelativeLayout) findViewById(R.id.relativeLayoutReportBrowser);

        p = new ProgressBar(my);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1000, 1000);

        p.setLayoutParams(params);


        r.addView(p);


        String[] CategoriesArray = Categories.toArray(new String[Categories.size()]);
        String[] AuthoritiesArray = Authorities.toArray(new String[Authorities.size()]);

        Report reportToSend = new Report("browser", this.Url, description, CategoriesArray, AuthoritiesArray);



        new CallAPI(reportToSend,my).execute("");

        return true;

    }

    private class CallAPI extends AsyncTask<String, Void, Boolean> {
        private Report reportToSend;
        private String UrlString;
        private String data;
        private Context context;

        public CallAPI(Report reportToSend, Context context){
            this.reportToSend = reportToSend;
            this.UrlString = APIurls.SendReportUrl;
            this.data = "{ \"id\":\""+this.reportToSend.getId()+"\",\"App\":\""+reportToSend.getApp()+"\",\"Url\":\""+reportToSend.getUrl()+"\",\"Description\":\""+reportToSend.getDescription()+"\",\"Category\":\"" + ArrayString.convertArrayToString(reportToSend.getCategories())+"\",\"Authority\":\"" + ArrayString.convertArrayToString(reportToSend.getAuthorities())+"\"}";
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {


            OutputStream out = null;
            try {

                long start = System.currentTimeMillis();

                URL url = new URL(UrlString);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");

                out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));

                writer.write(data);

                writer.flush();

                writer.close();

                out.close();

                urlConnection.connect();

                System.out.println(data);

                System.out.println(urlConnection.getResponseMessage());

                System.out.println(urlConnection.getResponseCode());

                if(urlConnection.getResponseCode()==200){
                    System.out.println(System.currentTimeMillis()-start);
                    return true;

                }else{
                    return false;
                }

            } catch (Exception e) {

                System.out.println(e.getMessage());
                return false;


            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                DatabaseHelper DB2=null;
                try{
                    DB2 = new DatabaseHelper(context);
                    DB2.insertDataSent(reportToSend);
                    DB2.close();
                    DB2=null;


                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    if(DB2!=null){
                        DB2.close();
                        DB2=null;
                    }

                    isReportClickable=true;
                    p.setVisibility(View.GONE);
                    p=null;
                }

                Toast.makeText(context,"Report Sent", Toast.LENGTH_LONG).show();
                onBackPressed();
            }else{
                Toast.makeText(context,"Error. Couldn't send report", Toast.LENGTH_LONG).show();
                isReportClickable=true;
                p.setVisibility(View.GONE);
                p=null;
            }




        }
    }
}