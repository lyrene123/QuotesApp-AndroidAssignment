package cs.dawson.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import cs.dawson.myapplication.util.DBHelperUtil;

/**
 * Displays to the user a list of short quotes of a particular category
 * that are retrieved from the firebase. Uses the DbHelper class for firebase authentication
 * in order to access the database. The DbHelper class will then be able to
 * populate the list view of the main activity. Items are loaded with the help of
 * the custom adapter and a custom layout used for each list item.
 * A click event listeners is set up by the DBHelper for each list item that will launch
 * an intent to display the QuoteListActivity and display the list of quote.
 *
 * @author Lyrene Labor
 * @author Peter Bellefleur
 */
public class QuoteListActivity extends MenuActivity {

    //the category that was selected
    private int categoryID;
    private String categoryTitle;

    //for DAO access methods
    private DBHelperUtil dbHelper;

    /**
     * Sets the layout of the view. Retrieves the necessary information from the bundle
     * such as the category name and the category id which will be used to retrieve the
     * list of short quotes of a category from the database with the help of the DBHelperUtil
     * class. The data retrieved will be loaded into the ListView and displayed to the user.
     *
     * @param savedInstanceState Bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //retrieve the name of the category from the intent extras and display in TextView
        TextView categoryTitleTV = (TextView) findViewById(R.id.categoryTitleTV);
        if ( getIntent().hasExtra("category_name") != false &&
                getIntent().getExtras().getString("category_name") != null) {
            categoryTitle = getIntent().getExtras().getString("category_name");
            categoryTitleTV.setText(categoryTitle);
        }

        //retrieve the id or position of the category that was selected from the intent extras
        if ( getIntent().hasExtra("category_index") != false &&
                getIntent().getExtras().getString("category_index") != null) {
            categoryID = Integer.parseInt(getIntent().getExtras().getString("category_index")) + 1;
        }

        //retrieve the image category
        String img = "";
        if ( getIntent().hasExtra("category_img") != false &&
                getIntent().getExtras().getString("category_img") != null) {
            img = getIntent().getExtras().getString("category_img");
        }

        //instantiate the DBHelper instance
        dbHelper = new DBHelperUtil();

        //retrieve the ListView from the view to load the items into it
        ListView list = (ListView) findViewById(R.id.listViewCat);

        /*retrieve and load the list of short quotes for the selected category
        * pass the current activity, the ListView in which to load the short quotes, the
        * data String type, the category id selected, the category title selected.
        */
        dbHelper.retrieveRecordsFromDb(QuoteListActivity.this, list, "quote_short", categoryID, categoryTitle, -1);
        dbHelper.setImgName(img);
    }
}
