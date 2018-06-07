package cy.ac.ucy.cs.mandolapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.tooleap.sdk.Tooleap;
import com.tooleap.sdk.TooleapMiniApp;
import com.tooleap.sdk.TooleapPopOutMiniApp;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import cy.ac.ucy.cs.mandolapp.ReportActivities.NavigationDrawerActivity;
import cy.ac.ucy.cs.mandolapp.ReportActivities.ReportBrowserFirst;
import cy.ac.ucy.cs.mandolapp.ReportActivities.ReportFacebook;
import cy.ac.ucy.cs.mandolapp.ReportActivities.ReportFacebookFirst;
import cy.ac.ucy.cs.mandolapp.ReportActivities.ReportTwitterFirst;

public class MyService extends Service {
    //creating a mediaplayer object
    public static boolean isMiniAppLaunched;
    private Context my;

    public static TooleapMiniApp miniApp;
    private static BroadcastReceiver myBroadcastReceiver;
    public static boolean miniAppLaunched;
    public static boolean isFirst;
    public static boolean isServiceStarted;
    public static boolean isAsyncTaskCreated;
    public static String OpenApplication="";
    public static String PreviousApplication="";
    public static AsyncTask Check=null;

    public static String Picture;

    private ClipboardManager cliboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener listener;
    public static String Url;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        my = this;
        System.out.println("SERVICEEEEE");




        isServiceStarted = true;
        miniAppLaunched = true;
        isFirst = false;

        this.listener = new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = cliboardManager.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);
                try{
                    String str2 = item.getText().toString();
                    if(isValidURL(str2)) {
                        Url = str2;
                        if(Check!=null){
                            Check.cancel(true);
                            Check = null;
                        }
                        if (OpenApplication.equals("com.facebook.katana")) {
                            MyActivity.updateMiniApp(MyApplication.getAppContext(),"cy.ac.ucy.cs.mandolapp.ReportActivities.ReportFacebook",0);

                        }else if (OpenApplication.equals("com.twitter.android")) {
                            MyActivity.updateMiniApp(MyApplication.getAppContext(),"cy.ac.ucy.cs.mandolapp.ReportActivities.ReportTwitterNew",0);

                        }
                        else if (OpenApplication.equals("com.android.chrome") || OpenApplication.equals("com.android.browser") || OpenApplication.equals("org.mozilla.firefox") || OpenApplication.equals("com.opera.browser") || OpenApplication.equals("com.UCMobile.intl")) {
                            MyActivity.updateMiniApp(MyApplication.getAppContext(),"cy.ac.ucy.cs.mandolapp.ReportActivities.ReportBrowser",0);

                        }
                    }
                }
                catch(Exception e){
                    //Swallow Exception
                    e.printStackTrace();
                }




            }
        };

        this.cliboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (this.cliboardManager != null) {
            this.cliboardManager.addPrimaryClipChangedListener(this.listener);
        }


        myBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("Stopped MiniApp");
                long miniAppId = intent.getLongExtra(Tooleap.Consts.APP_ID, 0); // Get the removed mini app id
                MyActivity.miniAppLaunched = false;
                MyService.miniAppLaunched = false;
//                if (NavigationDrawerActivity.CheckAppTask != null) {
//                    NavigationDrawerActivity.CheckAppTask.cancel(true);
//                }
//                if (ReportTwitterFirst.CheckAppTask != null) {
//                    ReportTwitterFirst.CheckAppTask.cancel(true);
//                }
//                if (ReportFacebookFirst.CheckAppTask != null) {
//                    ReportFacebookFirst.CheckAppTask.cancel(true);
//                }
//                if (ReportBrowserFirst.CheckAppTask != null) {
//                    ReportBrowserFirst.CheckAppTask.cancel(true);
//                }
                DestroyAsyncTask();
                System.gc();

            }


        };


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Tooleap.Consts.USER_REMOVED_MINI_APP);
        registerReceiver(myBroadcastReceiver, intentFilter);

        if (!isAsyncTaskCreated && Check==null) {
            Check = new CheckAppListener().execute("");
            isAsyncTaskCreated = true;
        }

        //we have some options for service
        //start sticky means service will be explicity started and stopped
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isServiceStarted = false;
        miniAppLaunched = false;
        unregisterReceiver(myBroadcastReceiver);
        if(MyService.Check!=null){
            Check.cancel(true);
            Check=null;
        }
        System.out.println("Stopping service");
        this.cliboardManager.removePrimaryClipChangedListener(this.listener);
        super.onDestroy();

    }

    public static void DestroyAsyncTask(){
        if(Check!=null){
            Check.cancel(true);
            Check=null;
        }
    }

    public static void CreateAsyncTask(){
        try {


            if (Check == null) {
                Check = new CheckAppListener().execute();
                isAsyncTaskCreated = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // Uses an AsyncTask to download a Twitter user's timeline
    private static class CheckAppListener extends AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... screenNames) {
            String result = null;
            while (true) {
                if (isCancelled()) {
                    return "Exit";
                }
                String currentApp = "NULL";
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        UsageStatsManager usm = (UsageStatsManager) MyApplication.getAppContext().getSystemService(Context.USAGE_STATS_SERVICE);
                        long time = System.currentTimeMillis();
                        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
                        if (appList != null && appList.size() > 0) {
                            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                            for (UsageStats usageStats : appList) {
                                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                            }
                            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                                currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                            }
                            mySortedMap.clear();
                            mySortedMap=null;
                        }
                        usm = null;
                        appList.clear();
                        appList=null;
                    } else {
                        ActivityManager am = (ActivityManager) MyApplication.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
                        List<ActivityManager.RunningAppProcessInfo> tasks = null;
                        if (am != null) {
                            tasks = am.getRunningAppProcesses();
                            currentApp = tasks.get(0).processName;
                            am=null;
                            tasks.clear();
                            tasks=null;
                        }

                    }
                    if (currentApp != null && currentApp != "") {
                        if (currentApp.equals("com.facebook.katana")) {
                            if (miniAppLaunched || MyActivity.miniAppLaunched) {
                                if (!currentApp.equals(OpenApplication)) {
                                    MyActivity.updateMiniApp(MyApplication.getAppContext(), "cy.ac.ucy.cs.mandolapp.ReportActivities.ReportFacebookFirst", 1);
                                }
                            }
                            OpenApplication = currentApp;
                            PreviousApplication = currentApp;
                        } else if (currentApp.equals("com.twitter.android")) {
                            if (miniAppLaunched || MyActivity.miniAppLaunched) {
                                if (!currentApp.equals(OpenApplication)) {
                                    MyActivity.updateMiniApp(MyApplication.getAppContext(), "cy.ac.ucy.cs.mandolapp.ReportActivities.ReportTwitterFirst", 1);
                                }
                            }
                            OpenApplication = currentApp;
                            PreviousApplication = currentApp;
                        } else if (currentApp.equals("com.android.chrome") || currentApp.equals("com.android.browser") || currentApp.equals("org.mozilla.firefox") || currentApp.equals("com.opera.browser") || currentApp.equals("com.UCMobile.intl")) {
                            if (miniAppLaunched || MyActivity.miniAppLaunched) {
                                if (!currentApp.equals(OpenApplication)) {
                                    MyActivity.updateMiniApp(MyApplication.getAppContext(), "cy.ac.ucy.cs.mandolapp.ReportActivities.ReportBrowserFirst", 1);
                                }
                            }
                            OpenApplication = currentApp;
                            PreviousApplication = currentApp;
                        } else if (currentApp.equals("com.example.myapp")) {
                            if (PreviousApplication.equals("com.twitter.android")) {
                                OpenApplication = "com.twitter.android";
                            } else if (PreviousApplication.equals("com.facebook.katana")) {
                                OpenApplication = "com.facebook.katana";
                            } else if (PreviousApplication.equals("com.android.chrome") || PreviousApplication.equals("com.android.browser") || PreviousApplication.equals("org.mozilla.firefox") || PreviousApplication.equals("com.opera.browser") || PreviousApplication.equals("com.UCMobile.intl")) {
                                OpenApplication = PreviousApplication;
                            } else {
                                OpenApplication = "";
                                PreviousApplication = "";
                            }
                        } else {
                            OpenApplication = "";
                            if(!OpenApplication.equals(PreviousApplication)){
                                MyActivity.updateMiniApp(MyApplication.getAppContext(), "cy.ac.ucy.cs.mandolapp.ReportActivities.NavigationDrawerActivity", 2);

                            }
                            PreviousApplication = "";
                        }


                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    //Swallow exception
                }

            }
        }


        // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Exit")) {
                return;
            }
            if (result.equals("twitter")) {
                System.out.println("Twitter");
            } else if (result.equals("facebook")) {
                System.out.println("Facebook");

            } else if (result.equals("browser")) {
                System.out.println("browser");
            }

        }

        @Override
        protected void onCancelled(String result){
            return;

        }


    }

    // Uses an AsyncTask to download a Twitter user's timeline

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

}
