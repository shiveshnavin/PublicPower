package in.hoptec.ppower.database;

import android.net.Uri;
import android.os.SystemClock;
import android.webkit.URLUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.hoptec.ppower.Constants;
import in.hoptec.ppower.utl;

/**
 * Created by shivesh on 14/6/17.
 */

public class Feed {


    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("duration")
    @Expose
    public String duration;
    @SerializedName("user")
    @Expose
    public String user;
    @SerializedName("user_image")
    @Expose
    public String userImage;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("likes")
    @Expose
    public String likes;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("artwork_url")
    @Expose
    public String artworkUrl;
    @SerializedName("stream_url")
    @Expose
    public String streamUrl;
    @SerializedName("tag_list")
    @Expose
    public String tagList;

    public boolean isLiked(String userId)
    {
        if(tagList==null)
        {
            return false;
        }
        return tagList.contains(""+userId);
    }
    public String getCreatedAt()
    {


        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = dateFormat.parse(createdAt);

            String monthname=(String)android.text.format.DateFormat.format("MMMM", date1);


            return ""+date1.getDate()+" "+monthname;

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return createdAt;
    }

    public String getTitle()
    {
        if(title.length()>20)
        {
            return title.substring(0,20)+"...";

        }
        else
            return title;
    }


    public String getDescription()
    {
        if(description.length()>70)
        {
            return description.substring(0,70)+"...";

        }
        else
            return description;
    }


    public String getFileName()
    {
        String file="test_"+ SystemClock.uptimeMillis()+".mp4";

        if(streamUrl.contains("?"))
        {
        Uri uri= Uri.parse(streamUrl);
            file= uri.getQueryParameter("file");
        }
        else {
            file= URLUtil.guessFileName(streamUrl, null, null);
        }

        if(file==null)
        {
            file=utl.refineString(user+"_"+title,"_")+".mp4";
        }

        utl.l("Name  : "+title+"\nFile : "+file+"\nURL : "+streamUrl);

        return file;
    }


    public int getLikes()
    {

        return Integer.parseInt(likes);
    }

    public String getLocalFile()
    {
        final String path= Constants.getDwdFolder()+"/"+getFileName().hashCode();

        return  path;
    }



}
