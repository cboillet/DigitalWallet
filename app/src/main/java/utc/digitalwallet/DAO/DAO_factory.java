package utc.digitalwallet.DAO;

import android.content.Context;

/**
 * Created by Louis on 30/05/2016.
 */
public class DAO_factory {

    public static DAO_test create_DAO_test(Context context){
        return new DAO_test(context);
    }

    public static DAO_getUser create_DAO_getUser(Context context){
        return new DAO_getUser(context);
    }
}
