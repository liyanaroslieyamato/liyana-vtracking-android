package yamato.vtracking.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Gison on 9/4/16.
 */
public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private static SessionManager _instance;
    // Shared preferences file name
    private static final String PREF_NAME = "VTRACKING";

    private static final String HANDSET_USER_ID = "handset_user_id";

    private Context _context;

    private static final String WORK_ID = "work_id";

    private static final String NAME_ID = "name";

    private SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static SessionManager instance(Context context) {
        if(_instance == null) {
            _instance = new SessionManager(context);
        }
        return _instance;
    }


    public void setLogin(String handset_user_id, String work_id, String name) {

        editor.putString(HANDSET_USER_ID, handset_user_id);
        editor.putString(WORK_ID, work_id);
        editor.putString(NAME_ID, name);
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public String getHandSetUserId(){
        return pref.getString(HANDSET_USER_ID, null);
    }

    public String getWorkId(){
        return pref.getString(WORK_ID, null);
    }
    
    public String getUserName(){
        return pref.getString(NAME_ID, null);
    }
}
