package in.hoptec.ppower;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.hoptec.ppower.adapters.CommentAdapter;
import in.hoptec.ppower.database.Comment;
import in.hoptec.ppower.database.Feed;
import in.hoptec.ppower.utils.GenricCallback;
import in.hoptec.ppower.views.GoalProgressBar;


public class Player extends AppCompatActivity {
    private static final int RECOVERY_REQUEST = 1;
    public static Context ctx;
    public static Activity act;


    private static final String TEST_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    private EasyVideoPlayer player;


    @BindView(R.id.prog)
    GoalProgressBar prog;

    @BindView(R.id.load)
    TextView load;


     @BindView(R.id.rec)
    RecyclerView rec;

     @BindView(R.id.title)
    TextView title;

    @BindView(R.id.text)
    TextView name;

    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.opt)
    ImageView userimage;

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
    GenricUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=this;
        act=this;
        utl.fullScreen(act);
        utl.init(this);

        setContentView(R.layout.activity_player);

       // videoView = (VideoView) findViewById(R.id.myVideoView);
      //  playerSurfaceView = (SurfaceView)findViewById(R.id.playersurface);

        //pbLoading = (ProgressBar) findViewById(R.id.pbVideoLoading);


        user=utl.readUserData();


        ButterKnife.bind(this);
        js=new Gson();

         user=utl.readUserData();

        String jst=getIntent().getStringExtra("cat");
        Integer vid=getIntent().getIntExtra("vid",0);

        if(jst!=null)
        {

            fed=js.fromJson(jst,Feed.class);
            isLiked=fed.isLiked(user.uid);

            set();
            play(fed );

        }

        else if(vid!=0){


            getVideos("",vid);


        }

        getVideos(fed.id);
        getComments();

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

                try {
                    setUpList(comments);
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //  comment();
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

    }



    ArrayList<Feed>  parser(String response)
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

            return videos;


        } catch (Exception e) {
            e.printStackTrace();
            return videos;
        }


    }


    public void getVideos(String query,final Integer vid)
    {

        String url=Constants.HOST+Constants.API_GET_VIDEOS+"?query="+ URLEncoder.encode(query);
        utl.l("Video : "+url);
        AndroidNetworking.get(url).build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {


                        ArrayList<Feed>  feeds= parser(response);
                        for(int i=0;i<feeds.size();i++)
                        {
                            if(feeds.get(i).id.equals(""+vid))
                            {
                                fed=feeds.get(i);
                                isLiked=fed.isLiked(user.uid);
                                set();
                                play(fed );

                                break;
                            }
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {


                        utl.l(ANError.getErrorDetail());
                    }
                });



    }




    public void getVideos(final String vid)
    {

        String url=Constants.HOST+Constants.API_GET_VIDEOS+"?query="+ URLEncoder.encode("");
        utl.l("Video : "+url);
        AndroidNetworking.get(url).build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {


                        ArrayList<Feed>  feeds= parser(response);
                        for(int i=0;i<feeds.size();i++)
                        {
                            if(feeds.get(i).id.equals(""+vid))
                            {
                                fed=feeds.get(i);
                                isLiked=fed.isLiked(user.uid);
                                set();

                                break;
                            }
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {


                        utl.l(ANError.getErrorDetail());
                    }
                });



    }



    VideoView videoView;ProgressBar pbLoading;
    boolean forefulPause =true;
    boolean firstTime =true;

    @Override
    public void onDestroy()
    {
        AndroidNetworking.cancel("dwd");
        bd.cancel();
        super.onDestroy();
    }

    public void pause()
    {


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

            final String url=(String)obj;

            prog.setVisibility(View.GONE);
            load.setVisibility(View.GONE);

            utl.l("Plating : "+url);
            Handler h=new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    play(url,false);

                }
            },1000);


        }
    };

    public void play(String url,boolean isEasy)
    {

        player = ((EasyVideoPlayer) findViewById(R.id.player));

        utl.l("Playing "+url);
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

                e.printStackTrace();

            }

            @Override
            public void onCompletion(EasyVideoPlayer player) {

                try {
                    if(player.getDuration()<1)
                    {
                        File f=new File(fed.getLocalFile());
                        if(f.exists())
                            f.delete();

                        play(fed);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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


    public void comment(final String comment)
    {

        //GET user user_image user_id comment vid

        String url=Constants.HOST+Constants.API_ADD_COMMENT+"?user="+
                URLEncoder.encode(user.user_fname)
                +"&comment="+ URLEncoder.encode(comment)
                +"&vid="+fed.id
                +"&user_image="+  URLEncoder.encode(user.user_image)
                +"&user_id="+user.uid ;
    utl.l(url);

        AndroidNetworking.get(url).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                utl.l(response);

                utl.l("Comment Success ",comment);


                utl.snack(scrl,"Comment Posted !");


                getComments();


            }

            @Override
            public void onError(ANError ANError) {

            }
        });

    }




    public void getComments()
    {

        String url=Constants.HOST+Constants.API_GET_COMMENTS+"?vid="+fed.id;
        utl.l(url);
        AndroidNetworking.get(url).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                utl.l(response.replace(",{","\n"));
                comments=new ArrayList<>();
                try {
                    JSONArray jr=new JSONArray(response);
                    for(int i=0;i<jr.length();i++)
                    {

                        comments.add(js.fromJson(jr.get(i).toString(),Comment.class));
                    }

                    if(adapter!=null&&rv!=null)
                    {
                        adapter=new CommentAdapter(ctx, comments, new CommentAdapter.CallBacks() {
                            @Override
                            public void share(Comment cat, int id) {

                            }

                            @Override
                            public void like(Comment cat, boolean like) {

                            }

                            @Override
                            public void click(Comment cat, int id, View v) {

                            }
                        });
                        rv.setAdapter(adapter);


                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError ANError) {

                utl.l("434"+ANError.getErrorDetail());
            }
        });

    }

    boolean isLiked;
    public void set()
    {

        title.setText(fed.title);
        desc.setText(fed.description+"\n\n"+fed.likes+" Likes");
        time.setText(""+fed.getCreatedAt());

        name.setText(""+fed.user);
        utl.changeColorDrawable(userimage,R.color.white);

        Picasso.with(ctx).load(fed.userImage).into(userimage);

        if(fed.isLiked(user.uid))
        {
            like.setImageResource(R.drawable.loved);

        }
        else {
            like.setImageResource(R.drawable.love);

        }


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


        if(!isLiked)
        {
            like.setImageResource(R.drawable.loved);
            fed.likes=""+(Integer.parseInt(fed.likes)+1);

        }
        else {
            like.setImageResource(R.drawable.love);
            fed.likes=""+(Integer.parseInt(fed.likes)-1);

        }

        //set();


        likee(fed);



    }



    public void likee(Feed fedd)
    {
        //GET user_id vid

        String url=Constants.HOST+Constants.API_LIKE+"?user_id="+user.uid+"&vid="+fed.id;
        utl.l(url);


        AndroidNetworking.get(url).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {

                utl.snack(act,"Thanks ! ");
                fed=utl.js.fromJson(response,Feed.class);
                set();

            }

            @Override
            public void onError(ANError ANError) {

                utl.l(ANError.getErrorDetail());
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

        utl.inputDialog(ctx, "", "Say something...", utl.TYPE_DEF, new utl.InputDialogCallback() {
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



    RecyclerView rv;
    CommentAdapter adapter;
    public void setUpList(ArrayList<Comment> feeds)
    {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
        View convertView = LayoutInflater.from(ctx).inflate(R.layout.commets_diag, null);
        alertDialog.setView(convertView);



        Dialog dialog = alertDialog.create();
        rv = (RecyclerView) convertView.findViewById(R.id.rec);
         View c = ( View) convertView.findViewById(R.id.comment);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(ctx));
         adapter=new CommentAdapter(ctx, feeds, new CommentAdapter.CallBacks() {
            @Override
            public void share(Comment cat, int id) {

            }

            @Override
            public void like(Comment cat, boolean like) {

            }

            @Override
            public void click(Comment cat, int id, View v) {

            }
        });
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);




        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);



        dialog.show();




    }
    @Override
    public void onPause()
    {

        pause();
        super.onPause();
    }





}
