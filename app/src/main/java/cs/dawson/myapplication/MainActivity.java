package cs.dawson.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;
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
 * Displays to the user a list of category names that are retrieved from the
 * firebase database. Uses the DbHelper class for firebase authentication
 * in order to access the database. The DbHelper class will be able to
 * populate the list view of the main activity. Items are loaded with the help of
 * the custom adapter and a custom layout is used for each list item.
 * A click event listeners is set up for each list item that will launch
 * an intent to display the QuoteListActivity and display the list of quote.
 *
 * @author Lyrene Labor, Peter Bellefleur
 */
public class MainActivity extends AppCompatActivity {

    //for DAO access methods
    private DBHelperUtil dbHelper;


    /**
     * Sets the layout of the activity. Initializes the DBHelper instance
     * to be used for the firebase authentication and retrieving the categories
     * names. Retrieves the ListView object from the view that will contain
     * the categories names. DBHelper will then load the categories into the
     * list
     *
     * @param savedInstanceState Bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize DBHelper object
        dbHelper = new DBHelperUtil();

        //retrieve the ListView that will hold the list of categories
        ListView list = (ListView) findViewById(R.id.listViewCat);

        //retrieve and load the category names from the database into the list view
        dbHelper.retrieveCategoriesFromDb(MainActivity.this, list);
    }
}
