package utc.digitalwallet.Beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Telephony;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Pot implements Parcelable {
    private Float amount;
    private Float currentAmount;
    private String name;
    private String purpose;
    //private List<Participant> participants;
    private Boolean paye;
    private Boolean editable;
    private Date deadlinePay;
    private List<Friends> participants;

    public Pot()
    {
        participants=new ArrayList<Friends>();

        for (Friends x :
                participants) {
            currentAmount += x.getHasPaid();
        }
    }

    public Float getAmount(){
        return amount;
    }
    public List<Friends> getParticipants(){
        return participants;
    }
    public Boolean getPaye(){
        return paye;
    }
    public Boolean getEditable(){
        return editable;
    }
    public String getName() {
        return name;
    }
    public String getPurpose() {
        return purpose;
    }
    public Date getDeadlinePay() {
        return deadlinePay;
    }
    public Float getCurrentAmount() {
        return currentAmount;
    }
    public void setCurrentAmount(float a)
    {
        this.currentAmount = a;
    }


    public void setAmount(Float amount){
        this.amount = amount;
    }
    public void setParticipants(List<Friends> participants){
        this.participants = new ArrayList<Friends>(participants.size());
        for(Friends p: participants) this.participants.add(p);
    }
    public void setPaye(Boolean paye){
        this.paye = paye;
    }
    public void setEditable(Boolean editable){
        this.editable = editable;
    }
    public void setDate(Date deadlinePay){
        this.deadlinePay = deadlinePay;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }



    /***Participants management***/
    /*public void addParticipant(User userToAdd){
        Participant newParticipant=new Participant(userToAdd);
        participants.add(newParticipant);
    }*/
    public void removeParticipant(Participant userToRemove){
        participants.remove(userToRemove);
    }

    public void addRights(Participant userToEdit, List<Rights> rightsToAdd ){
        Iterator riter= rightsToAdd.iterator();
        while (riter.hasNext()){
            Rights right = (Rights) riter.next();
            userToEdit.setRights(userToEdit.getRights() | right.getNumVal());
            // /userToEdit.rights.add(rigth);
        }

    }
    public void removeRights(Participant userToEdit, List<Rights> rightsToRemove){
        Iterator riter= rightsToRemove.iterator();
        while (riter.hasNext()){
            Rights right = (Rights) riter.next();
            userToEdit.setRights(userToEdit.getRights() &~ right.getNumVal());
            //userToEdit.rights.remove(rigth);
        }
    }

    /***method putPotMoney()
     * Input: user adding money, amount of money to send
     * ***/
   /* public void putPotMoney(User user, float amount){
        Participant participant = findByUser(user);
        participant.setHasPaid(participant.getHasPaid()+amount);
    }*/

    /***method useMoney()
     * Input: user doing the request, amount of money to spen
     * ***/
    public void useMoney(){}

    /***method redistributeMoney()
     * goal: redistribute the Money between all the paricipants
     * Input: user doing the request
     * ***/
    public void redistributeMoney(){
    }


    /***Utility***/
   /* public Participant findByUser(User user){
        Participant participant = null;
        for(Participant p : participants) {
            if (p.getUser().equals(user)) {
                 participant = p;
            }
        }
        return participant;
    }*/


    /***Methods and Class used for the implementation of Parcelable***/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(amount);
        dest.writeFloat(currentAmount);
        dest.writeString(name);
        dest.writeString(purpose);
        dest.writeByte((byte) (paye ? 1 : 0));
        dest.writeByte((byte) (editable ? 1 : 0));
        dest.writeLong(deadlinePay.getTime());
        dest.writeTypedList(participants);
    }

    private Pot(Parcel in){
        amount=in.readFloat();
        currentAmount=in.readFloat();
        name=in.readString();
        purpose=in.readString();
        paye=in.readByte()!=0;
        editable=in.readByte()!=0;
        deadlinePay=new Date(in.readLong());
        participants=new ArrayList<Friends>();
        in.readTypedList(participants, Friends.CREATOR);
    }

    public static final Parcelable.Creator<Pot> CREATOR
            = new Parcelable.Creator<Pot>(){

        @Override
        public Pot createFromParcel(Parcel in){
            return new Pot(in);
        }

        @Override
        public Pot[] newArray(int size){
            return new Pot[size];
        }
    };
}