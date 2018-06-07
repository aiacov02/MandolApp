package cy.ac.ucy.cs.mandolapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tooleap.sdk.Tooleap;
import com.tooleap.sdk.TooleapMiniApp;
import com.tooleap.sdk.TooleapPopOutMiniApp;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import cy.ac.ucy.cs.mandolapp.ReportActivities.NavigationDrawerActivity;

/**
 * Created by andreas on 13/04/2018.
 */

public class Receiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        if(!MyService.miniAppLaunched) {

            MyActivity.removeAllMiniApps();
            try {
                System.out.println("notification pressed");

                String currentApp = getRunningAppOnce();
                if (MyService.Check == null) {
                    MyService.CreateAsyncTask();
                }
                MyService.miniAppLaunched = true;
                MyActivity.miniAppLaunched = true;
                System.out.println(currentApp);
                String activity = null;
                if (currentApp.equals("com.twitter.android")) {
                    activity = "cy.ac.ucy.cs.mandolapp.ReportActivities.ReportTwitterFirst";
                } else if (currentApp.equals("com.facebook.katana")) {
                    activity = "cy.ac.ucy.cs.mandolapp.ReportActivities.ReportFacebookFirst";
                } else if (currentApp.equals("com.android.chrome") || currentApp.equals("com.android.browser") || currentApp.equals("org.mozilla.firefox") || currentApp.equals("com.opera.browser") || currentApp.equals("com.UCMobile.intl")) {
                    activity = "cy.ac.ucy.cs.mandolapp.ReportActivities.ReportBrowserFirst";
                } else {
                    activity = "cy.ac.ucy.cs.mandolapp.ReportActivities.NavigationDrawerActivity";
                }

                popoutMiniApp1(context,activity);
                MyActivity.updateMiniApp(MyApplication.getAppContext(),activity,3);
            } catch (Exception e) {
                e.printStackTrace();
            }

            MyService.isFirst = false;
            MyActivity.isFirst = false;

        }

    }

    public String getRunningAppOnce(){
        String currentApp=null;
        try {


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager usm = (UsageStatsManager) MyApplication.getAppContext().getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                List<UsageStats> appList = null;
                if (usm != null) {
                    appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
                }
                if (appList != null && appList.size() > 0) {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                    for (UsageStats usageStats : appList) {
                        mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    if (mySortedMap != null && !mySortedMap.isEmpty()) {
                        currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    }
                    mySortedMap=null;
                }
                usm=null;
                appList=null;

            } else {
                ActivityManager am = (ActivityManager) MyApplication.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
                currentApp = tasks.get(0).processName;
                am=null;
                tasks=null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return currentApp;
    }

    private void popoutMiniApp1(Context context, String activity) {
        Tooleap.getInstance(MyApplication.getAppContext()).getAllMiniApps();
        TooleapMiniApp miniApp = new TooleapPopOutMiniApp(MyApplication.getAppContext(), NavigationDrawerActivity.class);
        miniApp.contentTitle = "Mandolapp";
        miniApp.contentText = "Mandolapp";
        miniApp.bubbleBackgroundColor =  0xFF3498DB;
        miniApp.notificationText = "Hello World";
        miniApp.notificationBadgeNumber = 1;
        miniApp.when = new Date();
        miniApp.setBubbleSize(30);
        MyService.miniApp = miniApp;
        Tooleap.getInstance(MyApplication.getAppContext()).addMiniApp(miniApp);

    }


}

