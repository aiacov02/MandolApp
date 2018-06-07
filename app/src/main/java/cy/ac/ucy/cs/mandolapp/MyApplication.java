package cy.ac.ucy.cs.mandolapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by andreas on 15/04/2018.
 */

public class MyApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}