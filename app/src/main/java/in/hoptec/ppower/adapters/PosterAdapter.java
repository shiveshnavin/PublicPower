package in.hoptec.ppower.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.hoptec.ppower.R;
import in.hoptec.ppower.database.Feed;
import in.hoptec.ppower.utl;

import static java.security.AccessController.getContext;


public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.CustomViewHolder> {
    public List<Feed> feedItemList;
    private Context ctx;

    public PosterAdapter(Context context, List<Feed> feedItemList) {
        this.feedItemList = feedItemList;
        this.ctx = context;
        AndroidNetworking.initialize(ctx);
       }

    public Integer colors[] ={R.color.green,R.color.yellow,R.color.blue,R.color.red,R.color.orange};
    public static int width,height;
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_row,  null, false);
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
      final Feed cat=feedItemList.get(i);
        final int id=i;
        final Qant qn=new Qant();


        if(count>=4)
            count=0;

        Double dp=new Double(i);
        dp=dp%4;

        final int col=colors[dp.intValue()];

        cv.name.setText( (cat.title));
        cv.sub.setText( (cat.description));
        cv.time.setText(cat.getCreatedAt());

        try {
            Picasso.with(ctx).load(cat.artworkUrl).placeholder(R.drawable.videos_1).into(cv.pic);
            utl.l(cat.artworkUrl);

            cv.line.setBackgroundColor(ctx.getResources().getColor(col));
            count++;


            utl.changeColorDrawable(cv.opt,R.color.grey_600);

            cv.opt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(ctx, cv.opt);
                     //inflating menu from xml resource
                    popup.inflate(R.menu.opt);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.view:
                                    //handle menu1 click
                                    click(cat,id);
                                    break;
                                case R.id.like:
                                    //handle menu2 click
                                    break;
                                case R.id.share:
                                    //handle menu3 click
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                   // popup.show();


                    MenuPopupHelper menuHelper = new MenuPopupHelper(ctx, (MenuBuilder) popup.getMenu(), cv.opt);
                    menuHelper.setForceShowIcon(true);

                    menuHelper.show();



                }
            });


            AndroidNetworking.get(cat.artworkUrl).build().getAsBitmap(new BitmapRequestListener() {
                @Override
                public void onResponse(Bitmap response) {


                    utl.l("BTM : "+response.getByteCount());
                    try{

                     //   cv.line.setBackgroundColor(utl.getDominantColor1(response));

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(ANError ANError) {

                    utl.l(ANError.getErrorDetail()
                    );
                }
            });
        } catch (Exception e) {


            e.printStackTrace();
        }


/* try  {
          //  customViewHolder. wb.loadData(cat.excerpt.rendered, "text/html", "UTF-8");
             utl.l("Post "+cat.title.rendered);


           //  Html.fromHtml(cat.title.rendered);
             cv.name.setText( Html.fromHtml(cat.title.rendered));
             cv.sub.setText( Html.fromHtml(cat.excerpt.rendered));
             try {
                 AndroidNetworking.get(cat.links.wpAttachment.get(0).href).build()
                         .getAsString(new StringRequestListener() {
                             @Override
                             public void onResponse(String response) {

                                 try {
                                     JSONArray jo=new JSONArray(response);
                                     JSONObject j=jo.getJSONObject(0);
                                     String  url=j.getJSONObject("guid").getString("rendered").replace("\\/","/");
                                     utl.l("url "+url);
                                     try {
                                         Picasso.with(ctx).load(url).placeholder(R.drawable.background_poly).into(cv.pic);
                                     } catch (Exception e) {

                                         Picasso.with(ctx).load(url).into(cv.pic);

                                         e.printStackTrace();
                                     }

                                     cat.slug=url;
                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 }
                             }

                             @Override
                             public void onError(ANError ANError) {
                                 utl.l(""+ANError.getErrorBody());
                             }
                         });
             } catch (Exception e) {
                 e.printStackTrace();
             }


         }
         catch (Error e)
         {
          }
         catch (Exception e) {
            e.printStackTrace();
        }
*/
        cv.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(cat,id);
            }
        });


    }

    int qan;
    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public void add(Feed cat,int id)
    {

    }


    public void intr(Feed cat,int id)
    {

    }


    public void checkin(Feed cat,int id)
    {

    }

    public void disco(Feed cat,int id)
    {

    }

    public void click(Feed cat,int id)
    {

    }


public class CustomViewHolder extends RecyclerView.ViewHolder
{
    public View view;

    public ImageView pic,opt;
    public TextView name,sub,time;
    public WebView wb;
    public View line;


    public CustomViewHolder(View itemView) {
        super(itemView);
        view=itemView.findViewById(R.id.main_target);
        line=itemView.findViewById(R.id.line);

       // wb=(WebView) itemView.findViewById(R.id.web);
        pic=(ImageView) itemView.findViewById(R.id.thumbnail);
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
