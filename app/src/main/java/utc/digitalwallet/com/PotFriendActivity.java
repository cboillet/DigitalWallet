package utc.digitalwallet.com;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import java.util.ArrayList;
import java.util.List;

import utc.digitalwallet.Beans.Friends;
import utc.digitalwallet.Beans.Participant;
import utc.digitalwallet.Beans.Pot;
import utc.digitalwallet.Beans.User;

/**
 * Created by Louis on 13/06/2016.
 */
public class PotFriendActivity extends Activity
{
    private Pot pot = null;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_list);
        User user = getIntent().getExtras().getParcelable("user");
        int i =  getIntent().getExtras().getInt("index");
        ArrayList<Friends> friends = new ArrayList<Friends>(user.getPots().get(i).getParticipants());

        System.out.println("nombre d'amis  " +friends.size());

        listview=(ListView) findViewById(R.id.listViewParticipant);
        ParticipantAdapter myAdaptater = new ParticipantAdapter(this, friends);
        listview.setAdapter(myAdaptater);



    }

    public class ParticipantAdapter extends BaseAdapter {
        private ArrayList<Friends> friends;
        private ArrayList<FriendAdapterListener> mListListener = new ArrayList<FriendAdapterListener>();
        private LayoutInflater mInflater;

        public ParticipantAdapter(PotFriendActivity context,ArrayList<Friends> f) {;
            friends = f;
            mInflater = LayoutInflater.from(context);
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
            System.out.println("nombre d'amis  " +friends.size());
            LinearLayout layoutItem;
            if (convertView == null) {
                layoutItem = (LinearLayout) mInflater.inflate(R.layout.participant_listview, parent, false);
            } else {
                layoutItem = (LinearLayout) convertView;
            }

            final TextView friendName = (TextView)layoutItem.findViewById(R.id.nameParticipant);

            final TextView friendAmount = (TextView)layoutItem.findViewById(R.id.amountParticipant);
            ProfilePictureView profilePictureView = (ProfilePictureView) layoutItem.findViewById(R.id.fb_participant);

            //(3) : Renseignement des valeurs
            friendName.setText(friends.get(position).getFbUsername());
            System.out.println("amis " +friends.get(position).getFbUsername());
            profilePictureView.setProfileId(friends.get(position).getFbUid());

            if(friends.get(position).getHasPaid() != 0){
                friendAmount.setVisibility(View.VISIBLE);
                friendAmount.setText(String.valueOf(friends.get(position).getHasPaid())+" €");
            }

            friendName.setTag(position);

            return layoutItem;
        }
    }
}
