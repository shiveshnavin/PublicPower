package in.hoptec.ppower;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.androidnetworking.AndroidNetworking;

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

            }
        });

        populate();


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
}
