package in.hoptec.ppower;

import android.app.Activity;
import android.app.Application;
import android.location.Location;

import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by shivesh on 28/6/17.
 */

public class BaseApp extends Application {


    public static  FusedLocationProviderClient mFusedLocationClient;

    @Override
    public void onCreate()
    {
        Constants.init(this);
        AndroidNetworking.initialize(this);


    }


}
