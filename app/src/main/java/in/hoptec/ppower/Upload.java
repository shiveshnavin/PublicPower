package in.hoptec.ppower;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.hoptec.ppower.utils.ExecuterU;
import in.hoptec.ppower.views.CustomProgress;
import in.hoptec.ppower.views.GoalProgressBar;

public class Upload extends AppCompatActivity {

    @BindView(R.id.prog)
    GoalProgressBar prog;

    EasyVideoPlayer player;

    @BindView(R.id.cancel)
    View cancel;


    @BindView(R.id.done)
    View done;

    @BindView(R.id.title)
    EditText title;


    @BindView(R.id.desc)
    EditText desc;

    GenricUser user;
    FirebaseAuth mAuth;
    int duration=200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utl.init(this);
        setContentView(R.layout.activity_upload);

        user=utl.readUserData();
        ButterKnife.bind(this);
        mAuth=FirebaseAuth.getInstance();

        setTitle("Upload");


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uploadTask!=null)
                    uploadTask.cancel();

                finish();


            }
        });



        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(dwdUrl==null)
                {
                    utl.snack(Upload.this,"Still Uploading...");
                }
                else {

                    String path=Constants.getFolder()+"/thumb_"+SystemClock.uptimeMillis()+".png";
                    utl.l("Thumb HTG PATH: "+path);
                    utl.l("Video HTG: "+dwdUrl);
                    utl.saveVideoThumb(path,filePath);
                    utl.snack(Upload.this,"Requesting...");
                    utl.showDig(true,Upload.this);
                    //GET title description user user_image user_id artwork_url stream_url duration
                    startThumbUpload(path, new UploadCallBack() {
                        @Override
                        public void progress(int percent) {

                        }

                        @Override
                        public void done(String turl) {
                            utl.l("Thumb HTG URL: "+turl);

                            finalli(dwdUrl,turl);
                        }

                        @Override
                        public void cancel() {

                        }
                    });

                }


            }
        });

        prog.setProgress(0,true);

        player = ((EasyVideoPlayer) findViewById(R.id.player));
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


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        des=Constants.getFolder()+"/";

        if (Intent.ACTION_SEND.equals(action) && type != null) {

             if (type.startsWith("video/")) {
                handleSendImage(intent);
            }
        }

        try {
            Uri video=Uri.parse(getIntent().getStringExtra("uri"));
            if(video!=null)
            {
                handelUri(video);
            }
        } catch (Exception e) {


        }


    }

    public void finalli(String dwdUrl,String thumbUrl){

        String url=Constants.HOST+Constants.API_UPLOAD+"?"
                +"title="+ URLEncoder.encode(""+title.getText().toString())
                +"&description="+ URLEncoder.encode(""+desc.getText().toString())
                +"&user="+ URLEncoder.encode(""+user.user_fname)
                +"&duration="+(duration)
                +"&user_image="+ URLEncoder.encode(""+user.user_image)
                +"&user_id="+ URLEncoder.encode(""+user.uid)
                +"&artwork_url="+ URLEncoder.encode(thumbUrl)
                +"&stream_url="+ URLEncoder.encode(dwdUrl)
                ;

        utl.l(url);
        ANRequest.GetRequestBuilder get=new ANRequest.GetRequestBuilder(url);
        get.build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                utl.toast(Upload.this,"Upload Success !!");
                utl.l(response);
                startActivity(new Intent(Upload.this,Home.class));
                utl.showDig(false,Upload.this);

                finish();;

            }

            @Override
            public void onError(ANError ANError) {
                utl.showDig(false,Upload.this);

                utl.l(ANError.getErrorDetail());

            }
        });
    }
    UploadCallBack cb= new UploadCallBack() {
        @Override
        public void progress(int percent) {
            prog.setProgress(percent);

        }

        @Override
        public void done(String dwdurl) {

            utl.l("Downlaod URL :" +dwdurl);
            dwdUrl=dwdurl;
            uploadFinished=true;

            prog.setProgress(100);
            player.setSource(Uri.parse(dwdurl));
            player.start();
        }

        @Override
        public void cancel() {
            utl.l("Cancelled ");

        }
    };

    String dwdUrl=null;
    public boolean uploadStarted=false;
    public boolean uploadFinished=false;

    public void processUri(final String  video)
    {

        try{
            startUpload(video,cb);
/*
            player.setSource(Uri.parse(video));
            player.start();
            duration=player.getDuration();*/
/*

            if (mAuth.getCurrentUser() != null)
            {
                mAuth.getCurrentUser().reload();
                // startUpload(video,cb);

                mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        startUpload(video,cb);

                    }
                });

            }
            else
            {
                mAuth.signInAnonymously()
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                startUpload(video,cb);

                                Log.d("FirebaseAuth", "signInAnonymously:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful())
                                {
                                    Log.w("FirebaseAuth", "signInAnonymously", task.getException());
                                    Toast.makeText(Upload.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                // ...
                            }
                        });
            }


*/

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }



    String  des;
    String  filePath;

    public void handelUri(final Uri video)
    {
        utl.l("Rec : "+video.toString());
        ExecuterU ex=new ExecuterU(Upload.this,"Compressing...This may take a while !"){
            @Override
            public void doIt() {
                super.doIt();
                try {
                    utl.l("HTG Compressing : "+ (video.toString()));
                    utl.l("HTG Compressing to : "+des);
                    filePath = SiliCompressor.with(ctx).compressVideo((video.toString()), des);

                } catch ( Exception e) {
                    e.printStackTrace();
                }


            }


            @Override
            public void doNe() {
                super.doNe();

                processUri( (filePath) );
                utl.l("HTG Compressed : "+filePath);


            }
        };
        ex.execute();
/*
             try {
                 filePath=utl.getRealPathFromUri(video);
             } catch (Exception e) {
                 e.printStackTrace();
                 filePath=video.toString().replace("file://","");
             }
             processUri( (filePath) );*/

    }
    private void handleSendImage(Intent intent) {


         final Uri video = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

         if (video != null) {

             handelUri(video);

         } else{
            Toast.makeText(this, "Error occured, URI is invalid", Toast.LENGTH_LONG).show();
        }
    }

    public static interface UploadCallBack{
        public void progress(int percent);
        public void done(String dwdurl);
        public void cancel();
    }















    public void startThumbUpload(String path,final UploadCallBack callBack)
    {


        String url="http://feelinglone.com/test/file_upload.php";
        utl.l(url);
        File f=new File(path);
        if(f.length()<2)
        {
            utl.snack(Upload.this,"Empty File !");
            return;
        }


        AndroidNetworking.upload(url).addMultipartFile("file",f).build().setUploadProgressListener(new UploadProgressListener() {
            @Override
            public void onProgress(long bytesUploaded, long totalBytes) {
                Double per=100.0*(bytesUploaded/totalBytes);
                callBack.progress(per.intValue());
                utl.l("Progress : "+per);
            }
        }).getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    callBack.progress(100);
                    utl.snack(Upload.this,response.getString("message"));
                    utl.l(response.toString());
                    callBack.done(response.getString("link"));



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(ANError ANError) {

                utl.l(ANError.getErrorDetail());
            }
        });


    }



    public void startUpload(String  uri,final UploadCallBack callBack) {


        String url="http://feelinglone.com/test/file_upload.php";
        utl.l(url);
        File f=new File(uri);
        if(f.length()<2)
        {
            utl.snack(Upload.this,"Empty File !");
            return;
        }


        AndroidNetworking.upload(url).addMultipartFile("file",f).build().setUploadProgressListener(new UploadProgressListener() {
            @Override
            public void onProgress(long bytesUploaded, long totalBytes) {
                Double per=100.0*(bytesUploaded/totalBytes);
                callBack.progress(per.intValue());
                utl.l("Progress : "+per);
            }
        }).getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String res) {
                try {
                    utl.l("HTG "+res);
                    callBack.progress(100);
                    JSONObject response=new JSONObject(res);
                    utl.snack(Upload.this,response.getString("message"));
                     callBack.done(response.getString("stream_link"));



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError ANError) {

                utl.l("HTG 506 ER "+ANError.getErrorBody()+"\n"+ANError.getErrorDetail());
            }
        });







    }











        UploadTask uploadTask;
    public void  startThumbUpload1(String path,final UploadCallBack callBack)
    {

        FirebaseStorage storage = FirebaseStorage.getInstance(Constants.FIRE_BASE_STORAGE);
        StorageReference storageRef = storage.getReference().child(Constants.getApp());

        // StorageReference uploadRef=storageRef.child(utl.refineString(utl.getDateTime(),"_"));

        File file=new File(path);

        utl.l("File : "+path);
        utl.l("File Size: "+file.length());

        InputStream ip=null;
        try {
            ip=new FileInputStream(file);
        } catch ( Exception e) {
            e.printStackTrace();
        }
        if(ip==null)
        {
            return;
        }
        uploadTask=storageRef.child(file.getName()).putStream(ip);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                callBack.cancel();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                utl.l("Download URL : "+downloadUrl.toString());
                callBack.done(downloadUrl.toString());
            }
        });
    }
    public void startUpload1(String  uri,final UploadCallBack callBack)
    {

        if(!uploadStarted)
        {
            uploadStarted=true;
        }
        else
        {
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance(Constants.FIRE_BASE_STORAGE);
        StorageReference storageRef = storage.getReference().child(Constants.getApp());
        String path=uri;

       // StorageReference uploadRef=storageRef.child(utl.refineString(utl.getDateTime(),"_"));

        File file=new File(path);

        utl.l("File : "+path);
        utl.l("File Size: "+file.length());

        InputStream ip=null;
        try {
              ip=new FileInputStream(file);
        } catch ( Exception e) {
            e.printStackTrace();
        }
        if(ip==null)
        {
            return;
        }
          uploadTask=storageRef.child(file.getName()).putStream(ip);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                callBack.cancel();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                utl.l("Download URL : "+downloadUrl.toString());
                callBack.done(downloadUrl.toString());
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Uri sessionUri = taskSnapshot.getUploadSessionUri();

                Double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");

                callBack.progress(progress.intValue());


                if (sessionUri != null  ) {


                }
            }
        });






    }



}
