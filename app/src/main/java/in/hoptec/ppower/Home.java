package in.hoptec.ppower;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.iceteck.silicompressorr.SiliCompressor;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.hoptec.ppower.database.Feed;
import in.hoptec.ppower.utils.Bouncer;
import in.hoptec.ppower.utils.GenricCallback;
import in.hoptec.ppower.views.GoalProgressBar;

import static in.hoptec.ppower.BaseApp.mFusedLocationClient;

public class Home extends AppCompatActivity {


    /*
    *    <com.afollestad.easyvideoplayer.EasyVideoPlayer
            android:transitionName="@string/activity_image_trans"
            xmlns:android="http://schemas.android.com/apk/res/android"
            android:id="@+id/player"
            android:layout_width="match_parent"
            android:layout_height="250dp" />


            */

    @BindView(R.id.prog)
    GoalProgressBar prog;

    @BindView(R.id.load)
    TextView load;

    @BindView(R.id.city)
    TextView city;


    @BindView(R.id.trend)
    TextView trend;

    @BindView(R.id.upload)
    View upload;

    @BindView(R.id.uploads)
    View uploads;

    @BindView(R.id.report)
    View report;

    @BindView(R.id.latest)
    View latest;

    private static final String TEST_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    EasyVideoPlayer player;
    public static Context ctx;
    public static Activity act;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        act = this;
        utl.init(ctx);
        utl.fullScreen(this);
        setContentView(R.layout.activity_home);
        player = ((EasyVideoPlayer) findViewById(R.id.player));

        utl.l("Permit : "+checkLocationPermission());

        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");
        FirebaseMessaging.getInstance().subscribeToTopic("content");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        try {
            //noinspection MissingPermission
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {

                                utl.l("Location : "+location);
                                utl.writeFile("loc",utl.js.toJson(location));


                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


        player.setBottomLabelText("Trending");
        getVideos("");

        ButterKnife.bind(this);
        AndroidNetworking.initialize(ctx);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_VIDEO);

                /*
                Intent intent=new Intent(ctx,Upload.class);
                intent.putExtra("json",json);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(act,view, getString(R.string.activity_image_trans));
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }
*/


            }
        });


        uploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(ctx,MyVideos.class);
                intent.putExtra("json",json);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(act,view, getString(R.string.activity_image_trans));
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }



                //   utl.logout();
                // finish();

            }
        });


        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(ctx,Report.class);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(act,view, getString(R.string.activity_image_trans));
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }


            }
        });

        latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(ctx,LatestVideos.class);
                intent.putExtra("json",json);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(act,view, getString(R.string.activity_image_trans));
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }




            }
        });

        populate();

    }

    boolean isFirstTime=true;

    public void play1(String url)
    {

        player.setLoop(true);
        player.setCallback(new EasyVideoCallback() {
            @Override
            public void onStarted(EasyVideoPlayer player) {

            }

            @Override
            public void onPaused(EasyVideoPlayer player) {

            }

            @Override
            public void onPreparing(EasyVideoPlayer player) {

            }

            @Override
            public void onPrepared(EasyVideoPlayer player) {





            }

            @Override
            public void onBuffering(int percent) {

                if(!isVisible)

                {   player.pause();
                    return;
                }

                if(percent>10&&isFirstTime) {
                    player.start();
                    player.hideControls();
                    isFirstTime=false;

                }


            }

            @Override
            public void onError(EasyVideoPlayer player, Exception e) {

            }

            @Override
            public void onCompletion(EasyVideoPlayer player) {

            }

            @Override
            public void onRetry(EasyVideoPlayer player, Uri source) {

            }

            @Override
            public void onSubmit(EasyVideoPlayer player, Uri source) {

            }
        });
        player.setBottomLabelText("Trending");

        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        player.setSource(Uri.parse(url));


    }


    void populate()
    {
        getCity();
        View[] views = new View[] {latest,report, upload,uploads };

// 100ms delay between Animations
        long delayBetweenAnimations = 100l;

        for(int i = 0; i < views.length; i++) {
            final View view = views[i];

            // We calculate the delay for this Animation, each animation starts 100ms
            // after the previous one
            Long delay =i * delayBetweenAnimations;

            view.postDelayed(new Runnable() {
                @Override
                public void run() {


                    final Animation myAnim = AnimationUtils.loadAnimation(ctx, R.anim.bounce);

                    // Use bounce interpolator with amplitude 0.2 and frequency 20
                    Bouncer interpolator = new Bouncer(0.15, 7);
                    myAnim.setInterpolator(interpolator);

                    view.startAnimation(myAnim);


                }
            }, delay);
        }


    }



    void parser(String response)
    {

        utl.l(response);
        ArrayList<Feed> videos=new ArrayList<Feed>();

        try {
            JSONArray jar=new JSONArray(response);

            for(int i=0;i<jar.length();i++)
            {
                Feed fe=utl.js.fromJson(jar.getString(i),Feed.class);
                videos.add(fe);
            }

            if(videos.size()>0) {
               Feed fed = videos.get(0);
                for(int i=0;i<videos.size();i++)
                {
                    if(videos.get(i).getLikes()>fed.getLikes())
                    {
                        fed=videos.get(i);
                    }
                }
                final Feed feed=fed;
                play(feed );


                trend.setText(fed.title );







                trend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent=new Intent(ctx,Player.class);
                        intent.putExtra("cat",utl.js.toJson(feed));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation(act, player, getString(R.string.activity_image_trans));
                            startActivity(intent, options.toBundle());
                        }
                        else {
                            startActivity(intent);
                        }

                    }
                });


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    String json="";
    public void getCity ()   {

        Location loc=null;
        String  locj=utl.readFile("loc");
        if(locj!=null) {

            loc=utl.js.fromJson(locj,Location.class);
            city.setText(loc.getLatitude()+","+loc.getLongitude());
        }

        String url="http://maps.googleapis.com/maps/api/geocode/json?latlng="+(loc!=null?loc.getLatitude()+","+loc.getLongitude():"28.592,76.990")+"&sensor=true";
        utl.l(url);
        AndroidNetworking.get(url).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject jsonObj) {

                try {
                    String Status = jsonObj.getString("status");
                    if (Status.equalsIgnoreCase("OK")) {
                        JSONArray Results = jsonObj.getJSONArray("results");
                        JSONObject zero = Results.getJSONObject(0);
                        JSONArray address_components = zero
                                .getJSONArray("address_components");

                        for (int i = 0; i < address_components.length(); i++) {
                            JSONObject zero2 = address_components
                                    .getJSONObject(i);
                            String long_name = zero2.getString("long_name");
                            JSONArray mtypes = zero2.getJSONArray("types");
                            String Type = mtypes.getString(0);
                            if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                                // Address2 = Address2 + long_name + ", ";
                                String City = long_name;
                                Log.d(" CityName --->", City + "");
                                city.setText(City);

                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError ANError) {

            }
        });

    }
    public void getVideos(String query)
    {
        Location loc=null;
        String  locj=utl.readFile("loc");
        if(locj!=null) {

            loc=utl.js.fromJson(locj,Location.class);

        }
        String url=Constants.HOST+Constants.API_GET_VIDEOS+"?query="+ URLEncoder.encode(query)
                + "&location=" + URLEncoder.encode(loc!=null?loc.getLatitude()+","+loc.getLongitude():"28.592,76.990");;


        utl.l("Video : "+url);
        AndroidNetworking.get(url).build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {


                        json=response;

                        parser(response);



                    }

                    @Override
                    public void onError(ANError ANError) {


                        utl.l(ANError.getErrorDetail());
                    }
                });



    }

    public static boolean isVisible=true;
    @Override
    public void onResume()
    {
        isVisible=true;
        super.onResume();
    }
    @Override
    public void onPause()
    {
       // player.setLoop(false);

        isVisible=false;
        if(player.isPrepared()&&player.isPlaying())
            player.pause();


        super.onPause();

    }

    public int SELECT_VIDEO=1021;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
               Uri uri = (data.getData());

                Intent intent=new Intent(ctx,Upload.class);
                intent.putExtra("uri",uri.toString());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(act,upload, getString(R.string.activity_image_trans));
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }


    @Override
    public void onDestroy()
    {
        try {
            AndroidNetworking.cancel("dwd");
            bd.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    BaseDownloadTask bd;
    public void play(final Feed fed)
    {

        FileDownloader.setup(ctx);
        utl.l("Start dwd : "+fed.streamUrl);
        bd=utl.download(fed.streamUrl,fed.getLocalFile(),dwdCallack);
        bd.start();

    }
    GenricCallback dwdCallack=new GenricCallback() {
        @Override
        public void onStart( ) {

        }
        @Override
        public void onDo(Object obj) {
            int pro=(int)obj;
            utl.l("Progress : "+pro);
            prog.setProgress(pro,false);

            load.setText("Loaded : "+pro+" %");
        }
        @Override
        public void onDo(Object obj,Object obj2) {
            int pro=(int)obj;
            utl.l("Progress : "+pro);
            prog.setProgress(pro,false);
            load.setText("Loading  "+pro+" %  "+" @ "+(int)obj2+" KB/s");

        }

        @Override
        public void onDone(Object obj) {

            String url=(String)obj;

            prog.setVisibility(View.GONE);
            load.setVisibility(View.GONE);

            utl.l("Plating : "+url);

            play(url,false);

        }
    };

    public void play(String url,boolean isEasy)
    {

        player = ((EasyVideoPlayer) findViewById(R.id.player));

        player.setBottomLabelText("Trending");
        utl.l("Playing "+url);
        player.setCallback(new EasyVideoCallback() {
            @Override
            public void onStarted(EasyVideoPlayer player) {

            }

            @Override
            public void onPaused(EasyVideoPlayer player) {


            }

            @Override
            public void onPreparing(EasyVideoPlayer player) {

            }

            @Override
            public void onPrepared(EasyVideoPlayer player) {



            }

            @Override
            public void onBuffering(int percent) {
               /* if(percent>=3&&!forefulPause) {
                   player.start();
                    if(firstTime){
                        firstTime=false;
                    player.hideControls();}

                }*/
            }

            @Override
            public void onError(EasyVideoPlayer player, Exception e) {

            }

            @Override
            public void onCompletion(EasyVideoPlayer player) {

            }

            @Override
            public void onRetry(EasyVideoPlayer player, Uri source) {

            }

            @Override
            public void onSubmit(EasyVideoPlayer player, Uri source) {

            }
        });

        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        player.setSource(Uri.parse(url));
        player.setAutoPlay(true);




    }




    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission. ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission. ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage("Please Grant Permission for Coarse Location .")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(act,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission. ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:


                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }




}
