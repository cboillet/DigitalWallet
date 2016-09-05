package utc.digitalwallet.DAO;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utc.digitalwallet.Beans.User;

/**
 * Created by Loukis95 on 01/06/2016.
 */
public class DAO_getUser extends background_DAO {

    User _user;

    public DAO_getUser(Context context) {
        super(context, "http://172.26.2.38/~digitalwallet/getUser.php");
    }

    public User getUser() {
        return _user;
    }

    @Override
    protected String treatData(JSONArray jArray) {
        Log.e("log_tag", "Entrée dans treat data ");
        String returnString = "";

        try{
            int id = 0;
            String username = "";
            String email = "";

            for(int i=0;i<jArray.length();i++){
                JSONObject json_data = jArray.getJSONObject(i);
                id = json_data.getInt("id");
                username = json_data.getString("name");
                email = json_data.getString("email");

                // Résultats de la requête
                returnString += "\n\t" + jArray.getJSONObject(i);
            }
            _user = new User(username, email, (long) id);
            Log.e("log_tag", "Treating data " + _user.getUsername());
        }catch(JSONException e){
            Log.e("log_tag", "Error treating data " + e.toString());
            return new String("Exception: " + e.getMessage());
        }

        super._hasfinished = true;
        return returnString;
    }
}

