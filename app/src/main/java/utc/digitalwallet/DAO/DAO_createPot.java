package utc.digitalwallet.DAO;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utc.digitalwallet.Beans.Pot;

/**
 * Created by root on 09/06/16.
 */
public class DAO_createPot extends background_DAO {
    Pot _pot;

    public DAO_createPot(Context context) {
        super(context, "http://172.26.2.38/~digitalwallet/getUser.php");
    }

    public Pot getPot() {
        return _pot;
    }

    @Override
    protected String treatData(JSONArray jArray) {
        return null;
    }
}
