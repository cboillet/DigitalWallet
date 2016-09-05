package utc.digitalwallet.DAO;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Loukis95 on 29/05/2016.
 */
public class DAO_test extends background_DAO {

    public DAO_test(Context context) {
        super(context, "http://172.26.2.38/~digitalwallet/test.php");
    }

    @Override
    protected String treatData(JSONArray jArray) {

        String returnString = "";

        try{
            for(int i=0;i<jArray.length();i++){
                JSONObject json_data = jArray.getJSONObject(i);
                // Affichage ID_ville et Nom_ville dans le LogCat
                Log.i("log_tag","col1: "+json_data.getInt("col1")+
                        ", col2: "+json_data.getString("col2")+
                        ", col3: "+json_data.getString("col3")+
                        ", col4: "+json_data.getString("col4")
                );
                // Résultats de la requête
                returnString += "\n\t" + jArray.getJSONObject(i);
            }
        }catch(JSONException e){
            Log.e("log_tag", "Error treating data " + e.toString());
            return new String("Exception: " + e.getMessage());
        }

        return returnString;
    }
}
