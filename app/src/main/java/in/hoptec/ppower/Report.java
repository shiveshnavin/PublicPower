package in.hoptec.ppower;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Report extends AppCompatActivity {


    @BindView(R.id.phone)
    EditText phone;

    @BindView(R.id.fir)
    EditText fir;

    @BindView(R.id.name)
    EditText name;

    @BindView(R.id.subject)
    EditText subject;

    @BindView(R.id.casee)
    EditText casee;

    @BindView(R.id.attach)
    TextView attach;


    public String path;

    public   Context ctx;
    public   Activity act;

    GenricUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        ctx = this;
        act = this;


        user = utl.readUserData();

        phone.setText(""+user.extra1);
        name.setText(""+user.user_fname);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText v[]={casee,name,subject,phone};

                for (EditText vv:v) {

                    if(vv.getText().toString().length()<2)
                    {
                        vv.setError("Mandatory");
                        return;
                    }
                }


                send();

            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, SELECT_VIDEO);

            }
        });
    }
    public void send() {
        utl.showDig(true,ctx);
        if (path != null)
            startThumbUpload(path, new Upload.UploadCallBack() {
                @Override
                public void progress(int percent) {

                    attach.setText("\nUploading : "+percent);


                }

                @Override
                public void done(String dwdurl) {
                    uploading=true;
                    utl.snack(act,"Uploaded !");
                    dwd=dwdurl;
                    donee(dwdurl);


                }


                @Override
                public void cancel() {

                }
            });
        else
            donee("");

    }


    public void donee(String aurl) {



        String url = Constants.HOST + Constants.API_ADD_REPORT + "?"
                + "user=" + URLEncoder.encode("" + name.getText().toString())
                + "&user_image=" + URLEncoder.encode("" + user.user_image)
                + "&user_id=" + URLEncoder.encode(user.uid)
                + "&title=" + URLEncoder.encode("" + subject.getText().toString())
                + "&comment=" + URLEncoder.encode("" + casee.getText().toString())
                + "&extra0=" + URLEncoder.encode("" + aurl)
                + "&extra1=" + URLEncoder.encode("" + utl.getFCMToken())
                + "&extra2=" + URLEncoder.encode("" + fir.getText().toString())
                + "&extra3=" + URLEncoder.encode("" + phone.getText().toString());


        utl.l(url);

        AndroidNetworking.get(url).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                utl.showDig(false,ctx);

                utl.diag(ctx, "Sent ! ", "" + response,false, "FINISH", new utl.ClickCallBack() {
                    @Override
                    public void done(DialogInterface dialogInterface) {
                        finish();
                    }
                });
            }

            @Override
            public void onError(ANError ANError) {
                utl.showDig(false,ctx);


                utl.l(ANError.getErrorDetail());
            }
        });


    }
    String dwd="";
    public boolean uploading=true;
    public boolean uploaded=false;


    public void startThumbUpload(String path, final Upload.UploadCallBack callBack) {

        uploading=true;
        String url = "http://feelinglone.com/test/file_upload.php";
        utl.l(url);
        File f = new File(path);
        utl.l(path);
       if (f.length() < 2) {
            utl.snack(act, "Empty File !");
            return;
        }

        AndroidNetworking.upload(url).addMultipartFile("file", f).build().setUploadProgressListener(new UploadProgressListener() {
            @Override
            public void onProgress(long bytesUploaded, long totalBytes) {


                if(totalBytes==0)
                {
                    totalBytes=1;
                }
                double dd=(double)bytesUploaded/totalBytes;
                dd=dd*100.0;

                 utl.l("Progress : "+dd+"\nSo Far   : " +bytesUploaded+"\nTotal   : " +totalBytes);

                 callBack.progress((int)dd);

             }
        }).getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    callBack.progress(100);
                    utl.snack(act, response.getString("message"));
                    utl.l(response.toString());
                    callBack.done(response.getString("link"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(ANError ANError) {
    utl.showDig(false,ctx);
                utl.snack(act,"Error Occured while Uploading file !");
                utl.l(ANError.getErrorDetail()+"\n"+ANError.getErrorBody());
            }
        });


    }


    public int SELECT_VIDEO = 1021;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                Uri uri = (data.getData());

                path=utl.getRealPathFromUri(uri);
                File file=new File(path);

                try {
                    filename=(file.getName());
                    attach.setText("ATTACHED !");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                catch (Error e)
                {
                    e.printStackTrace();
                }
                //  send();



            }

        }


    }

    String filename;
}
