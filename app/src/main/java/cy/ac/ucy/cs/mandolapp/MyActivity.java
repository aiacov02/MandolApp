package cy.ac.ucy.cs.mandolapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tooleap.sdk.Tooleap;
import com.tooleap.sdk.TooleapMiniApp;
import com.tooleap.sdk.TooleapPopOutMiniApp;

import cy.ac.ucy.cs.mandolapp.Intro.IntroActivity;
import cy.ac.ucy.cs.mandolapp.ReportActivities.NavigationDrawerActivity;
import cy.ac.ucy.cs.mandolapp.ReportActivities.ReportBrowser;
import cy.ac.ucy.cs.mandolapp.ReportActivities.ReportBrowserFirst;
import cy.ac.ucy.cs.mandolapp.ReportActivities.ReportFacebook;
import cy.ac.ucy.cs.mandolapp.ReportActivities.ReportFacebookFirst;
import cy.ac.ucy.cs.mandolapp.ReportActivities.ReportTwitterFirst;


import java.util.Date;

public class MyActivity extends Activity {

    public static TooleapMiniApp miniApp;

    private BroadcastReceiver myBroadcastReceiver;

    private long appId;
    private Tooleap tooleap;
    public static boolean miniAppLaunched;
    public static boolean isFirst=true;
    private boolean isServiceStarted;

    private static boolean isNotificationOpen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        final Context context = this;

        SharedPreferences sharedPref = MyApplication.getAppContext().getSharedPreferences("MandolappSettings",Context.MODE_PRIVATE);

        boolean isFirstTime = sharedPref.getBoolean("FirstTime", true);

        if(isFirstTime){
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        }

        boolean granted = false;
        final Context my = this;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), context.getPackageName());

            if (mode == AppOpsManager.MODE_DEFAULT) {
                granted = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
            } else {
                granted = (mode == AppOpsManager.MODE_ALLOWED);
            }

            if(!granted){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("PLEASE READ!!!! \nUsage Access Permission is required. By pressing the OK button below you will be redirected to the Settings app to give the permission. The permission is required so that we can provide to you the best possible user experience!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        }


        ((Button)findViewById(R.id.popout)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			    if(!MyService.miniAppLaunched){
                    //startService(new Intent(my, MyService.class));
                    popoutMiniApp(my);
                    isFirst=false;
                    miniAppLaunched=true;
                    MyService.miniAppLaunched=true;
                    MyService.isFirst=false;
			    }
			    if(!MyService.isServiceStarted){
                    startService(new Intent(my, MyService.class));
                    isServiceStarted=true;
                }else{
			        MyService.CreateAsyncTask();
                }

                if(!isNotificationOpen){
                    Intent intent = new Intent(getApplicationContext() , Receiver.class);
                    PendingIntent pendingIntent  = PendingIntent.getBroadcast (my, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                    Notification.Builder builder = new Notification.Builder(getApplicationContext());
                    builder.setContentTitle("Mandolapp");
                    builder.setContentText("Mandolapp running");
                    builder.setSubText("Click to launch app bubble");
                    builder.setNumber(101);
                    builder.setContentIntent(pendingIntent);
                    builder.setTicker("Mandolapp Notification");
                    builder.setSmallIcon(R.drawable.ic_launcher);
                    builder.setAutoCancel(false);
                    builder.setOngoing(true);
                    builder.setPriority(Notification.PRIORITY_HIGH);
                    Notification notification = builder.build();
                    NotificationManager notificationManger =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManger.notify(01, notification);
                    isNotificationOpen=true;
                }


			}
		});


        ((Button)findViewById(R.id.remove)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println();
                if(isServiceStarted){
                    stopService(new Intent(my, MyService.class));
                    isServiceStarted=false;
                }
                removeAllMiniApps();
                if(isNotificationOpen || true){
                    NotificationManager notificationManager = (NotificationManager) my.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(01);
                    isNotificationOpen = false;
                }

            }
        });







    }


//
//    private void popoutMiniApp1() {
//        TooleapMiniApp miniApp = new TooleapPopOutMiniApp(this, NavigationDrawerActivity.class);
//        miniApp.contentTitle = "Mandolapp";
//        miniApp.contentText = "Mandolapp";
//        miniApp.bubbleBackgroundColor =  0xFF3498DB;
//        miniApp.notificationText = "Hello World";
//        miniApp.notificationBadgeNumber = 1;
//        miniApp.when = new Date();
//        miniApp.setBubbleSize(30);
//        miniApp = this.miniApp;
//        Tooleap.getInstance(this).addMiniApp(miniApp);
//
//
//
//    }

    public static void popoutMiniApp(Context context) {
    	TooleapMiniApp miniApp = new TooleapPopOutMiniApp(MyApplication.getAppContext(), NavigationDrawerActivity.class);
        miniApp.contentTitle = "Mandolapp";
        miniApp.contentText = "Mandolapp";
        miniApp.bubbleBackgroundColor =  0xFF3498DB;
        miniApp.when = new Date();
        miniApp.setBubbleSize(30);
        Tooleap.getInstance(MyApplication.getAppContext()).addMiniApp(miniApp);
        MyService.miniApp = miniApp;

        Tooleap.getInstance(MyApplication.getAppContext()).showMiniApp(miniApp.getAppId());
    }

//    public static void popoutMiniApp(Context context,String activity) {
//        Class<?> c;
//        try{
//            c = Class.forName(activity);
//            TooleapMiniApp miniApp = new TooleapPopOutMiniApp(MyApplication.getAppContext(), c);
//            miniApp.contentTitle = "Mandolapp";
//            miniApp.contentText = "Mandolapp";
//            miniApp.bubbleBackgroundColor =  0xFF3498DB;
//            miniApp.notificationText = "Hello World";
//            miniApp.notificationBadgeNumber = 1;
//            miniApp.when = new Date();
//            miniApp.setBubbleSize(30);
//            miniApp = miniApp;
//            Tooleap.getInstance(MyApplication.getAppContext()).addMiniApp(miniApp);
//            MyService.miniApp = miniApp;
//
//            Tooleap.getInstance(MyApplication.getAppContext()).showMiniApp(miniApp.getAppId());
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//    }

    public static void updateMiniApp(Context context, String activity,int Screen){
        Class<?> c;
        try{

            miniApp = MyService.miniApp;
            c = Class.forName(activity);

            miniApp.setContentIntent(new Intent(MyApplication.getAppContext(),c));
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_launcher);
            miniApp.setIcon(icon);

            if(Screen==0){
                miniApp.notificationText = "Link Copied";
                miniApp.notificationBadgeNumber = 1;
                Tooleap.getInstance(context).updateMiniAppAndNotify(miniApp.getAppId(),miniApp);
            }
            else if(Screen==1){
                miniApp.notificationText = "Social Media App Opened";
                miniApp.notificationBadgeNumber = 1;
                Tooleap.getInstance(context).updateMiniAppAndNotify(miniApp.getAppId(),miniApp);
            }
            else{
                miniApp.notificationText = "Social Media App Closed";
                miniApp.notificationBadgeNumber = 1;
                Tooleap.getInstance(context).updateMiniAppAndNotify(miniApp.getAppId(),miniApp);
            }

            MyService.miniApp = miniApp;

            if(Screen==3){
                Tooleap.getInstance(context).showMiniApp(miniApp.getAppId());
            }

        }catch (Exception e){
            e.printStackTrace();
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void removeAllMiniApps() {
        Tooleap.getInstance(MyApplication.getAppContext()).removeAllMiniApps();
        MyActivity.miniAppLaunched = false;
        MyService.miniAppLaunched=false;

    }


}
