package in.hoptec.ppower;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.hoptec.ppower.adapters.PosterAdapter;
import in.hoptec.ppower.database.Feed;
import in.hoptec.ppower.views.SplashView;

public class LatestVideos extends AppCompatActivity {

    public Context ctx;
    public Activity act;

    String query="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=this;
        act=this;
        utl.fullScreen(this);

        setContentView(R.layout.activity_latest_videos);


        bindViews();


        ref.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getVideos(query);
            }
        });

       getVideos(query);
       parser("[{\"id\":\"2\",\"title\":\"TEST TESTTEST\",\"description\":\"TEST TEST TEST TEST TEST TEST TEST\",\"duration\":\"0\",\"user\":\"TEST\",\"user_image\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"user_id\":\"1\",\"likes\":\"0\",\"created_at\":\"2017-07-10 12:54:07\",\"artwork_url\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"stream_url\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"tag_list\":\"aa\"},{\"id\":\"3\",\"title\":\"TEST\",\"description\":\"TEST\",\"duration\":\"0\",\"user\":\"TEST\",\"user_image\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"user_id\":\"1\",\"likes\":\"0\",\"created_at\":\"2017-07-10 12:54:07\",\"artwork_url\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"stream_url\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"tag_list\":\"aa\"},{\"id\":\"1\",\"title\":\"TEST\",\"description\":\"TEST\",\"duration\":\"0\",\"user\":\"TEST\",\"user_image\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"user_id\":\"1\",\"likes\":\"0\",\"created_at\":\"2017-07-10 12:53:44\",\"artwork_url\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"stream_url\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"tag_list\":\"aa\"}]");
    }


    @BindView(R.id.rec)
    RecyclerView rec;

    @BindView(R.id.ref)
    SwipeRefreshLayout ref;


    void bindViews() {

        ButterKnife.bind(this);

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
                videos.add(fe);
                videos.add(fe);
            }

            setUpList(videos);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void getVideos(String query)
    {
        String url=Constants.HOST+Constants.API_GET_VIDEOS+"?query="+ URLEncoder.encode(query);
        utl.l("Video : "+url);
        AndroidNetworking.get(url).build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {


                        parser(response);


                    }

                    @Override
                    public void onError(ANError ANError) {

                        utl.l(ANError.getErrorDetail());
                    }
                });



    }

    public void setUpList(ArrayList<Feed> videos)
    {

        ref.setRefreshing(false);

        PosterAdapter adapter=new PosterAdapter(ctx,videos)
        {
            @Override
            public void click(Feed cat, int id) {
                super.click(cat, id);


            }
        };

       LinearLayoutManager layoutManager= new LinearLayoutManager(ctx);
        rec.setLayoutManager(layoutManager);
        rec.setAdapter(adapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rec.getContext(),
                layoutManager.getOrientation());

        rec.addItemDecoration(dividerItemDecoration);

    }


}
