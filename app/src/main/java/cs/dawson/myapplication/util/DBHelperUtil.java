package cs.dawson.myapplication.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ListView;

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

import cs.dawson.myapplication.R;

/**
 * Helper utility class that handles database access operations and authentication
 * for the firebase. This DAO class only handles read operations against the database.
 * Only when the authentication to the firebase is successful,
 * handles the retrieval of the names of the categories for the MainActivity
 * and loading them into a ListView with the help of a custom adapter.
 * Handles the retrieval as well of the short quotes from the firebase and loaded
 * into a ListView with a custom adapter for the QuoteListActivity. For the QuoteActivity,
 * handles the retrieval of all information of a particular quote of category.
 *
 *
 * @author Lyrene Labor, Peter Bellefleur
 */
public class DBHelperUtil {

    //for the list of categories
    private List<String> categoriesList;
    private CustomAdapter adapter;

    //for database access and authentication
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;

    //credentials for database authentication
    private final String email = "user@droid.com";
    private final String password = "iloveandroid";

    /**
     * Initializes the DatabaseReference object for retrieval of data
     * and the FirebaseAuth for database authentication
     */
    public DBHelperUtil(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Retrieves the names of the categories only when the database authentication
     * is successful. If not, a dialog will display with an error message letting the user know
     * that an error has occurred
     *
     * @param activity Activity that called this method
     * @param list ListView that will contain the list of categories
     */
    public void retrieveCategoriesFromDb(final Activity activity, final ListView list){
        //initialize the list of categories
        categoriesList = new ArrayList<>();

        //sign in into firebase to retrieve categories
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //load the main activity view containing the list view if successful
                            loadCategoriesFromDb(list, activity);

                        } else {
                            //display en error dialog box if authentication failed
                            displayErrorAuthentication(task, activity);
                        }
                    }
                });
    }

    /**
     * Retrieves the list of categories from the database and instantiates the customer
     * adapter in order to set the data retrieved into the ListView
     *
     * Solution based on :
     * solution based on https://stackoverflow.com/questions/41434475/how-to-list-data-from-firebase-database-in-listview
     *
     * @param list ListView object which we want to load the data into
     * @param activity Activity that called the method
     */
    private void loadCategoriesFromDb(ListView list, Activity activity){
        //create the ValueEventListener listener object
        ValueEventListener listener = new ValueEventListener() {
            /**
             * Retrieves any changes made to the database. In our case
             * this method will be called once at the beginning when
             * retrieving the initial data.
             *
             * @param dataSnapshot DataSnapshot object data from db
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    //add the retrieved data into the list of categories
                    addCategoryToList((String)item.child("name").getValue());
                    Log.d("QUOTES-MainActivity", "Category item is: " +
                            (String)item.child("name").getValue());

                    adapter.notifyDataSetChanged();
                }
            }

            /**
             * onCancelled called when an error has occurred and the data cannot be retrieved
             *
             * @param databaseError DatabaseError containing the error that occured
             */
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("QUOTES-MainActivity", "Failed to read value.", databaseError.toException());
            }
        };

        //set the listener for the categories of the database
        mDatabase.child("categories").addValueEventListener(listener);

        //instantiate the custom adapter
        adapter =  new CustomAdapter(activity, categoriesList, "MainActivity", -1, "");

        //set the adapter into the ListView
        list.setAdapter(adapter);
    }

    /**
     * Creates and displays an alert dialog that will display an error message if
     * the authentication sign in to the firebase has failed.
     *
     * @param task AuthResult task
     */
    private void displayErrorAuthentication(Task<AuthResult> task, Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(task.getException().getMessage())
                .setTitle(R.string.login_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Adds a category string into the list of Strings that holds the names of
     * categories
     *
     * @param category category name to add to the list
     */
    private void addCategoryToList(String category){
        categoriesList.add(category);
    }



}

