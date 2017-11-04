package cs.dawson.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
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
 * @author Peter Bellefleur
 * @author Lyrene Labor
 */

public class MenuActivity extends AppCompatActivity {

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
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
        //handle item selection
        switch (item.getItemId()) {
            //about option: display AboutActivity
            case R.id.about:
                //create intent, start activity
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            //random option: display a random quote in QuoteActivity
            case R.id.random:
                Intent randomIntent = new Intent(this, QuoteActivity.class);
                randomIntent.putExtra("category_index", ((int)(Math.random() * 4) + 1) + "");
                randomIntent.putExtra("quote_index", ((int)(Math.random() * 3)) + "");
                randomIntent.putExtra("category_title", "a random subject");
                startActivity(randomIntent);
                return true;
            //last option: display last viewed quote in QuoteActivity
            case R.id.last:
                SharedPreferences prefs = getSharedPreferences("QUOTE_INDICES", MODE_PRIVATE);
                if (prefs.contains("category_title") && prefs.contains("quote_index")
                        && prefs.contains("category_index")) {
                    Intent lastIntent = new Intent(this, QuoteActivity.class);
                    lastIntent.putExtra("category_index", prefs.getInt("category_index", -1) + "");
                    lastIntent.putExtra("quote_index", prefs.getInt("quote_index", -1) + "");
                    lastIntent.putExtra("category_title", prefs.getString("category_title", null));
                    startActivity(lastIntent);
                } else {
                    Toast.makeText(this, "You have not viewed a quote yet!", Toast.LENGTH_LONG)
                            .show();
                }
                return true;

            //default: call superclass implementation
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
