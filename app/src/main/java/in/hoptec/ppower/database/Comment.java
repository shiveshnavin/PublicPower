package in.hoptec.ppower.database;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shivesh on 14/6/17.
 */

public class Comment {


    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("user")
    @Expose
    public String user;
    @SerializedName("userimage")
    @Expose
    public String userimage;
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



}
