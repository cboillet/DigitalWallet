package utc.digitalwallet.Beans;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Telephony;

@SuppressLint("ParcelCreator")
public class Participant implements Parcelable {
    private User user;
    private int rights;
    //amount of money paid
    private float hasPaid;

    public Participant(User potUser) {
        user = potUser;
    }

    public User getUser() {
        return user;
    }
    public int getRights() {
        return rights;
    }
    public float getHasPaid() {
        return hasPaid;
    }

    public void setUser(User user){
        this.user=user;
    }
    public void setRights(int rights){
        this.rights=rights;
    }
    public void setHasPaid(float hasPaid){
        this.hasPaid=hasPaid;
    }


    /***Methods and Class used for the implementation of Parcelable***/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user,flags);
        dest.writeInt(rights);
        dest.writeFloat(hasPaid);

    }

    private Participant(Parcel in){
        user = in.readParcelable(User.class.getClassLoader());
        rights = in.readInt();
        hasPaid = in.readFloat();
    }

    public static final Parcelable.Creator<Participant> CREATOR
            = new Parcelable.Creator<Participant>() {

        @Override
        public Participant createFromParcel(Parcel in) {
            return new Participant(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Participant[] newArray(int size) {
            return new Participant[size];
        }
    };
}