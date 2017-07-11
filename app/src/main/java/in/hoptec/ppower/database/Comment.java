package in.hoptec.ppower.database;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shivesh on 14/6/17.
 */

public class Comment {


    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_image")
    @Expose
    public String userImage;
    @SerializedName("comment")
    @Expose
    public String comment;
    @SerializedName("reply")
    @Expose
    public String reply;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("vid")
    @Expose
    public String vid;


    public String getCreatedAt()
    {


        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = dateFormat.parse(date);

            String monthname=(String)android.text.format.DateFormat.format("MMMM", date1);


            return ""+date1.getDate()+" "+monthname;

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return date;
    }



}
