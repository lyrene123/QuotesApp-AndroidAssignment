package cs.dawson.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.Math;

/**
 * The MenuActivity class defines an options menu that will appear in all other Activities.
 * MenuActivity provides several features that an application user can access anywhere, regardless
 * of current Activity.
 *
 * @author Lyrene Labor
 * @author Peter Bellefleur
 */

public class MenuActivity extends AppCompatActivity {
    private final String TAG = "MenuActivity";

    /**
     * Inflates the XML for the menu, to display it to the user.
     *
     * @param menu  The Menu to inflate.
     * @return  true when the menu is inflated.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //run superclass's onCreateOptionsMenu method
        super.onCreateOptionsMenu(menu);
        //menu is defined in res/menu/options.xml
        //inflate it to display to user
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);

        //setup the icon beside the app title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        Log.i(TAG, "onCreateOptionsMenu");
        return true;
    }

    /**
     * Handles selection events for all menu items.
     * Selecting the "About" item will launch the AboutActivity activity.
     * Selecting the "Random: item will launch the QuoteActivity activity and display a random
     * quote from a random category.
     * Selecting the "Last" item will launch the QuoteActivity activity and display the last quote
     * viewed by the user, including quotes viewed in the previous runtime.
     *
     * @param item  The menu item selected.
     * @return  true if the menu item has successfully completed its action.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        //handle item selection
        switch (item.getItemId()) {
            //about option: display AboutActivity
            case R.id.about:
                Log.d(TAG, "option selected: About");
                //create intent, start activity
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            //random option: display a random quote in QuoteActivity
            case R.id.random:
                Log.d(TAG, "option selected: Random");
                //create intent
                Intent randomIntent = new Intent(this, QuoteActivity.class);
                //generate two random indices to pull from database
                int categoryIndex = (int)(Math.random() * 4) + 1;
                int quoteIndex = (int)(Math.random() * 3) + 1;
                Log.d(TAG, "category index generated: " + categoryIndex);
                Log.d(TAG, "quote index generated: " + quoteIndex);
                randomIntent.putExtra("category_index", categoryIndex + "");
                randomIntent.putExtra("quote_index", quoteIndex + "");
                //we do not know what the specific category will be, so specify it was random
                randomIntent.putExtra("category_title", getResources()
                        .getString(R.string.random_cat));
                //start activity
                startActivity(randomIntent);
                return true;
            //last option: display last viewed quote in QuoteActivity
            case R.id.last:
                Log.d(TAG, "option selected: Last");
                //retrieve QUOTE_INDICES shared prefs
                SharedPreferences prefs = getSharedPreferences("QUOTE_INDICES", MODE_PRIVATE);
                //check that shared prefs contain key-value pairs we need to load quote
                if (prefs.contains("category_title") && prefs.contains("quote_index")
                        && prefs.contains("category_index")) {
                    Log.d(TAG, "user has previously viewed a quote, launching QuoteActivity");
                    //if so, create new intent, plays values in as extras, launch activity
                    Intent lastIntent = new Intent(this, QuoteActivity.class);
                    lastIntent.putExtra("category_index", prefs.getInt("category_index", -1) + "");
                    lastIntent.putExtra("quote_index", prefs.getInt("quote_index", -1) + "");
                    lastIntent.putExtra("category_title", prefs.getString("category_title", null));
                    startActivity(lastIntent);
                } else {
                    Log.d(TAG, "user has not previously viewed a quote, popping Toast");
                    //if not found, no previous data saved - user has not viewed a quote.
                    //display message to user
                    Toast.makeText(this, "You have not viewed a previous quotes yet!", Toast.LENGTH_LONG)
                            .show();
                }
                return true;

            //default: call superclass implementation
            default:
                Log.e(TAG, "an option was selected but not recognized as About, Random or Last!!");
                return super.onOptionsItemSelected(item);
        }
    }
}
