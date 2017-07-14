package in.hoptec.ppower;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;

import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.hoptec.ppower.adapters.MyPosterAdapter;
import in.hoptec.ppower.adapters.PosterAdapter;
import in.hoptec.ppower.database.Feed;
import in.hoptec.ppower.utils.GenricCallback;

public class MyVideos extends AppCompatActivity {

    public Context ctx;
    public Activity act;

    @BindView(R.id.opt)
    ImageView userimage;

    @BindView(R.id.text)
    TextView name;

    @BindView(R.id.time)
    TextView time;
    public static String likes="";
    public static GenricUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utl.fullScreen(this);

        ctx=this;
        act=this;


        user=utl.readUserData();
        setContentView(R.layout.activity_my_videos);
        bindViews();


        Picasso.with(ctx).load(""+user.user_image).placeholder(R.drawable.user).into(userimage);
        name.setText(user.user_fname);
        time.setText(user.extra1);


/*

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                utl.snack(findViewById(R.id.activity_my_videos), "Are you sure ? ", "LOGOUT", new GenricCallback() {
                    @Override
                    public void onStart() {

                        utl.logout();
                        startActivity(new Intent(ctx,Splash.class));
                        finish();
                    }

                    @Override
                    public void onDo(Object obj) {

                    }

                    @Override
                    public void onDo(Object obj, Object obj2) {

                    }

                    @Override
                    public void onDone(Object obj) {

                    }
                });


            }
        });
        getVideos("");
    }

    void bindViews() {

        ButterKnife.bind(this);

    }


    @BindView(R.id.rec)
    RecyclerView rec;

    @BindView(R.id.ref)
    SwipeRefreshLayout ref;


    public void loading(boolean isLoading)
    {
        AVLoadingIndicatorView splashView=(AVLoadingIndicatorView)findViewById(R.id.splash_view2);
        View rec=findViewById(R.id.rec);

        if(isLoading)
        {
            splashView.show();
            rec.setVisibility(View.INVISIBLE);


        }
        else
        {
            rec.setVisibility(View.VISIBLE);
            splashView.hide();

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

            setUpList(videos);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void getVideos(String query)
    {
        loading(true);
        String url=Constants.HOST+Constants.API_GET_USER_VIDEOS+"?query="+ URLEncoder.encode(query)+"&user_id="+user.uid+"&city="
                +URLEncoder.encode(utl.getCity());
        utl.l("Video : "+url);
        AndroidNetworking.get(url).build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {


                        loading(false);
                        parser(response);


                    }

                    @Override
                    public void onError(ANError ANError) {

                        utl.snack(act,"Error Occured !");
                        ref.setRefreshing(false);
                        loading(false);
                        utl.l(ANError.getErrorDetail());
                    }
                });



    }

    public void setUpList(ArrayList<Feed> videos)
    {

        ref.setRefreshing(false);

        for(int i=0;i<videos.size();i++)
        {
            Feed v=videos.get(i);
            if(v.isLiked(user.uid))
            {
                likes+=":"+v.id;
            }
        }

        MyPosterAdapter adapter=new MyPosterAdapter(ctx, videos, new MyPosterAdapter.CallBacks() {
            @Override
            public void share(Feed cat, int id) {



                Intent txtIntent = new Intent(Intent.ACTION_SEND);
                txtIntent .setType("text/plain");
                txtIntent .putExtra(Intent.EXTRA_SUBJECT, cat.title);
                txtIntent .putExtra(Intent.EXTRA_TEXT,(Constants.HOST+Constants.API_SHARE+"?vid="+cat.id));
                startActivity(Intent.createChooser(txtIntent ,"Share "+cat.getTitle()));



            }

            @Override
            public void like(Feed cat, boolean like) {


                likee(cat);


            }

            @Override
            public void click(Feed cat, int id,View v) {

                Intent intent=new Intent(ctx,Player.class);
                intent.putExtra("cat",utl.js.toJson(cat));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(act, v, getString(R.string.activity_image_trans));
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }



            }
        });

        LinearLayoutManager layoutManager= new LinearLayoutManager(ctx);
        rec.setLayoutManager(layoutManager);
        rec.setAdapter(adapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rec.getContext(),
                layoutManager.getOrientation());

        rec.addItemDecoration(dividerItemDecoration);

    }


    public void likee(Feed fed)
    {
        //GET user_id vid

        String url=Constants.HOST+Constants.API_REMOVE_VIDEO+"?user_id="+user.uid+"&vid="+fed.id;
        utl.l(url);


        AndroidNetworking.get(url).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {

                utl.snack(act,""+response);
                getVideos("");

            }

            @Override
            public void onError(ANError ANError) {

                utl.l(ANError.getErrorDetail());
            }
        });
    }


}
