package utc.digitalwallet.com;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import utc.digitalwallet.Beans.Friends;
import utc.digitalwallet.Beans.Pot;
import utc.digitalwallet.Beans.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    String friends;
    ArrayList<Friends> friendsPot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User user = getIntent().getExtras().getParcelable("user");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setScrollbarFadingEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView username = (TextView) headerView.findViewById(R.id.textView);
        username.setText(user.getUsername());

        ProfilePictureView profilePictureView = (ProfilePictureView) headerView.findViewById(R.id.profile_picture);
        profilePictureView.setProfileId(user.getFbId());


        GraphRequestBatch batch = new GraphRequestBatch(
                GraphRequest.newMyFriendsRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(
                                    JSONArray jsonArray,
                                    GraphResponse response) {
                                // Application code for users friends
                                System.out.println("getFriendsData onCompleted : jsonArray " + jsonArray);
                                System.out.println("getFriendsData onCompleted : response " + response);
                                try {
                                    JSONObject jsonObject = response.getJSONObject();
                                    JSONArray data = jsonObject.getJSONArray("data");
                                    friends = data.toString();
                                    System.out.println("getFriendsData onCompleted : jsonObject " + jsonObject);
                                    JSONObject summary = jsonObject.getJSONObject("summary");
                                    System.out.println("getFriendsData onCompleted : summary total_count - " + summary.getString("total_count"));
                                    addFriendsToPot();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        })

        );
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                // Application code for when the batch finishes
            }
        });
        batch.executeAsync();

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture");



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) //Barre supérieure !!
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    //Le paramètre item permet de savoir quel boutton a été cliqué par l'utilisateur
    {
        int id = item.getItemId();
        if (id == R.id.nav_myaccount)
        {
            Intent appel = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(appel);
            return true;

        }
        else if (id == R.id.nav_myfriends)
        {

        }
        else if (id == R.id.nav_notifications)
        {

        }
        else if (id == R.id.nav_pot)
        {

            User user = getIntent().getExtras().getParcelable("user");

            List<Pot> pots=user.getPots();
            Pot pot=pots.get(0);
            pot.setParticipants(friendsPot);
            List<Pot> potsFriends=new ArrayList<>();
            potsFriends.add(pot);
            pots.get(1).setParticipants(friendsPot);
            user.setPots(pots);
            Intent appel = new Intent(MainActivity.this, PotsActivity.class);
            appel.putExtra("user",user);
            appel.putExtra("jsondata", friends);
            startActivity(appel);
            return true;
        }
        else if (id == R.id.nav_about)
        {

        }
        else if (id == R.id.nav_logout)
        {
            LoginManager.getInstance().logOut();
            Intent appel = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(appel);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addFriendsToPot() {
        JSONArray friendslist;
        friendsPot = new ArrayList<Friends>();
        try {
            friendslist = new JSONArray(friends);
            for (int l = 0; l < friendslist.length(); l++) {
                Friends f = new Friends();
                f.setFbUid(friendslist.getJSONObject(l).getString("id"));
                f.setFbUsername(friendslist.getJSONObject(l).getString("name"));
                f.setParticipates((float)1);
                f.setHasPaid((float) (l*7%3)*4);
                friendsPot.add(f);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

/* Au niveau des vues : les layout gèrent la mise en forme de la page alors que
 * les widget offrent à l'utilisateur un contenu pour qu'il puisse intéragir avec la page
 */

//Toast.makeText(this, "Coucou, ceci est un test", Toast.LENGTH_LONG).show();
