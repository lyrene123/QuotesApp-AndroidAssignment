package cs.dawson.myapplication;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs.dawson.myapplication.model.QuoteItem;
import cs.dawson.myapplication.util.DBHelperUtil;

/**
 * Displays all information related to a quote that are retrieved from the database.
 * Uses the DBHelperUtil class to retrieve the specific quote record from the database
 * and loads it into a model QuoteItem class. By having a QuoteItem instance, we can easily
 * get from that instance the information of the quote such as the attributed, the date added,
 * the date of birth of the attributed, etc. and display them in the view. Sets the click listeners
 * on the attributed text to popup the dialog containing a blurb and sets the reference text
 * as a clickable link.
 *
 * @author Lyrene Labor
 * @author Peter Bellefleur
 */
public class QuoteActivity extends MenuActivity {

    private TextView attributedTV, dateTV, birthdateTV, fullquoteTV, refTV, quoteTitleTV;
    private int quoteID, categoryID, currentOrientation;
    private ImageView imageView;
    private DBHelperUtil dbHelper;
    private QuoteItem quote;
    private String imgName;
    private boolean isRotate;

    private static String TAG = "QuoteActivity";

    /**
     * Sets the layout of the view. Retrieves the necessary information from the Intent
     * such as the category name, the category id, and quote id which will be used to retrieve the
     * all info of a quote of a category from the database with the help of the DBHelperUtil
     * class.
     *
     * @param savedInstanceState Bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);

        Log.i(TAG, "onCreate");
        retrieveHandleToTextViews();
        quoteID = 0;
        categoryID = 0;
        currentOrientation = 0;
        isRotate = false;

        //get current device orientation to be used to determine if user has rotated device
        this.currentOrientation = getResources().getConfiguration().orientation;

        retrieveDataFromIntent();
        
        //retrieve all quote into, pass the current activity, the data type
        // and set the category id and the quote id
        dbHelper = new DBHelperUtil();
        dbHelper.retrieveRecordsFromDb(QuoteActivity.this, null, "quote_item", categoryID, "",
                quoteID);
    }

    /**
     *  Saves the category title, and the indices necessary to retrieve the quote from the
     *  database, in a SharedPreferences file. This allows the specific quote being viewed to be
     *  retrieved again (via an option in the options menu) after the Activity's lifecycle ends.
     *  Only save when device is not rotating which also causes the onPause to be called.
     */
    @Override
    protected void onPause() {
        super.onPause();

        Log.i(TAG, "onPause");
        checkOrientationChanged();

        //Only save when device is not rotating which also causes the onPause to be called.
        if(!isRotate) {
            //create shared prefs, give it a name so we can refer to it in other Activities
            SharedPreferences prefs = getSharedPreferences("QUOTE_INDICES", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            //retrieve category title from intent extras
            editor.putString("category_title", getIntent().getExtras().getString("category_title"));
            //we need the indices to pull the specific quote from the database later
            editor.putInt("quote_index", quoteID);
            Log.d(TAG, "saving quote index: " + quoteID);
            editor.putInt("category_index", categoryID);
            Log.d(TAG, "saving category index: " + categoryID);
            //save data
            editor.commit();
        }
    }

    /**
     * Retrieves the orientation of the device and compares it with the property orientation
     * that was set in onCreate method. Both are compared and if orientation has changed, set
     * the isRotate boolean to true and replace the property orientation with the new orientation
     * retrieved
     * <p>
     * This method of checking orientation is inspired by
     * https://www.android-examples.com/find-get-current-screen-orientation-in-android-programmatically/
     */
    private void checkOrientationChanged() {
        Log.d(TAG, "checkOrientationChanged method called");
        int newOrientation = getResources().getConfiguration().orientation;
        if (this.currentOrientation != newOrientation) {
            Log.d("DQ", "Orientation changed");
            this.isRotate = true;
            this.currentOrientation = newOrientation;
        }
    }

    /**
     * Displays the information contained in a QuoteItem instance into the TextViews of
     * the activity view such as the attributed name, the date added, the birthdate of the
     * attribtued, the full quote, and the reference. Sets the reference as a clickable
     * link to the website in which the quote was taken from. Sets the attributed text
     * as clickable that will popup the dialog containing the blurb of the attributed.
     *
     * @param quote QuoteItem to load into the view
     */
    public void displayQuoteInfoInTextViews(QuoteItem quote){
        Log.i(TAG, "displayQuoteInfoInTextViews");

        //the following is to underline the attributed name
        SpannableString content = new SpannableString(quote.getAttributed());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        //set the information of the quote into the Text Views
        attributedTV.setText(content);
        dateTV.setText(quote.getDate_added());
        birthdateTV.setText(quote.getDob());
        fullquoteTV.setText(quote.getQuote_full());

        /*
        * Sends a request to the database to authenticate to the database before
        * being able to retrieve an image from firebase storage
        * */
        dbHelper.retrieveRecordsFromDb(this, null, "cat_img", -1, "", -1);

        //set clickable link for the reference
        addLink(refTV, "^Reference", quote.getReference());

        //set the popup dialog
        this.quote = quote;
        attributedTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayBlurbDialog();
            }
        });
    }

    /**
     * Builds and displays the dialog window that will display the blurb information
     * of the attributed and a dismiss button
     */
    private void displayBlurbDialog(){
        Log.i(TAG, "displayBlurbDialog");

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.blurb_title);
        builder.setMessage(quote.getBlurb())
                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog ad = builder.create();
        ad.show();
    }

    /**
     * Retrieves a handle of all the necessary TextViews from the view
     */
    private void retrieveHandleToTextViews(){
        Log.i(TAG, "retrieveHandleToTextViews");

        attributedTV = (TextView) findViewById(R.id.attributedTxt);
        dateTV = (TextView) findViewById(R.id.dateTxt);
        birthdateTV = (TextView) findViewById(R.id.birthdateTxt);
        fullquoteTV = (TextView) findViewById(R.id.quoteFullTxt);
        imageView = (ImageView) findViewById(R.id.categoryImg);
        refTV = (TextView) findViewById(R.id.refTxt);
        quoteTitleTV = (TextView) findViewById(R.id.quoteTitle);
    }

    /**
     * Add a link to the TextView which is given.
     *
     * Solution found in :
     * https://stackoverflow.com/questions/4746293/android-linkify-textview
     *
     * @param textView the field containing the text
     * @param patternToMatch a regex pattern to put a link around
     * @param link the link to add
     */
    private void addLink(TextView textView, String patternToMatch,
                               final String link) {
        Log.i(TAG, "addLink");
        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            @Override public String transformUrl(Matcher match, String url) {
                return link;
            }
        };
        Linkify.addLinks(textView, Pattern.compile(patternToMatch), null, null,
                filter);
    }

    /**
     * Loads an image from the firebase storage. Retrieves a storage reference first
     * for a particular image name and the help of Glide, load the image retrieved
     * from storage into an image view.
     *
     * Solution based on:
     * https://github.com/codepath/android_guides/wiki/Displaying-Images-with-the-Glide-Library
     *
     */
    public void loadImageIntoImageView(){
        Log.i(TAG, "loadImageIntoImageView()");

        //get storage ref for an image
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageReference.child(imgName);
        Log.d(TAG, "Loading image: " + imgName);

        //load the image in an ImageView with Glide
        ref.getDownloadUrl().addOnSuccessListener(QuoteActivity.this, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "Uri image: " + uri);
                Glide.with(QuoteActivity.this)
                        .load(uri)
                        .into(imageView);
            }
        });
    }

    /**
     * Creates the String image filename depending on the category index or id.
     */
    private void determineImageFilename(){
        Log.i(TAG, "determineImageFilename");
        switch (categoryID){
            case 1 :
                imgName = "fuitsvegies.png";
                break;
            case 2 :
                imgName = "junkfood.png";
                break;
            case 3 :
                imgName = "pasta.png";
                break;
            case 4 :
                imgName = "seafood.png";
                break;
            case 5 :
                imgName = "soup.png";
                break;
            default :
                imgName = "";
        }

        Log.d(TAG, "created image filename: " + imgName);
    }

    /**
     * Retrieves the necessary data from the Intent object that was passed as
     * extras. Load the data into their appropriate Widget or instance variable.
     */
    private void retrieveDataFromIntent(){
        Log.d(TAG, "retrieveDataFromIntent started");

        //retrieve quoteTitle Text view and display in it the category name from the Intent
        if ( getIntent().hasExtra("category_title") != false &&
                getIntent().getExtras().getString("category_title") != null) {
            quoteTitleTV.setText(quoteTitleTV.getText()
                    + " " + getIntent().getExtras().getString("category_title"));
        }

        //retrieve the quote id from the Itent
        if ( getIntent().hasExtra("quote_index") != false &&
                getIntent().getExtras().getString("quote_index") != null) {
            quoteID = Integer.parseInt(getIntent().getExtras().getString("quote_index"));
        }

        //retrieve the category id from the Itent
        if ( getIntent().hasExtra("category_index") != false &&
                getIntent().getExtras().getString("category_index") != null) {
            categoryID = Integer.parseInt(getIntent().getExtras().getString("category_index"));
        }

        //retrieve the category image from the Itent
        if ( getIntent().hasExtra("category_img") != false &&
                getIntent().getExtras().getString("category_img") != null) {
            imgName = getIntent().getExtras().getString("category_img");
        } else {
            //if the image is not passed to the Itent, then determine the image based on category number
            determineImageFilename();
        }
    }

}
