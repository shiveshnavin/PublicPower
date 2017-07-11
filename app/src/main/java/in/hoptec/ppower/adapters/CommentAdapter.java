package in.hoptec.ppower.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import in.hoptec.ppower.LatestVideos;
import in.hoptec.ppower.R;
import in.hoptec.ppower.database.Comment;
import in.hoptec.ppower.utl;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CustomViewHolder> {
    public List<Comment> feedItemList;
    private Context ctx;

    public CommentAdapter(Context context, List<Comment> feedItemList, CallBacks cab) {
        this.feedItemList = feedItemList;
        this.ctx = context;
        this.cab=cab;
        AndroidNetworking.initialize(ctx);
       }

    public Integer colors[] ={R.color.green,R.color.yellow,R.color.blue,R.color.red,R.color.orange};
    public static int width,height;
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_row,  null, false);
      //  view.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.WRAP_CONTENT));

        WindowManager windowManager = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        height = windowManager.getDefaultDisplay().getHeight();
        width = windowManager.getDefaultDisplay().getWidth();


        Double w,h;
        w=width/1.0;
        w=w-w*0.04;
        h=height/2.4;
        CardView.LayoutParams par=new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if(utl.dpFromPx(ctx,h.floatValue())<200);
      //  par=new LinearLayout.LayoutParams(w.intValue(), utl.pxFromDp(ctx,170F).intValue());


        par.bottomMargin=4;
        view.setLayoutParams(par);

//        YoYo.with(Techniques.SlideInLeft).duration(500).playOn( view);
        return viewHolder;
    }

    public static class Qant{
        public int quan=1;
    }


    int count=0;
    @Override
    public void onBindViewHolder(final CustomViewHolder cv, final int i) {
               //Setting text view title
      final Comment cat=feedItemList.get(i);
        final int id=i;
        final Qant qn=new Qant();


        if(count>=4)
            count=0;

        Double dp=new Double(i);
        dp=dp%4;

        final int col=colors[dp.intValue()];

        cv.name.setText( (cat.userName));
        cv.sub.setText( (cat.comment));
        cv.time.setText(cat.getCreatedAt());

        try {
            Picasso.with(ctx).load(cat.userImage).placeholder(R.drawable.videos_1).into(cv.opt);
            utl.l(cat.userImage);

            cv.line.setBackgroundColor(ctx.getResources().getColor(col));
            count++;


           // utl.changeColorDrawable(cv.opt,R.color.grey_600);


        } catch (Exception e) {


            e.printStackTrace();
        }


        cv.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cab.click(cat,id,cv.view);
            }
        });
        cv.line.setVisibility(View.INVISIBLE);


    }

    public boolean isLiked(String id)
    {
        return LatestVideos.likes.contains(":"+ id);
    }
    int qan;
    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    CallBacks cab;
    public static interface CallBacks{

    public void share(Comment cat, int id);

    public void like(Comment cat, boolean like);

    public void click(Comment cat, int id, View v);
    }

public class CustomViewHolder extends RecyclerView.ViewHolder
{
    public View view;

    public ImageView pic,opt,like,play;
    public TextView name,sub,time;
    public WebView wb;
    public View line;


    public CustomViewHolder(View itemView) {
        super(itemView);
        view=itemView.findViewById(R.id.main_target);
        line=itemView.findViewById(R.id.line);

       // wb=(WebView) itemView.findViewById(R.id.web);
        pic=(ImageView) itemView.findViewById(R.id.thumbnail);
        play=(ImageView) itemView.findViewById(R.id.play);
        like=(ImageView) itemView.findViewById(R.id.love);
        opt=(ImageView) itemView.findViewById(R.id.opt);
        name=(TextView) itemView.findViewById(R.id.text);
        sub=(TextView) itemView.findViewById(R.id.text2);
        time=(TextView) itemView.findViewById(R.id.time);


    }
}


public class Dummy
{

}






}
