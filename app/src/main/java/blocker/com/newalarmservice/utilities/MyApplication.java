package blocker.com.newalarmservice.utilities;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class MyApplication extends Application {
    private static MyApplication myApplicationInstance;

    private Fragment fragment;
    private AppCompatActivity mCurrentActivity = null;


    @Override
    public void onCreate() {
        super.onCreate();
        myApplicationInstance = this;
    }

    public AppCompatActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(AppCompatActivity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public static MyApplication getApplicationInstance() {
        return myApplicationInstance;
    }

    public Context getMyApplicationContext() {
        return myApplicationInstance.getApplicationContext();
    }

    public void setCurrentFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getCurrentFragmet() {
        return fragment;
    }
}
