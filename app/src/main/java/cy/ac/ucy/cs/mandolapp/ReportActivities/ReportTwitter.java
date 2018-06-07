///*
// * Copyright (C) 2014 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package cy.ac.ucy.cs.mandolapp.ReportActivities;
//
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.ActionBarDrawerToggle;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.widget.RecyclerView;
//import android.util.Base64;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//import com.tooleap.sdk.Tooleap;
//import com.tooleap.sdk.TooleapActivities;
//import com.tooleap.sdk.TooleapMiniApp;
//
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//
//import cy.ac.ucy.cs.mandolapp.Database.DatabaseHelper;
//import cy.ac.ucy.cs.mandolapp.PlanetAdapter;
//import cy.ac.ucy.cs.mandolapp.R;
//import cy.ac.ucy.cs.mandolapp.adapters.DropDownListAdapter;
//import cy.ac.ucy.cs.mandolapp.adapters.DropDownListAdapterAuth;
//
//
///**
// * This example illustrates a common usage of the DrawerLayout widget
// * in the Android support library.
// * <p/>
// * <p>When a navigation (left) drawer is present, the host activity should detect presses of
// * the action bar's Up affordance as a signal to open and close the navigation drawer. The
// * ActionBarDrawerToggle facilitates this behavior.
// * Items within the drawer should fall into one of two categories:</p>
// * <p/>
// * <ul>
// * <li><strong>View switches</strong>. A view switch follows the same basic policies as
// * list or tab navigation in that a view switch does not create navigation history.
// * This pattern should only be used at the root activity of a task, leaving some form
// * of Up navigation active for activities further down the navigation hierarchy.</li>
// * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
// * parent for Up navigation. This allows a user to jump across an app's navigation
// * hierarchy at will. The application should treat this as it treats Up navigation from
// * a different task, replacing the current task stack using TaskStackBuilder or similar.
// * This is the only form of navigation drawer that should be used outside of the root
// * activity of a task.</li>
// * </ul>
// * <p/>
// * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
// * established by the Action Bar that navigation should be to the left and actions to the right.
// * An action should be an operation performed on the current contents of the window,
// * for example enabling or disabling a data overlay on top of the current content.</p>
// */
//public class ReportTwitter extends TooleapActivities.Activity implements PlanetAdapter.OnItemClickListener {
//    private DrawerLayout mDrawerLayout;
//    private RecyclerView mDrawerList;
//    private ActionBarDrawerToggle mDrawerToggle;
//
//    private CharSequence mDrawerTitle;
//    private CharSequence mTitle;
//    private String[] mPlanetTitles;
//
//
//    private long appId;
//    private Tooleap tooleap;
//    private TooleapMiniApp miniApp;
//    private Report myReport;
//    public DatabaseHelper myDb;
//    public DatabaseHelper DB;
//    private Context my;
//
//    final static String ScreenName = "";
//    final static String LOG_TAG = "";
//
//    private String Url;
//    private String id;
//
//    private PopupWindow pw;
//    private boolean expanded; 		//to  store information whether the selected values are displayed completely or in shortened representatn
//    public static boolean[] checkSelected;	// store select/unselect information about the values in the list
//    public static boolean[] checkSelectedAuth;	// store select/unselect information about the values in the list
//    public ArrayList<String> Categories;
//    public ArrayList<String> Authorities;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//
//        setContentView(R.layout.activity_report_twitter);
//
//        mTitle = mDrawerTitle = getTitle();
//        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);
//
//        // set a custom shadow that overlays the main content when the drawer opens
//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
//        // improve performance by indicating the list if fixed size.
//        mDrawerList.setHasFixedSize(true);
//
//        // set up the drawer's list view with items and click listener
//        mDrawerList.setAdapter(new PlanetAdapter(mPlanetTitles, this));
//        // enable ActionBar app icon to behave as action to toggle nav drawer
//
//
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);
//        getActionBar().setDisplayShowHomeEnabled(false);
//
//        // ActionBarDrawerToggle ties together the the proper interactions
//        // between the sliding drawer and the action bar app icon
//        mDrawerToggle = new ActionBarDrawerToggle(
//                this,                  /* host Activity */
//                mDrawerLayout,/* DrawerLayout object */
//
//                R.drawable.ic_drawer_resized,  /* nav drawer image to replace 'Up' caret */
//                R.string.drawer_open,  /* "open drawer" description for accessibility */
//                R.string.drawer_close  /* "close drawer" description for accessibility */
//        ) {
//            public void onDrawerClosed(View view) {
//                getActionBar().setTitle(mTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle(mDrawerTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//        };
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//
//        if (savedInstanceState == null) {
//            selectItem(0);
//        }
//
//
//
//        my = this;
//        DB = new DatabaseHelper(this);
//        appId = this.getTooleapAppId();
//        tooleap = Tooleap.getInstance(this);
//        miniApp = tooleap.getMiniApp(appId);
//        Url = getIntent().getExtras().get("Url").toString();
//
//
//        String words[] = Url.split("status/");
//        if(words.length>1){
//            String words2[] = words[1].split("\\?");
//            if(words2.length>1){
//                id = words2[0];
//            }
//        }
//
//        downloadTweets();
//
//
//        //data source for drop-down list
//        final List<String> items_cat = Arrays.asList(getResources().getStringArray(R.array.category_array));
//        this.Categories = new ArrayList<String>(items_cat);
//        final ArrayList<String> Categories1 = Categories;
//
//        final List<String> items_auth = Arrays.asList(getResources().getStringArray(R.array.authority_array));
//        this.Authorities = new ArrayList<String>(items_auth);
//        final ArrayList<String> Authorities1 = Authorities;
//
//
//
//        checkSelected = new boolean[Categories.size()];
//        //initialize all values of list to 'unselected' initially
//        for (int i = 0; i < checkSelected.length; i++) {
//            checkSelected[i] = false;
//        }
//
//        checkSelectedAuth = new boolean[Authorities.size()];
//        //initialize all values of list to 'unselected' initially
//        for (int i = 0; i < checkSelectedAuth.length; i++) {
//            checkSelectedAuth[i] = false;
//        }
//
//
//
//        //onClickListener to initiate the dropDown list
//        Button createButton = (Button)findViewById(R.id.create);
//        createButton.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                System.out.println("CAT button");
//                initiatePopUp(Categories1,true);
//            }
//        });
//
//        //onClickListener to initiate the dropDown list
//        Button chooseAuthButton = (Button)findViewById(R.id.create2);
//        chooseAuthButton.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                System.out.println("AUTH Button");
//                initiatePopUp(Authorities1,false);
//            }
//        });
//
//        Button btnCancel = (Button)findViewById(R.id.btnCancel);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
//
//        Button btnSave = (Button)findViewById(R.id.btnSaveLater);
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean saved = saveReport();
//                if(saved){
//                    onBackPressed();
//
//                }
//            }
//        });
//
//
//
//
//
//
//
//    }
//
//    /*
//     * Function to set up the pop-up window which acts as drop-down list
//     * */
//    private void initiatePopUp(ArrayList<String> items, boolean isAboutCategory){
//
//        LayoutInflater inflater = (LayoutInflater)my.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        //get the pop-up window i.e.  drop-down layout
//        View layout = inflater.inflate(R.layout.pop_up_window, (ViewGroup)findViewById(R.id.PopUpView));
//
//
//        LinearLayout layout1;
//        if(isAboutCategory){
//            //get the view to which drop-down layout is to be anchored
//            layout1 = (LinearLayout)findViewById(R.id.linearLayoutCategory);
//        }
//        else{
//            layout1 = (LinearLayout)findViewById(R.id.linearLayoutAuthority);
//        }
//
//        pw = new PopupWindow(layout, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//
//        pw.showAsDropDown(layout1, 0, 0, Gravity.CENTER);
//
//        //Pop-up window background cannot be null if we want the pop-up to listen touch events outside its window
//        pw.setBackgroundDrawable(new BitmapDrawable());
//        pw.setTouchable(true);
//
//        //let pop-up be informed about touch events outside its window. This  should be done before setting the content of pop-up
//        pw.setOutsideTouchable(true);
//        pw.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        //dismiss the pop-up i.e. drop-down when touched anywhere outside the pop-up
//        pw.setTouchInterceptor(new View.OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//                    pw.dismiss();
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        //provide the source layout for drop-down
//        pw.setContentView(layout);
//
//        //anchor the drop-down to bottom-left corner of 'layout1'
//        pw.showAsDropDown(layout1);
//
//        Button btn;
//        if(isAboutCategory){
//            btn = (Button)findViewById(R.id.create);
//        }
//        else{
//            btn = (Button)findViewById(R.id.create2);
//        }
//        //populate the drop-down list
//        final ListView list = (ListView) layout.findViewById(R.id.dropDownList);
//        if(isAboutCategory){
//            DropDownListAdapter adapter = new DropDownListAdapter(my, items, btn);
//            list.setAdapter(adapter);
//        }
//        else{
//            DropDownListAdapterAuth adapter = new DropDownListAdapterAuth(my, items, btn);
//            list.setAdapter(adapter);
//        }
//
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(my,ReportTwitterFirst.class);
//        miniApp.setContentIntent(intent);
//        tooleap.updateMiniAppAndNotify(appId,miniApp);
//        tooleap.showMiniApp(appId);
//        finish();
//    }
//
//    // download twitter timeline after first checking to see if there is a network connection
//    public void downloadTweets() {
//        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//
//        if (networkInfo != null && networkInfo.isConnected()) {
//            Log.v("going in","going in");
//            RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayoutReportTwitter);
//            ImageView iv = (ImageView)findViewById(R.id.imgImage);
//            TextView tv1 = (TextView)findViewById(R.id.textView1);
//            TextView tv2 = (TextView)findViewById(R.id.textView2);
//            TextView tvTweet = (TextView)findViewById(R.id.textViewTweet);
//            TextView tv3 = (TextView)findViewById(R.id.textView3);
//            new DownloadTwitterTask(iv,tv1,tv2,tv3,tvTweet,relativeLayout).execute(ScreenName);
//        } else {
//            Log.v(LOG_TAG, "No network connection available.");
//        }
//    }
//
//    // Uses an AsyncTask to download a Twitter user's timeline
//    private class DownloadTwitterTask extends AsyncTask<String, Void, String> {
//
//
//        final static String CONSUMER_KEY = "gcnlJpaaYVHVT1UQ9CcIUGDpW";
//        final static String CONSUMER_SECRET = "UCcCrJAFAOyTPKJinvs5NShwgkvVFpjOZromPNYOiuBGHjJOWk";
//        final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
//        String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/show.json?tweet_mode=extended&id=" + id;
//        Bitmap image;
//        ImageView iv;
//        TextView tv1;
//        TextView tv2;
//        TextView tv3;
//        TextView tvTweet;
//        RelativeLayout relativeLayout;
//
//        public DownloadTwitterTask(ImageView iv, TextView tv1, TextView tv2, TextView tv3, TextView tvTweet, RelativeLayout relativeLayout) {
//            this.iv = iv;
//            this.tv1 = tv1;
//            this.tv2 = tv2;
//            this.tv3 = tv3;
//            this.tvTweet = tvTweet;
//            this.relativeLayout = relativeLayout;
//        }
//
//        @Override
//        protected void onPreExecute(){
//            setProgressBarIndeterminateVisibility(true);
//        }
//
//        @Override
//        protected String doInBackground(String... screenNames) {
//            String result = null;
//
//            if (screenNames.length > 0) {
//                result = getTwitterStream(screenNames[0]);
//            }
//
//            System.out.println("##############################");
//            System.out.println(result);
//
//            Tweet tweet = jsonToTwitter(result);
//
//            Bitmap profilepic = null;
//            try {
//                InputStream in = new URL(tweet.getUser().getProfileImageUrl()).openStream();
//                profilepic = BitmapFactory.decodeStream(in);
//                this.image = profilepic;
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//
//            return result;
//        }
//
//        // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
//        @Override
//        protected void onPostExecute(String result) {
//            Tweet tweet = jsonToTwitter(result);
//
//            setProgressBarIndeterminateVisibility(false);
//
//            this.iv.setImageBitmap(this.image);
//            this.tv1.setText(tweet.getUser().getName());
//            this.tv2.setText("@" + tweet.getUser().getScreenName());
//            this.tv3.setText(tweet.getDateCreated());
//            this.tvTweet.setText(tweet.getText());
//            this.relativeLayout.setVisibility(View.VISIBLE);
//            System.out.println(tweet.getText());
//
//        }
//
//        // converts a string of JSON data into a Twitter object
//        private Tweet jsonToTwitter(String result) {
//            Twitter twits = null;
//            Tweet tweet=null;
//            if (result != null && result.length() > 0) {
//                try {
//                    Gson gson = new Gson();
//                    tweet = gson.fromJson(result, Tweet.class);
//                } catch (IllegalStateException ex) {
//                    // just eat the exception
//                }
//            }
//            return tweet;
//        }
//
//        // convert a JSON authentication object into an Authenticated object
//        private Authenticated jsonToAuthenticated(String rawAuthorization) {
//            Authenticated auth = null;
//            if (rawAuthorization != null && rawAuthorization.length() > 0) {
//                try {
//                    Gson gson = new Gson();
//                    Log.v("e", rawAuthorization);
//                    //System.exit(0);
//                    auth = gson.fromJson(rawAuthorization, Authenticated.class);
//                } catch (IllegalStateException ex) {
//                    // just eat the exception
//                }
//            }
//            return auth;
//        }
//
//        private String getResponseBody(HttpRequestBase request) {
//            StringBuilder sb = new StringBuilder();
//            try {
//
//                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
//                HttpResponse response = httpClient.execute(request);
//                int statusCode = response.getStatusLine().getStatusCode();
//                String reason = response.getStatusLine().getReasonPhrase();
//
//                if (statusCode == 200) {
//
//                    HttpEntity entity = response.getEntity();
//                    InputStream inputStream = entity.getContent();
//
//                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
//                    String line = null;
//                    while ((line = bReader.readLine()) != null) {
//                        sb.append(line);
//                    }
//                } else {
//                    sb.append(reason);
//                }
//            } catch (UnsupportedEncodingException ex) {
//            } catch (ClientProtocolException ex1) {
//            } catch (IOException ex2) {
//            }
//            return sb.toString();
//        }
//
//        private String getTwitterStream(String screenName) {
//            String results = null;
//
//            // Step 1: Encode consumer key and secret
//            try {
//                // URL encode the consumer key and secret
//                String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
//                String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");
//
//                // Concatenate the encoded consumer key, a colon character, and the
//                // encoded consumer secret
//                String combined = urlApiKey + ":" + urlApiSecret;
//
//                // Base64 encode the string
//                String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);
//
//                // Step 2: Obtain a bearer token
//                HttpPost httpPost = new HttpPost(TwitterTokenURL);
//                httpPost.setHeader("Authorization", "Basic " + base64Encoded);
//                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//                httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
//                String rawAuthorization = getResponseBody(httpPost);
//                Authenticated auth = jsonToAuthenticated(rawAuthorization);
//
//                // Applications should verify that the value associated with the
//                // token_type key of the returned object is bearer
//                if (auth != null && auth.token_type.equals("bearer")) {
//
//                    // Step 3: Authenticate API requests with bearer token
//                    HttpGet httpGet = new HttpGet(TwitterStreamURL + screenName);
//
//                    // construct a normal HTTPS request and include an Authorization
//                    // header with the value of Bearer <>
//                    httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
//                    httpGet.setHeader("Content-Type", "application/json");
//                    // update the results with the body of the response
//                    results = getResponseBody(httpGet);
//                }
//            } catch (UnsupportedEncodingException ex) {
//            } catch (IllegalStateException ex1) {
//            }
//            return results;
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
//        return true;
//    }
//
//    /* Called whenever we call invalidateOptionsMenu() */
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        // If the nav drawer is open, hide action items related to the content view
////        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
////        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // The action bar home/up action should open or close the drawer.
//        // ActionBarDrawerToggle will take care of this.
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    /* The click listener for RecyclerView in the navigation drawer */
//    @Override
//    public void onClick(View view, int position) {
//        selectItem(position);
//    }
//
//    private void selectItem(int position) {
//        // update the main content by replacing fragments
//        Fragment fragment = PlanetFragment.newInstance(position);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//        ft.replace(R.id.content_frame, fragment);
//        ft.commit();
//
//        // update selected item title, then close the drawer
//        setTitle(mPlanetTitles[position]);
//        mDrawerLayout.closeDrawer(mDrawerList);
//    }
//
//    @Override
//    public void setTitle(CharSequence title) {
//        mTitle = title;
//        getActionBar().setTitle(mTitle);
//    }
//
//    /**
//     * When using the ActionBarDrawerToggle, you must call it during
//     * onPostCreate() and onConfigurationChanged()...
//     */
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        // Pass any configuration change to the drawer toggls
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }
//
//    /**
//     * Fragment that appears in the "content_frame", shows a planet
//     */
//    public static class PlanetFragment extends Fragment {
//        public static final String ARG_PLANET_NUMBER = "planet_number";
//
//        public PlanetFragment() {
//            // Empty constructor required for fragment subclasses
//        }
//
//        public static Fragment newInstance(int position) {
//            Fragment fragment = new PlanetFragment();
//            Bundle args = new Bundle();
//            args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
//            int i = getArguments().getInt(ARG_PLANET_NUMBER);
//            String planet = getResources().getStringArray(R.array.planets_array)[i];
//
//            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
//                    "drawable", getActivity().getPackageName());
//            ImageView iv = ((ImageView) rootView.findViewById(R.id.image));
//            iv.setImageResource(imageId);
//
//            getActivity().setTitle(planet);
//            return rootView;
//        }
//    }
//
//
//    private static boolean isValidURL(String urlString)
//    {
//        try
//        {
//            URL url = new URL(urlString);
//            url.toURI();
//            return true;
//        } catch (Exception exception)
//        {
//            return false;
//        }
//    }
//
//
//    private boolean saveReport() {
//
//        String text = ((TextView) findViewById(R.id.textViewTweet)).getText().toString();
//        ArrayList<String> Categories = new ArrayList<>();
//        ArrayList<String> Authorities = new ArrayList<>();
//        if (checkSelected.length == this.Categories.size()) {
//            for (int i = 0; i < checkSelected.length; i++) {
//                if (checkSelected[i]) {
//                    Categories.add(this.Categories.get(i));
//                }
//            }
//        }
//        if (checkSelectedAuth.length == this.Authorities.size()) {
//            for (int i = 0; i < checkSelectedAuth.length; i++) {
//                if (checkSelectedAuth[i]) {
//                    Authorities.add(this.Authorities.get(i));
//                }
//            }
//        }
//
//        String[] CategoriesArray = Categories.toArray(new String[Categories.size()]);
//        String[] AuthoritiesArray = Authorities.toArray(new String[Authorities.size()]);
//
//
//        Report report = new Report("twitter",this.Url,text,CategoriesArray,AuthoritiesArray);
//
//        boolean result = DB.insertData(report);
//        if (!result) {
//            Toast.makeText(this, "Error. Couldn't save report", Toast.LENGTH_LONG).show();
//            return false;
//        }
//        Toast.makeText(this, "Report saved", Toast.LENGTH_LONG).show();
//        return true;
//    }
//}