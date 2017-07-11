package in.hoptec.ppower;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.hoptec.ppower.database.Comment;
import in.hoptec.ppower.database.Feed;


public class Player extends AppCompatActivity {
    private static final int RECOVERY_REQUEST = 1;
    public static Context ctx;
    public static Activity act;


    private static final String TEST_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    private EasyVideoPlayer player;


    GenricUser user;

     @BindView(R.id.rec)
    RecyclerView rec;

     @BindView(R.id.title)
    TextView title;


     @BindView(R.id.desc)
    TextView desc;

    ArrayList<Comment> comments;


     @BindView(R.id.scrl)
    NestedScrollView scrl;


     @BindView(R.id.like)
    ImageView like;

     @BindView(R.id.comment)
    ImageView comment;

     @BindView(R.id.share)
    ImageView share;


    Gson js;
    Feed fed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=this;
        act=this;
        utl.fullScreen(act);

        setContentView(R.layout.activity_player);
        player = ((EasyVideoPlayer) findViewById(R.id.player));



        ButterKnife.bind(this);
        js=new Gson();

        user=utl.readUserData();

        String jst=getIntent().getStringExtra("cat");
        fed=js.fromJson(jst,Feed.class);
        set();

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   utl.toast(ctx,"Liked");
                like();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment();
            }
        });


        utl.changeColorDrawable(comment,R.color.white);


/*

        <com.afollestad.easyvideoplayer.EasyVideoPlayer
        android:transitionName="@string/activity_image_trans"
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@+id/player"
        android:layout_width="match_parent"
        android:layout_height="270dp" />



*/

        play(fed.streamUrl);
    }


    boolean forefulPause =true;
    boolean firstTime =true;

    public void play(String url)
    {


        player.setBottomLabelText(fed.title);
        player.setCallback(new EasyVideoCallback() {
            @Override
            public void onStarted(EasyVideoPlayer player) {

            }

            @Override
            public void onPaused(EasyVideoPlayer player) {

                forefulPause =true;
            }

            @Override
            public void onPreparing(EasyVideoPlayer player) {

            }

            @Override
            public void onPrepared(EasyVideoPlayer player) {



               // player.start();
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




    }


    public void comment(final String comment)
    {

        String url=Constants.HOST+"/api/comment.php?user="+
                URLEncoder.encode(utl.getUser().getDisplayName())+"&comment="+ URLEncoder.encode(comment)+"&vid="+fed.id;
        AndroidNetworking.get(url).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                utl.l(response);

                    utl.l("Comment Success ",comment);


                utl.snack(scrl,"Comment Posted !");


                getCOmments();


            }

            @Override
            public void onError(ANError ANError) {

            }
        });

    }





    public void getCOmments()
    {

        String url=Constants.HOST+"/api/getcomments.php?vid="+fed.id;
        AndroidNetworking.get(url).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                utl.l(response);
                comments=new ArrayList<>();
                try {
                    JSONArray jr=new JSONArray(response);
                    for(int i=0;i<jr.length();i++)
                    {

                        comments.add(js.fromJson(jr.get(i).toString(),Comment.class));
                    }


                    setUpList(comments);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError ANError) {

            }
        });

    }

    public void set()
    {

        title.setText(fed.title);
        desc.setText(fed.description+"\n\n"+fed.getCreatedAt()+"\n\n"+fed.likes+" Likes");

    }

    public boolean check()
    {

        boolean loggedin=true;


        if(utl.getUser()==null)
        {
            loggedin=false;
        }

        return loggedin;

    }

    public void login(final String doi)
    {




        Intent itx=(new Intent(Player.this, Splash.class));
        itx.putExtra("action","login");
        itx.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(itx);
        finish();
        if(true)
            return;
        View rootView = act.getWindow().getDecorView().getRootView();
        Snackbar snackbar = Snackbar.make(rootView,  "Must Login First !", Snackbar.LENGTH_LONG);
        snackbar.setAction("LOGIN", new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        snackbar.setActionTextColor(act.getResources().getColor(R.color.material_red_A400));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.red_300));


        snackbar.setActionTextColor(act.getResources().getColor(R.color.white));

         int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);


    }


    public void like()
    {


        if(!check())
        {

            login("like");
            return;
        }

        set();
        like.setImageResource(R.drawable.loved);


        String url=Constants.HOST+"/api/like.php?vid="+fed.id;
        if(fed.isLiked(user.uid))
        {
            like.setImageResource(R.drawable.love);
            url=Constants.HOST+"/api/unlike.php?vid="+fed.id;

        }

        AndroidNetworking.get(url).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                utl.l(response);



                utl.snack(findViewById(R.id.hold),""+response);





            }

            @Override
            public void onError(ANError ANError) {

            }
        });



    }


    AlertDialog dig;
    public void comment()
    {

        if(!check())
        {
            login("comment");
            return;
        }

        utl.inputDialog(ctx, "Comment", "Say something...", utl.TYPE_DEF, new utl.InputDialogCallback() {
            @Override
            public void onDone(String text) {
                comment(text);
            }
        });



    }



    public void share()
    {



        Intent txtIntent = new Intent(Intent.ACTION_SEND);
        txtIntent .setType("text/plain");
        txtIntent .putExtra(Intent.EXTRA_SUBJECT, fed.title);
        txtIntent .putExtra(Intent.EXTRA_TEXT, fed.streamUrl);
        startActivity(Intent.createChooser(txtIntent ,"Share Media"));
    }




    public void setUpList(ArrayList<Comment> feeds)
    {









    }




}
