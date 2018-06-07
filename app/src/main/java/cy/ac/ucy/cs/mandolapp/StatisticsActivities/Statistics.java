package cy.ac.ucy.cs.mandolapp.StatisticsActivities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Picture;
import android.os.Bundle;
import android.provider.Browser;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.tooleap.sdk.Tooleap;
import com.tooleap.sdk.TooleapActivities;
import com.tooleap.sdk.TooleapMiniApp;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import cy.ac.ucy.cs.mandolapp.Database.ArrayString;
import cy.ac.ucy.cs.mandolapp.Database.DatabaseHelper;
import cy.ac.ucy.cs.mandolapp.PlanetAdapter;
import cy.ac.ucy.cs.mandolapp.R;
import cy.ac.ucy.cs.mandolapp.ReportActivities.Report;
import cy.ac.ucy.cs.mandolapp.ReportActivities.Twitter;
import cy.ac.ucy.cs.mandolapp.SavedReportsActivities.SavedReports;
import cy.ac.ucy.cs.mandolapp.adapters.CustomAdapter;

public class Statistics extends TooleapActivities.Activity implements PlanetAdapter.OnItemClickListener {
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

    public static boolean[] checkSelectedApp;    // store select/unselect information about the values in the list
    public static boolean[] checkSelectedField;    // store select/unselect information about the values in the list
    public ArrayList<String> Apps;
    public ArrayList<String> Fields;
    private PopupWindow pw;

    ArrayList<Report> dataModels;
    ListView listView;
    private static CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


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
                getActionBar().setTitle("Statistics");
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


        final List<String> items_cat = Arrays.asList(getResources().getStringArray(R.array.category_array));
        ArrayList<String> Categories = new ArrayList<String>(items_cat);

        //{Picture,Browser,Twitter,Facebook}
        String[] Apps = {"Picture","Browser","Twitter","Facebook"};
        int[] NumofReports = new int[4];

        int[] NumofCategories = new int[Categories.size()];


        int TwitterCategories[] = new int[Categories.size()];
        int PictureCategories[] = new int[Categories.size()];
        int FacebookCategories[] = new int[Categories.size()];
        int BrowserCategories[] = new int[Categories.size()];

        DatabaseHelper DB=null;
        Cursor res=null;
        try{
            DB = new DatabaseHelper(my);
            res = DB.getAllDataSent();
            if(res.getCount()!=0){
                while(res.moveToNext()){
                    String[] reportCats = ArrayString.convertStringToArray(res.getString(4));
                    ArrayList<String> reportCategories = new ArrayList<String>(Arrays.asList(reportCats));
                    switch(res.getString(1)){
                        case "picture":
                            for(int i=0; i<Categories.size(); i++){
                                if(reportCategories.contains(Categories.get(i))){
                                    NumofCategories[i]++;
                                    PictureCategories[i]++;
                                }
                            }
                            NumofReports[0]++;
                            break;
                        case "browser":
                            for(int i=0; i<Categories.size(); i++){
                                if(reportCategories.contains(Categories.get(i))){
                                    NumofCategories[i]++;
                                    BrowserCategories[i]++;
                                }
                            }
                            NumofReports[1]++;
                            break;
                        case "twitter":
                            for(int i=0; i<Categories.size(); i++){
                                if(reportCategories.contains(Categories.get(i))){
                                    NumofCategories[i] ++;
                                    TwitterCategories[i] ++;
                                }
                            }
                            NumofReports[2]++;
                            break;
                        case "facebook":
                            for(int i=0; i<Categories.size(); i++){
                                if(reportCategories.contains(Categories.get(i))){
                                    NumofCategories[i] ++;
                                    FacebookCategories[i] ++;
                                }
                            }
                            NumofReports[3]++;
                            break;
                        default:break;
                    }
                }
            }
            else{
                TextView textView = (TextView)findViewById(R.id.txtNoData);
                textView.setVisibility(View.VISIBLE);
                ScrollView sc1 = (ScrollView)findViewById(R.id.scl1);
                sc1.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(res!=null && !res.isClosed()){
                res.close();
            }
            if(DB!=null){
                DB.close();
            }
        }


            PieChart chart = (PieChart)findViewById(R.id.chart);

            chart.setDrawEntryLabels(true);

            List<PieEntry> entries = new ArrayList<>();

            for(int i=0; i<NumofReports.length; i++){
                if(NumofReports[i]!=0){
                    entries.add(new PieEntry(NumofReports[i], Apps[i]));
                }
            }

            Legend legend = chart.getLegend();
            legend.setFormSize(10);
            legend.setTextSize(10);
            legend.setEnabled(true);
            legend.setWordWrapEnabled(true);


        PieDataSet set = new PieDataSet(entries, "App");
            set.setColors(new int[] { R.color.sea_green, R.color.orange_custom, R.color.twitter_blue, R.color.facebook_blue }, this);
            PieData data = new PieData(set);
            data.setValueFormatter(new MyValueFormatter());
            data.setValueTextSize(17);
            chart.setData(data);
            chart.setCenterText("Reports");
            chart.setCenterTextSize(20);
            chart.invalidate(); // refresh

            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayoutReportsPerApp);

            linearLayout.setVisibility(View.VISIBLE);



        PieChart chart2 = (PieChart)findViewById(R.id.chart2);

        chart2.setDrawEntryLabels(true);

        List<PieEntry> entries2 = new ArrayList<>();

        for(int i=0; i<NumofCategories.length; i++){
            if(NumofCategories[i]!=0){
                entries2.add(new PieEntry(NumofCategories[i], Categories.get(i)));
            }
        }

        Legend legend2 = chart2.getLegend();
        legend2.setFormSize(10);
        legend2.setTextSize(10);
        legend2.setEnabled(true);
        legend2.setWordWrapEnabled(true);

        PieDataSet set2 = new PieDataSet(entries2, "Category");
        set2.setColors(new int[] { R.color.sea_green, R.color.orange_custom, R.color.twitter_blue, R.color.facebook_blue,R.color.red,R.color.dark_grey,R.color.black_54,R.color.brown}, this);
        PieData data2 = new PieData(set2);
        data2.setValueFormatter(new MyValueFormatter());
        data2.setValueTextSize(17);
        chart2.setData(data2);
        chart2.setCenterText("Categories");
        chart2.setCenterTextSize(20);
        chart2.invalidate(); // refresh

        if(NumofReports[0]>0){
            PieChart chart3 = (PieChart)findViewById(R.id.chartPictureCat);

            chart3.setDrawEntryLabels(true);

            List<PieEntry> entries3 = new ArrayList<>();

            for(int i=0; i<PictureCategories.length; i++){
                if(PictureCategories[i]!=0){
                    entries3.add(new PieEntry(PictureCategories[i], Categories.get(i)));
                }
            }

            Legend legend3 = chart3.getLegend();
            legend3.setFormSize(10);
            legend3.setTextSize(10);
            legend3.setEnabled(true);
            legend3.setWordWrapEnabled(true);

            PieDataSet set3 = new PieDataSet(entries3, "Category");
            set3.setColors(new int[] { R.color.sea_green, R.color.orange_custom, R.color.twitter_blue, R.color.facebook_blue,R.color.red,R.color.dark_grey,R.color.black_54,R.color.brown}, this);
            PieData data3 = new PieData(set3);
            data3.setValueFormatter(new MyValueFormatter());
            data3.setValueTextSize(17);
            chart3.setData(data3);
            chart3.setCenterText("Picture");
            chart3.setCenterTextSize(20);
            chart3.invalidate(); // refresh
        }else{
            PieChart chart3 = (PieChart)findViewById(R.id.chartPictureCat);
            chart3.setVisibility(View.GONE);
        }

        if(NumofReports[1]>0){
            PieChart chart3 = (PieChart)findViewById(R.id.chartBrowserCat);

            chart3.setDrawEntryLabels(true);

            List<PieEntry> entries3 = new ArrayList<>();

            for(int i=0; i<BrowserCategories.length; i++){
                if(BrowserCategories[i]!=0){
                    entries3.add(new PieEntry(BrowserCategories[i], Categories.get(i)));
                }
            }

            Legend legend3 = chart3.getLegend();
            legend3.setFormSize(10);
            legend3.setTextSize(10);
            legend3.setEnabled(true);
            legend3.setWordWrapEnabled(true);

            PieDataSet set3 = new PieDataSet(entries3, "Category");
            set3.setColors(new int[] { R.color.sea_green, R.color.orange_custom, R.color.twitter_blue, R.color.facebook_blue,R.color.red,R.color.dark_grey,R.color.black_54,R.color.brown}, this);
            PieData data3 = new PieData(set3);
            data3.setValueFormatter(new MyValueFormatter());
            data3.setValueTextSize(17);
            chart3.setData(data3);
            chart3.setCenterText("Browser");
            chart3.setCenterTextSize(20);
            chart3.invalidate(); // refresh
        }else{
            PieChart chart3 = (PieChart)findViewById(R.id.chartBrowserCat);
            chart3.setVisibility(View.GONE);
        }

        if(NumofReports[2]>0){
            PieChart chart3 = (PieChart)findViewById(R.id.chartTwitterCat);

            chart3.setDrawEntryLabels(true);

            List<PieEntry> entries3 = new ArrayList<>();

            for(int i=0; i< TwitterCategories.length; i++){
                if(TwitterCategories[i]!=0){
                    entries3.add(new PieEntry(TwitterCategories[i], Categories.get(i)));
                }
            }

            Legend legend3 = chart3.getLegend();
            legend3.setFormSize(10);
            legend3.setTextSize(10);
            legend3.setEnabled(true);
            legend3.setWordWrapEnabled(true);

            PieDataSet set3 = new PieDataSet(entries3, "Category");
            set3.setColors(new int[] { R.color.sea_green, R.color.orange_custom, R.color.twitter_blue, R.color.facebook_blue,R.color.red,R.color.dark_grey,R.color.black_54,R.color.brown}, this);
            PieData data3 = new PieData(set3);
            data3.setValueFormatter(new MyValueFormatter());
            data3.setValueTextSize(17);
            chart3.setData(data3);
            chart3.setCenterText("Twitter");
            chart3.setCenterTextSize(20);
            chart3.invalidate(); // refresh
        }else{
            PieChart chart3 = (PieChart)findViewById(R.id.chartTwitterCat);
            chart3.setVisibility(View.GONE);
        }

        if(NumofReports[3]>0){
            PieChart chart3 = (PieChart)findViewById(R.id.chartFacebookCat);

            chart3.setDrawEntryLabels(true);

            List<PieEntry> entries3 = new ArrayList<>();

            for(int i=0; i<FacebookCategories.length; i++){
                if(FacebookCategories[i]!=0){
                    entries3.add(new PieEntry(FacebookCategories[i], Categories.get(i)));
                }
            }

            Legend legend3 = chart3.getLegend();
            legend3.setFormSize(10);
            legend3.setTextSize(10);
            legend3.setEnabled(true);
            legend3.setWordWrapEnabled(true);

            PieDataSet set3 = new PieDataSet(entries3, "Category");
            set3.setColors(new int[] { R.color.sea_green, R.color.orange_custom, R.color.twitter_blue, R.color.facebook_blue,R.color.red,R.color.dark_grey,R.color.black_54,R.color.brown}, this);
            PieData data3 = new PieData(set3);
            data3.setValueFormatter(new MyValueFormatter());
            data3.setValueTextSize(17);
            chart3.setData(data3);
            chart3.setCenterText("Facebook");
            chart3.setCenterTextSize(20);
            chart3.invalidate(); // refresh
        }else{
            PieChart chart3 = (PieChart)findViewById(R.id.chartFacebookCat);
            chart3.setVisibility(View.GONE);
        }

        tooleap.showMiniApp(appId);
        System.out.println("about to show mini app");

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
        Fragment fragment = SavedReports.PlanetFragment.newInstance(position);

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
                return;
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
        setTitle("Statistics");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
        setTitle("Statistics");
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
            Fragment fragment = new SavedReports.PlanetFragment();
            Bundle args = new Bundle();
            args.putInt(SavedReports.PlanetFragment.ARG_PLANET_NUMBER, position);
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

    private class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("#"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value); // e.g. append a dollar-sign
        }
    }

}