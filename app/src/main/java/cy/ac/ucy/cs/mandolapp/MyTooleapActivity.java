package cy.ac.ucy.cs.mandolapp;//package com.example.myapp;
//
//import android.app.ActivityManager;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.app.usage.UsageStats;
//import android.app.usage.UsageStatsManager;
//import android.content.ClipData;
//import android.content.ClipDescription;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.webkit.URLUtil;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.tooleap.sdk.Tooleap;
//import com.tooleap.sdk.TooleapActivities;
//import com.tooleap.sdk.TooleapMiniApp;
//import com.tooleap.sdk.TooleapPopOutMiniApp;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.List;
//import java.util.SortedMap;
//import java.util.TreeMap;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import static java.lang.Thread.sleep;
//
//public class MyTooleapActivity extends TooleapActivities.Activity {
//
//
//    private long appId;
//    private Tooleap tooleap;
//    private TooleapMiniApp miniApp;
//    private String AppCopied[] = new String[2];
//    private Report myReport;
//    private  DatabaseHelper myDb;
//    private Context my;
//
//
//    public void onCreate(Bundle savedInstanceState) {
//        my = this;
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.miniapp);
//        myReport = new Report();
//        myDb = new DatabaseHelper(this);
//        appId = this.getTooleapAppId();
//        tooleap = Tooleap.getInstance(this);
//        miniApp = tooleap.getMiniApp(appId);
//        //setLaunchListener();
//        final ClipboardManager cliboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                cliboardManager
//                .addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
//
//                    @Override
//                    public void onPrimaryClipChanged() {
//                        ClipData clipData = cliboardManager.getPrimaryClip();
//                        ClipData.Item item = clipData.getItemAt(0);
//                        try{
//                            String item2 = clipData.getDescription().getLabel().toString();
//                            TextView tv;
//                            String str2 = item.getText().toString();
//                            if(isValidURL(str2)){
//                                tv = (TextView)findViewById(R.id.textViewUrl);
//                                tv.setText(str2);
//                                miniApp.notificationText = "URL copied!";
//                                miniApp.notificationBadgeNumber = 1;
//                                AppCopied[0] = item2;
//                            }else{
//                                tv = (TextView)findViewById(R.id.textViewText);
//                                tv.setText(str2);
//                                miniApp.notificationText = "Text copied!";
//                                miniApp.notificationBadgeNumber = 2;
//                                AppCopied[1] = item2;
//                            }
//                            tooleap.updateMiniAppAndNotify(appId, miniApp);
//
//
//
//
//
//
//
//                        }
//                        catch(Exception e){
//
//                        }
//
//
//
//
//
//                    }
//        });
//
//        ((ImageButton)findViewById(R.id.btnSave)).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(my,NavMain.class);
////                startActivity(intent);
//
//                miniApp.setContentIntent(intent);
//
//                tooleap.updateMiniAppAndNotify(appId,miniApp);
//                tooleap.showMiniApp(appId);
//                finish();
//
//                /**Desirable code here*/
//
////                //saveReport();
//            }
//        });
//
//    }
//
//    private boolean saveReport(){
//        TextView tvUrl = (TextView)findViewById(R.id.textViewUrl);
//        if(isValidURL(tvUrl.getText().toString())){
//            TextView tvText = (TextView)findViewById(R.id.textViewText);
//            myReport.setApp(AppCopied[0]);
//            myReport.setUrl(tvUrl.getText().toString());
//            myReport.setReporttext(tvText.getText().toString());
//            boolean result = myDb.insertData(myReport.getApp(),myReport.getUrl(),myReport.getReporttext(),myReport.getCategories());
//            if(!result){
//                Toast.makeText(this,"Error. Couldn't save report",Toast.LENGTH_LONG).show();
//            }else{
//                Toast.makeText(this,"Report saved",Toast.LENGTH_LONG).show();
//                Cursor res = myDb.getAllData();
//                if(res.getCount()!=0){
//                    StringBuffer buffer = new StringBuffer();
//                    while(res.moveToNext()){
//                        buffer.append("Id :"+ res.getString(0)+"\n");
//                        buffer.append("App :"+ res.getString(1)+"\n");
//                        buffer.append("Url :"+ res.getString(2)+"\n");
//                        buffer.append("Text :"+ res.getString(3)+"\n");
//                        buffer.append("Category :"+ res.getString(4)+"\n\n");
//                    }
//                    showMessage("Data",buffer.toString());
//                }else{
//                    Toast.makeText(this,"000",Toast.LENGTH_LONG).show();
//                }
//            }
//        }else{
//            Toast.makeText(this,"Invalid Url",Toast.LENGTH_LONG).show();
//            return false;
//        }
//        return true;
//    }
//
//    private void setLaunchListener(){
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    String currentApp = "NULL";
//                    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                        UsageStatsManager usm = (UsageStatsManager) my.getSystemService(Context.USAGE_STATS_SERVICE);
//                        long time = System.currentTimeMillis();
//                        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
//                        if (appList != null && appList.size() > 0) {
//                            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
//                            for (UsageStats usageStats : appList) {
//                                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
//                            }
//                            if (mySortedMap != null && !mySortedMap.isEmpty()) {
//                                currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
//                            }
//                        }
//                    } else {
//                        ActivityManager am = (ActivityManager)my.getSystemService(Context.ACTIVITY_SERVICE);
//                        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
//                        currentApp = tasks.get(0).processName;
//                    }
//                    if(currentApp.equals("com.facebook.katana")){
//                        miniApp.notificationBadgeNumber = 5;
//                        tooleap.updateMiniAppAndNotify(appId, miniApp);
//                        return;
//                    }
//                    try{
//                        Thread.sleep(100);
//
//                    }
//                    catch (InterruptedException e){
//
//                    }
//                }
//            }
//        });
//        t.run();
//    }
//
//    private void showMessage(String title,String Message){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(Message);
//        builder.create().show();
//
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
//    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
//
//
//        HttpURLConnection urlConnection = null;
//        URL url = new URL(urlString);
//        urlConnection = (HttpURLConnection) url.openConnection();
//        urlConnection.setRequestMethod("GET");
//        urlConnection.setReadTimeout(10000 /* milliseconds */ );
//        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
//        urlConnection.setDoOutput(true);
//        urlConnection.connect();
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
//        StringBuilder sb = new StringBuilder();
//
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line + "\n");
//        }
//        br.close();
//
//        String jsonString = sb.toString();
//        System.out.println("JSON: " + jsonString);
//
//        return new JSONObject(jsonString);
//    }
//
//
//
//}
//
