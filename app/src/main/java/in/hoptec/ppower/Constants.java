package in.hoptec.ppower;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by shivesh on 28/6/17.
 */

public class Constants {



    public static String HOST="https://bigpitch.000webhostapp.com/api";

    public static String API_USER_REG_GET="/createuser.php";
    public static String API_USER_LOGIN_GET="/login.php";

    //GET query
    public static String API_GET_VIDEOS="/latest_videos.php";


    public static boolean IS_ANIMATED_BG_SPLASH=false;
    public static boolean isPdCancelable=true;

    public static String folder;
    public static String datafile;

    public static Context ctx;

    public static void init(Context context)
    {
        utl.init(ctx);
        ctx=context;
    }

    private static String FIRE_BASE="https://test-a0930.firebaseio.com/";

    public static String fireURL()
    {
        return Constants.FIRE_BASE+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");
    }


    public static String getFolder()
    {
        folder = Environment.getExternalStorageDirectory().getPath().toString()+"/."+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");
        return folder;
    }


    public static String userDataFile()
    {
        folder = Environment.getExternalStorageDirectory().getPath().toString()+"/."+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");

        File file=new File(folder);
        if(!file.exists())
        {
            file.mkdir();
        }
        datafile=folder+"/firebaseUser.json";
        return datafile;
    }



    public static String localDataFile()
    {
        folder = Environment.getExternalStorageDirectory().getPath().toString()+"/."+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");

        File file=new File(folder);
        if(!file.exists())
        {
            file.mkdir();
        }
        datafile=folder+"/data.json";
        return datafile;
    }



    public static String getApp()
    {
        return utl.refineString(ctx.getResources().getString(R.string.app_name),"");
    }











}
