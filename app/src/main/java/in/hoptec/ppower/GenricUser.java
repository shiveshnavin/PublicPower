package in.hoptec.ppower;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shivesh on 29/6/17.
 */

public class GenricUser {



    @SerializedName("user_name")
    @Expose
    public String user_name;
    @SerializedName("user_fname")
    @Expose
    public String user_fname;
    @SerializedName("user_password")
    @Expose
    public String user_password;
    @SerializedName("suid")
    @Expose
    public String suid;
    @SerializedName("auth")
    @Expose
    public String auth;
    @SerializedName("user_email")
    @Expose
    public String user_email;
    @SerializedName("user_image")
    @Expose
    public String user_image;
    @SerializedName("user_created")
    @Expose
    public String user_created;
    @SerializedName("extra0")
    @Expose
    public String extra0;
    @SerializedName("extra1")
    @Expose
    public String extra1;
    @SerializedName("extra2")
    @Expose
    public String extra2;
    @SerializedName("uid")
    @Expose
    public String uid;
    @SerializedName("social")
    @Expose
    public String social;




    public GenricUser(FirebaseUser user)
    {
        user_name=utl.refineString(""+user.getEmail(),"");
        user_fname=""+user.getDisplayName();
        user_password=""+user.getUid();
       // uid=""+user.getUid();
        suid=""+user.getUid();
        auth=""+user.getUid();
        user_email=""+user.getEmail();
        extra1=""+user.getPhoneNumber();
        try {
            user_image=""+user.getPhotoUrl().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public GenricUser()
    {

    }


}
