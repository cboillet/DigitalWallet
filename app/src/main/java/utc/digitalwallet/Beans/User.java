package utc.digitalwallet.Beans;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {
    private Long id;
    private String fbId;
    private String username;
    private String email;
    private String picture;
    private List<Pot> pots;

    public Long getId() {
        return id;
    }
    public String getFbId() { return fbId; }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPicture() {return picture; }
    public List<Pot> getPots() {
        return pots;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setFbId(String id) { this.fbId = id; }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPicture(String picture){this.picture = picture;}
    public void setPots(List<Pot> pots) {
        this.pots = pots;
    }

    public User(){

    }

    public User(String name, String eml, String pic) {
        id = null;
        username = name;
        email = eml;
        picture = pic;
        pots = new ArrayList<Pot>();
    }

    public User(String name, String eml, Long _id) {
        id = _id;
        username = name;
        email = eml;

        pots = new ArrayList<Pot>();
    }

    public void createPot(){
        Pot pot = new Pot();
        Participant owner = new Participant(this);
        //add all the rights to the pot owner
        for (Rights r : Rights.values()) {
            owner.setRights(owner.getRights() | r.getNumVal());
        }
        pots.add(pot);
    }

    /***Methods and Class used for the implementation of Parcelable***/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(fbId);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(picture);
        dest.writeTypedList(pots);
    }

    private User(Parcel in){
        id=in.readLong();
        fbId=in.readString();
        username=in.readString();
        email=in.readString();
        picture=in.readString();
        pots=new ArrayList<Pot>();
        in.readTypedList(pots, Pot.CREATOR);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>(){

        @Override
        public User createFromParcel(Parcel in){
            return new User(in);
        }

        @Override
        public User[] newArray(int size){
            return new User[size];
        }
    };


}

