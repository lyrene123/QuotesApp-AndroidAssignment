package cs.dawson.myapplication;

import android.app.Activity;
import android.os.Bundle;

/**
 * The AboutActivity class defines an Activity that will display information about the
 * application and its authors.
 *
 * @author Peter Bellefleur
 * @author Lyrene Labor
 */

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
    }
}
