package oditek.com.tuurbus.webservices;

import android.app.Application;
import android.content.Context;

/*
 * Created by biswajit on 08-08-15.
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static MyApplication getmInstance(){
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }
}
