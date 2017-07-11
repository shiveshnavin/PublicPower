package in.hoptec.ppower;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

/**
 * Created by shivesh on 28/6/17.
 */

public class BaseApp extends Application {



    @Override
    public void onCreate()
    {
        Constants.init(this);
        AndroidNetworking.initialize(this);
    }


}
