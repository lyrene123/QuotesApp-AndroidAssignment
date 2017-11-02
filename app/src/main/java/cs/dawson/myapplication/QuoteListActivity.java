package cs.dawson.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cs.dawson.myapplication.util.CustomAdapter;
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
 * @author Lyrene Labor, Peter Bellefleur
 */
public class QuoteListActivity extends Activity {

    //the category that was selected
    private int categoryID;
    private String categoryTitle;

    //for DAO access methods
    private DBHelperUtil dbHelper;

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

        //instantiate the DBHelper instance
        dbHelper = new DBHelperUtil();

        //retrieve the ListView from the view to load the items into it
        ListView list = (ListView) findViewById(R.id.listViewCat);

        //retrieve and load the list of short quotes for the selected category
        dbHelper.retrieveCategoriesFromDb(QuoteListActivity.this, list, "quote_short", categoryID, categoryTitle);
    }
}
