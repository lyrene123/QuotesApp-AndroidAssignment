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
import java.util.Arrays;
import java.util.List;

import cs.dawson.myapplication.QuoteActivity;
import cs.dawson.myapplication.R;
import cs.dawson.myapplication.model.QuoteItem;

/**
 * Helper utility class that handles database access operations and authentication
 * for the firebase. This DAO class only handles read operations against the database.
 * Only when the authentication to the firebase is successful, handles the retrieval of
 * the names of the categories for the MainActivity and loading them into a ListView
 * with the help of a custom adapter. Handles the retrieval as well of the short quotes
 * from the firebase and loaded into a ListView with a custom adapter for the QuoteListActivity.
 * For the QuoteActivity, handles the retrieval of all information of a particular quote
 * of category and data loaded into a model class.
 *
 * @author Lyrene Labor
 * @author Peter Bellefleur
 */
public class DBHelperUtil {

    //the different lists to fill with data from database
    private List<String> categoriesList;
    private List<String> quoteList;
    private List<String> imageList;
    private CustomAdapter adapter;

    //for database access and authentication
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;

    //credentials for database authentication
    private final String email = "user@droid.com";
    private final String password = "iloveandroid";

    //model class to fill up with quote information
    private  QuoteItem quote;

    //extra information needed for retrieval of specific records from the database
    private String imgName;

    private static String TAG = "QUOTES-QLActivity";

    /**
     * Initializes the DatabaseReference object for retrieval of data
     * and the FirebaseAuth for database authentication
     */
    public DBHelperUtil(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }


    /**
     * Retrieves the names of the categories, the short quotes of a category, or all the info of a quote
     * depending on the data input String which indicates the type data we want to data.
     * But handles database authentication first before retrieving any database records.
     * If not, a dialog will display with an error message letting the user know
     * that an error has occurred
     *
     * @param activity Activity that needs access to the database
     * @param list ListView that will contain the list of categories or short quotes
     *             (applied for the MainActivity and QuoteListActivity)
     * @param data String containing the type of data you want to data from the database
     */
    public void retrieveRecordsFromDb(final Activity activity, final ListView list, final String data,
                                      final int categoryID, final String categoryTitle, final int quoteID){

        //sign in into firebase to data records from database
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //if category names are the data you want to data
                            if(data.equalsIgnoreCase("category")) {
                                loadCategoriesFromDb(list, activity);
                            }

                            //if the list of category short quotes are the data to data
                            if(data.equalsIgnoreCase("quote_short")){
                                loadCategoryShortQuoteFromDb(list, activity, categoryID, categoryTitle);
                            }

                            //if a quote and its related info are the data to data
                            if(data.equalsIgnoreCase("quote_item")){
                                loadQuoteItemFromDb(activity, categoryID, quoteID);
                            }

                        } else {
                            //display en error dialog box if authentication failed
                            displayErrorAuthentication(task, activity);
                        }
                    }
                });
    }

    /**
     * Sets the image file name
     *
     * @param imgName String image file name
     */
    public void setImgName(String imgName){
        this.imgName = imgName;
    }

    /**
     * Retrieves the short quotes of a particular category and loads them into a ListView with
     * the help of a CustomAdapter object.
     *
     * @param list ListView in which to load the short quotes from the database
     */
    private void loadCategoryShortQuoteFromDb(ListView list, Activity activity, int categoryID,
                                              String categoryTitle){
        //initialize the list of short quotes
        quoteList = new ArrayList<>();

        //create the ValueEventListener listener object
        ValueEventListener listener = new ValueEventListener() {
            /**
             * Retrieves any changes made to the database. In our case
             * this method will be called once at the beginning when
             * retrieving the initial data. Retrieves all the short quotes
             * of a specific category and notifies the change to the adapter
             *
             * @param dataSnapshot DataSnapshot object data from db
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){

                    //add the retrieved short quote into the list of quotes
                    addShortQuoteToList((String)item.child("quote_short").getValue());
                    Log.d(TAG, "Quote-short item is: " +
                            (String)item.child("quote_short").getValue());

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
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        };

        //set the listener
        mDatabase.child("quotes").child(categoryID+"").addValueEventListener(listener);

        //create the adapter and set it to the ListView to inflate the items
        //pass the category image as the only item inside a List
        adapter = new CustomAdapter(activity, quoteList, new ArrayList<>(Arrays.asList(imgName)),
                categoryID, categoryTitle);
        list.setAdapter(adapter);
    }

    /**
     * Retrieves the information of a quote of a particular category and loads
     * all data retrieved into a model class QuoteItem. Once all information has been
     * loaded into a QuoteItem instance, display the quote information in the view by
     * calling the displayQuoteInfoInTextViews of the activity that requested the data
     * which is the QuoteActivity.
     *
     * @param activity Activity that requested the data which is the QuoteActivity
     */
    private void loadQuoteItemFromDb(final Activity activity, int categoryID,
                                     int quoteID){

        //create the ValueEventListener object
        ValueEventListener listener = new ValueEventListener() {
            /**
             * Retrieves any changes made to the database. In our case
             * this method will be called once at the beginning when
             * retrieving the initial data. Retrieves and load the quote
             * into a QuoteItem instance
             *
             * @param dataSnapshot DataSnapshot object data from db
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //retrieve and load the quote into a QuoteItem instance
                setQuoteItem(dataSnapshot.getValue(QuoteItem.class));
                Log.d(TAG, "Retrieved quote item: " + quote.getDate_added());

                //once quote is loaded into a QuoteItem instance, display the quote info
                if(quote != null){
                    ((QuoteActivity) activity).displayQuoteInfoInTextViews(quote);
                }
            }

            /**
             * onCancelled called when an error has occurred and the data cannot be retrieved
             *
             * @param databaseError DatabaseError containing the error that occured
             */
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        };

        //set the listener
        mDatabase.child("quotes").child(categoryID+"").child("q" + quoteID).addValueEventListener(listener);
    }


    /**
     * Retrieves the list of categories from the database and instantiates the custom
     * adapter in order to load the category names into the ListView
     *
     * Solution based on :
     * https://stackoverflow.com/questions/41434475/how-to-list-data-from-firebase-database-in-listview
     *
     * @param list ListView object which we want to load the data into
     * @param activity Activity that called the method
     */
    private void loadCategoriesFromDb(ListView list, final Activity activity){
        //initialize the list of categories
        categoriesList = new ArrayList<>();
        //initialize the list of categories images
        imageList = new ArrayList<>();

        //create the ValueEventListener listener object
        ValueEventListener listener = new ValueEventListener() {
            /**
             * Retrieves any changes made to the database. In our case
             * this method will be called once at the beginning when
             * retrieving the initial data. Retrieves the name of
             * the categories and notifies the adapter
             *
             * @param dataSnapshot DataSnapshot object data from db
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){

                    //add the retrieved data into the list of categories
                    addCategoryToList((String)item.child("name").getValue());

                    //add the retrieved image into the list of images
                    addImageToList((String)item.child("image").getValue());
                    Log.d(TAG, "Category item is: " +
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
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        };

        //set the listener for the categories of the database
        mDatabase.child("categories").addValueEventListener(listener);

        //instantiate the custom adapter
        adapter =  new CustomAdapter(activity, categoriesList, imageList, -1, "");

        //set the adapter into the ListView
        list.setAdapter(adapter);
    }

    /**
     * Adds an image file name string into the list of images
     *
     * @param imgName String image file name
     */
    private void addImageToList(String imgName){
        imageList.add(imgName);
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
     * Adds a category string name into the list of categories
     *
     * @param category category name to add to the list
     */
    private void addCategoryToList(String category){
        categoriesList.add(category);
    }

    /**
     * Adds a short quote string into the list quotes
     *
     * @param shortQuote short quote to add to the list
     */
    private void addShortQuoteToList(String shortQuote){
        quoteList.add(shortQuote);
    }

    /**
     * Sets the QuoteItem model instance.
     *
     * @param item QuoteItem object to set
     */
    private void setQuoteItem(QuoteItem item){
        this.quote = item;
    }

}

