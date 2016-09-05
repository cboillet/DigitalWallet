package utc.digitalwallet.com;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

//pour les test de dao
import utc.digitalwallet.Beans.Pot;
import utc.digitalwallet.Beans.User;
import utc.digitalwallet.DAO.DAO_factory;
import utc.digitalwallet.DAO.DAO_getUser;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by root on 25/05/16.
 */
public class LoginActivity extends FragmentActivity {
    private TextView info;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Intent appel;

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*DAO_getUser dao_getuser = DAO_factory.create_DAO_getUser(this);
        try {
            dao_getuser.execute("email", "utc.projet@gmail.com").get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        User user = dao_getuser.getUser();*/
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);
        info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        List<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("user_friends");
        permissions.add("email");
        permissions.add("user_birthday");

        loginButton.setReadPermissions(permissions);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                /* info.setText(
                         "User ID: "
                                 + loginResult.getAccessToken().getUserId()
                                 + "\n" +
                                 "Auth Token: "
                                 + loginResult.getAccessToken().getToken()
                 );*/
                 /*
                 * appeler ici la dao factory pour trouver l'utilisateur dans la BDD et injecter les infos dans le profil User
                 * rappel: le management de la DAO doit se faire en background
                 * */
                Log.e(TAG, "User ID: " + loginResult.getAccessToken().getUserId());
                Profile profile = Profile.getCurrentProfile();
                Log.e(TAG, "User ID: " + loginResult.getAccessToken().getUserId());
                Log.e(TAG, "Auth Token: " + loginResult.getAccessToken().getToken());
                SharedPreferences prefs = getSharedPreferences("com.dotfreeride.dotfreeride.login", 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userId", loginResult.getAccessToken().getUserId());
                editor.putString("fbToken", loginResult.getAccessToken().getToken());
                editor.commit();
                //System.out.println("User email"+profile.getName());

                Log.e("OnGraph", "------------------------");
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                String email = object.optString("email");
                                String nom = object.optString("name");
                                String uid = object.optString("id");
                                String data = object.optString("data");
                                String profilePicUrl = object.optString("picture");
                                System.out.println("User email" + email);
                                Log.e("GraphResponse", "-------------" + response.toString());
                               // DAO_getUser dao_getuser = DAO_factory.create_DAO_getUser(getApplicationContext());
                                //dao_getuser.execute("email", email);
                                //User user = dao_getuser.getUser();
                                //System.out.println("trouvé dans la BD: "+user.getUsername());
                                User user = createUser(uid, email, nom, profilePicUrl);
                                appel = new Intent(LoginActivity.this, MainActivity.class);
                                appel.putExtra("user",user);
                                startActivity(appel);

                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,gender,birthday,email,picture");
                request.setParameters(parameters);
                request.executeAsync();

                updateWithToken(AccessToken.getCurrentAccessToken());

            }

            private void updateWithToken(AccessToken currentAccessToken) {

                if (currentAccessToken != null) {
                    Log.e(TAG, "alerady logged in");
                    //Intent appel = new Intent(LoginActivity.this, MainActivity.class);
                    //startActivity(appel);
                } else {
                }
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private User createUser(String uid, String email, String username, String picture){
        User user = new User();
        user.setFbId(uid);
        user.setEmail(email);
        user.setUsername(username);
        user.setPicture(picture);
        List<Pot> pots = new ArrayList<Pot>();
        Pot pot1 = new Pot();
        pot1.setAmount((float)30);
        pot1.setCurrentAmount((float)12);
        pot1.setName("Fete des meres");
        pot1.setPurpose("Bonne fête à notre maman !");
        pot1.setPaye(false);
        pot1.setEditable(true);
        pot1.setDate(new Date());
        Pot pot2 = new Pot();
        pot2.setAmount((float)30);
        pot2.setCurrentAmount((float)12);
        pot2.setName("Anniversaire");
        pot2.setPurpose("Offrir un beau cadeau d'anniversaire à Antoine, je sais qu'il aime l'aéronotique, un stage de vol à voile ?");
        pot2.setPaye(false);
        pot2.setEditable(true);
        pot2.setDate(new Date());
        pots.add(pot1);
        pots.add(pot2);
        user.setPots(pots);
        user.setId((long)1);
        return user;
    }


    /*@Override
    public void onDestroy(){
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }*/

}
