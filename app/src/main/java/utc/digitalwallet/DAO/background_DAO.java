package utc.digitalwallet.DAO;

/**
 * Created by Louis on 27/05/2016.
 */

        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.net.URL;
        import java.net.URLConnection;
        import java.net.URLEncoder;

        import android.content.Context;
        import android.os.AsyncTask;
        import android.util.Log;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

public abstract class background_DAO extends AsyncTask<String,Void,String>{
    private Context context;
    private String _phpLink;
    protected boolean _hasfinished;

    //flag 0 means get and 1 means post.(By default it is get.)
    public background_DAO(Context context, String phpLink) {
        this.context = context;
        this._phpLink = phpLink;
        this._hasfinished = false;
        Log.d("ONCREATE", "it works. Yeah !");
    }

    public boolean hasFinished() {
        return this._hasfinished;
    }

    @Override
    protected void onPreExecute(){
        this._hasfinished = false;
    }

    @Override
    protected String doInBackground(String... arg0) {
        String result = "";
        String returnString = "";
        try{
            String link=_phpLink;
            String data = encodesPhpData(arg0);

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result=sb.toString();
            //returnString=result;
        }catch(Exception e){
            Log.e("log_tag", "Error converting result " + e.toString());
            return new String("Exception: " + e.getMessage());
        }

        // Parse les données JSON
        try{
            JSONArray jArray = new JSONArray(result);
            returnString = treatData(jArray);
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
            return new String("Exception: " + e.getMessage());
        }

        return returnString;
    }

    @Override
    protected void onPostExecute(String result){
        //executé à la fin du thread (affichage des résultats)
        Log.d("ONPOSTEXECUTE", result);
    }

    protected String encodesPhpData(String... arg0)
    {
        String data = "";

        try{
            if (arg0.length >= 2)
            {
                data += URLEncoder.encode(arg0[0], "UTF-8") + "=" + URLEncoder.encode(arg0[1], "UTF-8");
            }
            for (int i = 2; i < arg0.length; i+=2) {
                data += "&" + URLEncoder.encode(arg0[i], "UTF-8") + "=" + URLEncoder.encode(arg0[i+1], "UTF-8");
            }
        }catch (Exception e){
            Log.e("log_tag", "Error creating php data " + e.toString());
            return new String("Exception: " + e.getMessage());
        }

        return data;
    }

    protected abstract String treatData(JSONArray jArray);
}