package utc.digitalwallet.Beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 12/06/16.
 */
public class Friends implements Parcelable {
    private String fbUid;
    private String fbUsername;
    private Float hasPaid;
    private Float participates;

    public Friends(){
        hasPaid=(float)0;
    }

    public String getFbUid() {
        return fbUid;
    }

    public void setFbUid(String fbUid) {
        this.fbUid = fbUid;
    }

    public String getFbUsername() {
        return fbUsername;
    }

    public void setFbUsername(String fbUsername) {
        this.fbUsername = fbUsername;
    }

    public Float getHasPaid() {
        return hasPaid;
    }

    public Float getParticipates() { return participates; }

    public void setParticipates(Float participates) { this.participates = participates; }

    public void setHasPaid(Float hasPaid) {
        this.hasPaid = hasPaid;
    }

    /***Methods and Class used for the implementation of Parcelable***/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fbUid);
        dest.writeString(fbUsername);
        dest.writeFloat(hasPaid);
        dest.writeFloat(participates);

    }

    private Friends(Parcel in){
        fbUid = in.readString();
        fbUsername = in.readString();
        hasPaid = in.readFloat();
        participates = in.readFloat();
    }

    public static final Parcelable.Creator<Friends> CREATOR
            = new Parcelable.Creator<Friends>() {

        @Override
        public Friends createFromParcel(Parcel in) {
            return new Friends(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Friends[] newArray(int size) {
            return new Friends[size];
        }
    };
}
