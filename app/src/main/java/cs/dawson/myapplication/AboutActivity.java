package cs.dawson.myapplication;

import android.os.Bundle;
import android.util.Log;

/**
 * The AboutActivity class defines an Activity that will display information about the
 * application and its authors.
 *
 * @author Lyrene Labor
 * @author Peter Bellefleur
 */

public class AboutActivity extends MenuActivity {
    private final String TAG = "AboutActivity";

    /**
     * Displays the layout defined in XML layout resources.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Log.i(TAG, "onCreate");
    }
}
