package in.hoptec.ppower;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.hoptec.ppower.adapters.PosterAdapter;
import in.hoptec.ppower.database.Feed;
import in.hoptec.ppower.views.SplashView;

public class LatestVideos extends AppCompatActivity {

    public Context ctx;
    public Activity act;

    public static String likes="";
    public static GenricUser user;

    String query="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=this;
        act=this;
        utl.fullScreen(this);

        setContentView(R.layout.activity_latest_videos);

        user=utl.readUserData();

        bindViews();

        ref.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getVideos(query);
            }
        });


        String json=""+getIntent().getStringExtra("json");
        if(json.length()>10)
        {
            parser(json);
        }
        else
            getVideos(query);


        //  parser("[{\"id\":\"2\",\"title\":\"TEST TESTTEST\",\"description\":\"TEST TEST TEST TEST TEST TEST TEST\",\"duration\":\"0\",\"user\":\"TEST\",\"user_image\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"user_id\":\"1\",\"likes\":\"0\",\"created_at\":\"2017-07-10 12:54:07\",\"artwork_url\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"stream_url\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"tag_list\":\"aa\"},{\"id\":\"3\",\"title\":\"TEST\",\"description\":\"TEST\",\"duration\":\"0\",\"user\":\"TEST\",\"user_image\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"user_id\":\"1\",\"likes\":\"0\",\"created_at\":\"2017-07-10 12:54:07\",\"artwork_url\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"stream_url\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"tag_list\":\"aa\"},{\"id\":\"1\",\"title\":\"TEST\",\"description\":\"TEST\",\"duration\":\"0\",\"user\":\"TEST\",\"user_image\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"user_id\":\"1\",\"likes\":\"0\",\"created_at\":\"2017-07-10 12:53:44\",\"artwork_url\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"stream_url\":\"https:\\/\\/lh3.googleusercontent.com\\/viOtN25XGouzS_D2uEJIBuz4AuoQLXPQ3NkIej_8NeFxhBLzndqzlpg_UM-fkSsglw=h900-rw\",\"tag_list\":\"aa\"}]");


        setUpToolbar();


    }


    @BindView(R.id.rec)
    RecyclerView rec;

    @BindView(R.id.ref)
    SwipeRefreshLayout ref;


    void bindViews() {

        ButterKnife.bind(this);

    }


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
/*
    @Override
    public void onBackPressed()
    {
        finish();
    }*/

    public void getVideos(String query)
    {
        loading(true);
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

        PosterAdapter adapter=new PosterAdapter(ctx, videos, new PosterAdapter.CallBacks() {
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



    @Override
    protected void onDestroy() {
        //save last queries to disk
        utl.setSearches(searchBar.getLastSuggestions());
        super.onDestroy();
    }



    @BindView(R.id.search)
    MaterialSearchBar searchBar;
    public void setUpToolbar()
    {



         //  searchBar.setHint("Search");
        // searchBar.setNavButtonEnabled(false);


        searchBar.setSpeechMode(false);
        //enable searchbar callbacks
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {

            @Override
            public void onSearchStateChanged(boolean enabled) {
                String s = enabled ? "enabled" : "disabled";
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                utl.hideSoftKeyboard(act);
                // utl.snack(act,text.toString());



                loading(true);

                query=text.toString();
                getVideos(query);
              //  utl.addSearches(query);



            }

            @Override
            public void onButtonClicked(int buttonCode) {
                switch (buttonCode) {
                    case MaterialSearchBar.BUTTON_NAVIGATION:
                        break;
                    case MaterialSearchBar.BUTTON_SPEECH:
                }
            }

        });
        //restore last queries from disk

        if(utl.getSearches()!=null)
        {
           List<String > list = utl.getSearches();
            searchBar.setLastSuggestions(list);;
            utl.l("Last Searh set "+list.size());

        }



        try {
            searchBar.setTextColor(R.color.grey_700);
            searchBar.setTextHintColor(R.color.grey_400);
            searchBar.setHint("Search...");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    public void likee(Feed fed)
    {
        //GET user_id vid

        String url=Constants.HOST+Constants.API_LIKE+"?user_id="+user.uid+"&vid="+fed.id;
        utl.l(url);


        AndroidNetworking.get(url).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {

                utl.snack(act,"Thanks ! ");

            }

            @Override
            public void onError(ANError ANError) {

                    utl.l(ANError.getErrorDetail());
            }
        });
    }




}
