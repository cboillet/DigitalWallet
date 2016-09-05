package utc.digitalwallet.com;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.ShareMedia;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utc.digitalwallet.Beans.Friends;
import utc.digitalwallet.Beans.Pot;
import utc.digitalwallet.Beans.User;

/**
 * Created by Laure on 25/05/2016.
 */
public class PotsActivity extends Activity
{
    private List<Pot> pots; //liste donnée par notre DAO sur le User
    private ExpandableListAdapter myAdaptater;
    private ExpandableListView potsList;
    //private ParticipantAdapter participant_adapter;
    HashMap<Pot, List<Object>> listDataChild;

    private Pot currentPot = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pots);

        initPotList();

        potsList = (ExpandableListView) findViewById(R.id.list_pots);
        myAdaptater = new ExpandableListAdapter(this, pots, listDataChild);
        potsList.setAdapter(myAdaptater);
    }

    protected void initPotList()
    {
        User user = getIntent().getExtras().getParcelable("user");
        pots = user.getPots();
        listDataChild = new HashMap<Pot, List<Object>>();
        int i=0;
        for (Pot pot : pots) {
            List<Object> dataPot = new ArrayList<Object>();
            dataPot.add(pot);
            listDataChild.put(pots.get(i), dataPot);
            i++;
        }
    }

    public boolean addPot(View v)
    {
        User user = getIntent().getExtras().getParcelable("user");
        String jsondata = getIntent().getExtras().getString("jsondata");
        Intent appel = new Intent(PotsActivity.this, AddPotsActivity.class);
        appel.putExtra("user",user);
        appel.putExtra("jsondata",jsondata);
        startActivity(appel);
        return true;
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter
    {
        private Context ctx;
        private List<Pot> list_header;                       // header titles
        private HashMap<Pot, List<Object>> list_child;       // child data in format of header title, child title
        //private ScrollView participant_ScrollView;

        public ExpandableListAdapter(Context context, List<Pot> listDataHeader, HashMap<Pot, List<Object>> listChildData)
        {
            this.ctx = context;
            this.list_header = listDataHeader;
            this.list_child = listChildData;


            //this.participant_ScrollView = (ScrollView) findViewById(R.id.participant_scrollView);
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon)
        {
            return this.list_child.get(this.list_header.get(groupPosition)).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition)
        {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
        {
            final Pot childPot = (Pot) getChild(groupPosition, childPosition);

            currentPot = childPot;

            if (convertView == null)
            {
                LayoutInflater infalInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.item_pot_generic, null);
            }

            TextView pot_description = (TextView) convertView.findViewById(R.id.pot_description);
            TextView current_amount_pot = (TextView) convertView.findViewById(R.id.current_amount_pot);
            //View friends = (View) convertView.findViewById(R.id.scrollView);
            ImageButton deadline = (ImageButton) convertView.findViewById(R.id.deadline_button);

            pot_description.setText(childPot.getPurpose());

            /** Gestion de la progressBar */

            final ProgressBar pb = (ProgressBar) convertView.findViewById(R.id.progress_bar_amount);

            pb.setMax(Math.round(childPot.getAmount()));

            final float currentAmount = childPot.getCurrentAmount();
            pb.setProgress((int) currentAmount);

            int tmp = Math.round((currentAmount * 100 ) / childPot.getAmount());

            current_amount_pot.setText("" + tmp + "%");

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition)
        {
            return this.list_child.get(this.list_header.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition)
        {
            return this.list_header.get(groupPosition);
        }

        @Override
        public int getGroupCount()
        {
            return this.list_header.size();
        }

        @Override
        public long getGroupId(int groupPosition)
        {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
        {
            Pot pot = (Pot) getGroup(groupPosition);

            if (convertView == null)
            {
                LayoutInflater infalInflater = (LayoutInflater) this.ctx .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.pot_list_headers, null);
            }

            TextView Name = (TextView) convertView.findViewById(R.id.potName);
            TextView currentAmount = (TextView) convertView.findViewById(R.id.potCurrentAmount);

            Name.setText(pot.getName());
            currentAmount.setText(String.valueOf(pot.getAmount()) + " €");

            return convertView;
        }

        @Override
        public boolean hasStableIds()
        {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return true;
        }
    }


    public boolean toSend(View v)
    {
        EditText amount_sent = (EditText) findViewById(R.id.amount_sent);
        float cmpt = Float.parseFloat(amount_sent.getText().toString());
        Log.e("amount_sent", "" + cmpt);

        int index = pots.indexOf(currentPot);
        Log.e("index du pot", "" + index);

        if (amount_sent.getText().toString().equals(""))       //TODO: gérer le cas ou rien n'est ecrit
        {
            Log.i("Montant null", "true !");
            Toast.makeText(this, "Vous devez inscrire un montant", Toast.LENGTH_LONG).show();
            return false;
        }

        if (cmpt > currentPot.getAmount())
        {
            Log.e("Erreur", "Montant incorrect");
            Toast.makeText(this, "Problème de montant", Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            float newAmount = currentPot.getCurrentAmount() + cmpt;
            Log.e("new", "" + newAmount);

            currentPot.setCurrentAmount(newAmount);

            User user = getIntent().getExtras().getParcelable("user");

            pots.set(index, currentPot);

            user.setPots(pots);

            final ProgressBar pb = (ProgressBar) findViewById(R.id.progress_bar_amount);

            pb.setMax(Math.round(currentPot.getAmount()));

            final float currentAmount = currentPot.getCurrentAmount();
            pb.setProgress((int) currentAmount);

            int tmp = Math.round((currentAmount * 100 ) / currentPot.getAmount());

            TextView current_amount_pot = (TextView) findViewById(R.id.current_amount_pot);
            current_amount_pot.setText("" + tmp + "%");

            Toast.makeText(this, "Votre montant a été ajouté au pot. Merci !", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    public boolean showParticipants(View v){
        int index = pots.indexOf(currentPot);
        Log.e("index du pot", "" + index);

        User user = getIntent().getExtras().getParcelable("user");
        Intent appel = new Intent(PotsActivity.this, PotFriendActivity.class);
        appel.putExtra("user",user);
        appel.putExtra("index",(int)index);
        startActivity(appel);

        return true;
    }

    public void helpAmountSent(View v)
    {
        Toast.makeText(this, "Veuillez entrer le montant que vous souhaitez mettre dans le pot.", Toast.LENGTH_LONG).show();
    }


}