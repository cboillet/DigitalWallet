package utc.digitalwallet.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import utc.digitalwallet.Beans.Friends;
import utc.digitalwallet.Beans.Pot;
import utc.digitalwallet.Beans.User;

/**
 * Created by root on 01/06/16.
 */
public class AddPotsActivity extends FragmentActivity implements FriendAdapterListener {
    private static final int NUM_PAGES = 3;
    //  private ViewPager mPager;
    //  private PagerAdapter mPagerAdapter;
    private Button mButton;
    private String namePot;
    private String purposePot;
    private Float amountPot;
    private ArrayList<Friends> friends;
    private ArrayList<Friends> participants;
    private ListView listView;
    private FriendsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pot);
        String jsondata = getIntent().getExtras().getString("jsondata");

        JSONArray friendslist;
        friends = new ArrayList<Friends>();
        participants = new ArrayList<Friends>();
        try {
            friendslist = new JSONArray(jsondata);
            for (int l=0; l < friendslist.length(); l++) {
                Friends f = new Friends();
                f.setFbUid(friendslist.getJSONObject(l).getString("id"));
                f.setFbUsername(friendslist.getJSONObject(l).getString("name"));
                f.setParticipates((float) 0);
                friends.add(f);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // adapter which populate the friends in listview
        adapter = new FriendsAdapter(this, friends);
        adapter.addListener(this);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Button submit = (Button)  findViewById(R.id.ButtonCreatePot);
        View.OnClickListener createPot = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText nameField = (EditText) findViewById(R.id.PotTitleEdit);
                namePot = nameField.getText().toString();

                final EditText purposeField = (EditText) findViewById(R.id.PotPurposeEdit);
                purposePot = purposeField.getText().toString();

                final EditText amountField = (EditText) findViewById(R.id.PotAmountEdit);
                String amount = amountField.getText().toString();
                amountPot = Float.valueOf(amount);

                User user = getIntent().getExtras().getParcelable("user");
                Pot pot = new Pot();
                pot.setName(namePot);
                pot.setAmount(amountPot);
                pot.setCurrentAmount((float)0);
                pot.setPurpose(purposePot);
                pot.setPaye(false);
                pot.setEditable(true);
                pot.setDate(new Date());
                pot.setParticipants(participants);
                user.getPots().add(pot);
                Intent appel = new Intent(AddPotsActivity.this, PotsActivity.class);
                appel.putExtra("user",user);
                startActivity(appel);

            }
        };
        submit.setOnClickListener(createPot);

    }

    @Override
    public void onClickFriend(Friends item, int position) {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add participant");
        builder.setMessage("Would you like to add : " + item.getFbUsername());
        builder.setPositiveButton("Yes", null);
        builder.setNegativeButton("No", null);
        builder.show();
        */
        View wantedView = listView.getAdapter().getView(position, null, null);
        ImageView imageView = (ImageView) wantedView.findViewById(R.id.check);
        TextView textView = (TextView) wantedView.findViewById(R.id.friendName);
        System.out.println("friends name " + textView.getText() );
        CharSequence text = "";


            if(item.getParticipates()==0){
                item.setParticipates((float)1);
                participants.add(item);
                text=item.getFbUsername() + " added";
            }
            else{
                item.setParticipates((float)0);
                Iterator<Friends> i = participants.iterator();
                while (i.hasNext()) {
                    Friends f = i.next();
                    //we delete the friend from the list of participants
                    if (f == item) {
                        i.remove();
                        System.out.println("deleting friends " + item.getFbUsername());
                    }
                }
                text=item.getFbUsername() + "removed";
            }
            /*
            Iterator<Friends> i = friends.iterator();
            while (i.hasNext()) {
                Friends f = i.next();
                //we delete the friend from the list of participants
                if(f==item){
                    i.remove();
                    System.out.println("deleting friends " + item.getFbUsername() );
                }*/
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        adapter.notifyDataSetChanged();

    }

    public class FriendsAdapter extends BaseAdapter {
        private ArrayList<Friends> friends;
        private ArrayList<FriendAdapterListener> mListListener = new ArrayList<FriendAdapterListener>();
        private LayoutInflater mInflater;

        public FriendsAdapter(AddPotsActivity context,ArrayList<Friends> f) {;
            friends = f;
            mInflater = LayoutInflater.from(context);
        }

        public void addListener(FriendAdapterListener aListener) {
            mListListener.add(aListener);
        }

        private void sendListener(Friends item, int position) {
            for(int i = mListListener.size()-1; i >= 0; i--) {
                mListListener.get(i).onClickFriend(item, position);
            }
        }

        @Override
        public int getCount() {
            return friends.size();
        }

        @Override
        public Object getItem(int position) {
            return friends.get(position);
        }

        public Object getItemUid(int position) {
            return friends.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layoutItem;
            if (convertView == null) {
                layoutItem = (LinearLayout) mInflater.inflate(R.layout.friends_listview, parent, false);
            } else {
                layoutItem = (LinearLayout) convertView;
            }

            final TextView friendName = (TextView)layoutItem.findViewById(R.id.friendName);
            ProfilePictureView profilePictureView = (ProfilePictureView) layoutItem.findViewById(R.id.profile_picture);
            ImageView imageView = (ImageView) layoutItem.findViewById(R.id.check);

            //(3) : Renseignement des valeurs
            friendName.setText(friends.get(position).getFbUsername());
            profilePictureView.setProfileId(friends.get(position).getFbUid());

            if(friends.get(position).getParticipates() == 0){
                imageView.setVisibility(View.INVISIBLE);
            }


            if(friends.get(position).getParticipates() == 1){
                imageView.setVisibility(View.VISIBLE);
            }

            friendName.setTag(position);
            friendName.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //Lorsque l'on clique sur le nom, on récupère la position de la "Personne"
                    Integer position = (Integer)v.getTag();

                    //On prévient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                    sendListener(friends.get(position), position);

                }

            });

            return layoutItem;
        }
    }

}
