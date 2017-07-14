package in.hoptec.ppower;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by shivesh on 28/6/17.
 */

public class Constants {



    public static String HOST="https://bigpitch.000webhostapp.com";

    public static String API_USER_REG_GET="/api/createuser.php";
    public static String API_USER_LOGIN_GET="/api/login.php";

    //GET query city
    public static String API_GET_VIDEOS="/api/latest_videos.php";

    //GET query city user_id
    public static String API_GET_USER_VIDEOS="/api/latest_videos.php";

    //GET vid
    public static String API_GET_COMMENTS="/api/comments.php";

    //GET user user_image user_id comment vid
    public static String API_ADD_COMMENT="/api/comment.php";

    //GET user_id vid
    public static String API_LIKE="/api/like.php";

    //GET user_id vid
    public static String API_REMOVE_VIDEO="/api/remove_video.php";

    //GET user user_image user_id comment extra0 extra1 title
    //extra0 -> attachment_link extra1 -> fcm token extra2 -> fir_no  extra3 -> ph_no
    public static String API_ADD_REPORT="/api/report.php";


    //GET vid
    public static String API_SHARE="/api/share.php";

    //GET title description user user_image user_id artwork_url stream_url
    public static String API_UPLOAD="/api/upload_video.php";


    //POST file
    public static String API_FILE_UPLOAD="/api/file_upload.php";


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

    private static String FIRE_BASE="https://cmapp1919.firebaseio.com/";
    public static String FIRE_BASE_STORAGE="gs://cmapp1919.appspot.com";

    public static String fireURL()
    {
        return Constants.FIRE_BASE+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");
    }



    public static String getFolder()
    {
        folder = Environment.getExternalStorageDirectory().getPath().toString()+"/."+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");
        return folder;
    }


    public static String getDwdFolder()
    {
        folder = Environment.getExternalStorageDirectory().getPath().toString()+"/."+ utl.refineString(ctx.getResources().getString(R.string.app_name),"")
        +"/cache";
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
