package cy.ac.ucy.cs.mandolapp.SettingsActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import cy.ac.ucy.cs.mandolapp.MyApplication;
import cy.ac.ucy.cs.mandolapp.PlanetAdapter;
import cy.ac.ucy.cs.mandolapp.R;
import cy.ac.ucy.cs.mandolapp.ReportActivities.Report;
import cy.ac.ucy.cs.mandolapp.SavedReportsActivities.SavedReports;
import cy.ac.ucy.cs.mandolapp.adapters.CustomAdapter;
import cy.ac.ucy.cs.mandolapp.adapters.DropDownListAdapterAuthSettings;
import cy.ac.ucy.cs.mandolapp.adapters.DropDownListAdapterSRFacebook;
import cy.ac.ucy.cs.mandolapp.adapters.DropDownListAdapterSRFacebookAuth;

public class Settings extends TooleapActivities.Activity implements PlanetAdapter.OnItemClickListener {
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

    public static boolean[] checkSelectedAuth;

    private PopupWindow pw;

    ListView listView;
    private static CustomAdapter adapter;
    private ArrayList<String> Authorities;
    public static int selectedAuthoritiesNumber=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
                getActionBar().setTitle("Settings");
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

        Switch toggle = (Switch) findViewById(R.id.switchGuidelines);

        SharedPreferences sharedPref = MyApplication.getAppContext().getSharedPreferences("MandolappSettings",Context.MODE_PRIVATE);

        boolean isToggleChecked = sharedPref.getBoolean("ReportGuidelines", true);

        toggle.setChecked(isToggleChecked);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences sharedPref = MyApplication.getAppContext().getSharedPreferences("MandolappSettings",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("ReportGuidelines", true);
                    editor.apply();
                } else {
                    SharedPreferences sharedPref = MyApplication.getAppContext().getSharedPreferences("MandolappSettings",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("ReportGuidelines", false);
                    editor.apply();
                }
            }
        });

        Button btnClearSavedReports = (Button)findViewById(R.id.btnClearSavedReports);
        btnClearSavedReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(my);
                builder.setMessage("Are you sure you want to delete all Saved Reports?")
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean res = DeleteSavedReports();
                                if(res){
                                    Toast.makeText(my,"Saved Reports Cleared",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(my,"Error Clearing Saved Reports",Toast.LENGTH_LONG).show();

                                }
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                makeDialogTooleapCompatible(alert);
            }
        });

        Button btnClearReportHistory = (Button)findViewById(R.id.btnClearReportHistory);
        btnClearReportHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(my);
                builder.setMessage("Are you sure you want to delete Report History? All Statistics will be deleted as well!")
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean res = DeleteReportHistory();
                                if(res){
                                    Toast.makeText(my,"Report History Cleared",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(my,"Error Clearing Report History",Toast.LENGTH_LONG).show();

                                }
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                makeDialogTooleapCompatible(alert);
            }
        });

        final List<String> items_auth = Arrays.asList(getResources().getStringArray(R.array.authority_array));
        this.Authorities = new ArrayList<String>(items_auth);
        final ArrayList<String> Authorities1 = Authorities;



        String SelectedAuths = sharedPref.getString("DefaultAuthorities", null);
        Button btnChooseAuthority = (Button)findViewById(R.id.sprAuthoritySettings);

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
                btnChooseAuthority.setText(authList.get(0));
            }else if(authList.size()>1){
                btnChooseAuthority.setText(authList.get(0) + " & " + (selectedItems-1) + " more");
            }

        }else{
            checkSelectedAuth = new boolean[Authorities.size()];
            //initialize all values of list to 'unselected' initially
            for (int i = 0; i < checkSelectedAuth.length; i++) {
                checkSelectedAuth[i] = false;
            }
        }


        btnChooseAuthority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatePopUp(Authorities1);
            }
        });

    }

    private boolean DeleteSavedReports(){
        DatabaseHelper DB=null;
        try{
            DB = new DatabaseHelper(my);
            DB.deleteAllData();

        }catch (Exception e){
            e.printStackTrace();
            if(DB!=null){
                DB.close();
                DB=null;
            }
            return false;
        }

        return true;
    }

    private boolean DeleteReportHistory(){
        DatabaseHelper DB=null;
        try{
            DB = new DatabaseHelper(my);
            DB.deleteAllDataSent();

        }catch (Exception e){
            e.printStackTrace();
            if(DB!=null){
                DB.close();
                DB=null;
            }
            return false;
        }

        return true;
    }

    /*
    * Function to set up the pop-up window which acts as drop-down list
    * */
    private void initiatePopUp(ArrayList<String> items){

        LayoutInflater inflater = (LayoutInflater)my.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //get the pop-up window i.e.  drop-down layout
        View layout = inflater.inflate(R.layout.pop_up_window, (ViewGroup)findViewById(R.id.PopUpView));


        LinearLayout layout1;

        //get the view to which drop-down layout is to be anchored
        layout1 = (LinearLayout)findViewById(R.id.linearLayoutAuthoritySettings);

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
        btn = (Button)findViewById(R.id.sprAuthoritySettings);

        //populate the drop-down list
        final ListView list = (ListView) layout.findViewById(R.id.dropDownList);


        DropDownListAdapterAuthSettings adapter = new DropDownListAdapterAuthSettings(my, items, btn);
        list.setAdapter(adapter);


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
                activityToStart = "cy.ac.ucy.cs.mandolapp.StatisticsActivities.Statistics";
            }
            else if(mPlanetTitles[position].equals("Settings")){
                return;
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
        setTitle("Settings");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
        setTitle("Settings");
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