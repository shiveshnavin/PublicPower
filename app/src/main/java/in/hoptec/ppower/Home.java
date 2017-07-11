package in.hoptec.ppower;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.iceteck.silicompressorr.SiliCompressor;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.hoptec.ppower.utils.Bouncer;

public class Home extends AppCompatActivity {


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
        ctx=this;
        act=this;
        utl.init(ctx);
        utl.fullScreen(this);
        setContentView(R.layout.activity_home);
        player = ((EasyVideoPlayer) findViewById(R.id.player));

        getVideos("");

        ButterKnife.bind(this);
        AndroidNetworking.initialize(ctx);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        uploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ctx,Report.class));
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

        play(TEST_URL);
    }

    boolean isFirstTime=true;

    public void play(String url)
    {

        player.setLoop(true);
        player.setCallback(new EasyVideoCallback() {
            @Override
            public void onStarted(EasyVideoPlayer player) {

            }

            @Override
            public void onPaused(EasyVideoPlayer player) {
                player.pause();

            }

            @Override
            public void onPreparing(EasyVideoPlayer player) {

            }

            @Override
            public void onPrepared(EasyVideoPlayer player) {





            }

            @Override
            public void onBuffering(int percent) {

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


    String json="";
    public void getVideos(String query)
    {

        String url=Constants.HOST+Constants.API_GET_VIDEOS+"?query="+ URLEncoder.encode(query);
        utl.l("Video : "+url);
        AndroidNetworking.get(url).build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {


                        json=response;


                    }

                    @Override
                    public void onError(ANError ANError) {


                        utl.l(ANError.getErrorDetail());
                    }
                });



    }

    @Override
    public void onPause()
    {
        player.setLoop(false);

        player.pause();

        super.onPause();

    }

}
