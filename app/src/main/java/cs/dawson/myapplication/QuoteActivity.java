package cs.dawson.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

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

    private TextView attributedTV, dateTV, birthdateTV, fullquoteTV, refTV;
    private QuoteItem quote;
    private int quoteID;
    private int categoryID;

    /**
     * Sets the layout of the view. Retrieves the necessary information from the bundle
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
        retrieveHandleToTextViews();

        TextView quoteTitleTV = (TextView) findViewById(R.id.quoteTitle);
        quoteID = 0;
        categoryID = 0;


        //retrieve quoteTitle Text view and display in it the category name from the bundle
        if ( getIntent().hasExtra("category_title") != false &&
                getIntent().getExtras().getString("category_title") != null) {
            quoteTitleTV.setText(quoteTitleTV.getText()
                    + " " + getIntent().getExtras().getString("category_title"));
        }

        //retrieve the quote id from the bundle
        if ( getIntent().hasExtra("quote_index") != false &&
                getIntent().getExtras().getString("quote_index") != null) {
            quoteID = Integer.parseInt(getIntent().getExtras().getString("quote_index")) + 1;
        }

        //retrieve the category id from the bundle
        if ( getIntent().hasExtra("category_index") != false &&
                getIntent().getExtras().getString("category_index") != null) {
            categoryID = Integer.parseInt(getIntent().getExtras().getString("category_index"));
        }

        //retrieve the info of the quote with the DBHelper instance
        DBHelperUtil dbHelper = new DBHelperUtil();
        dbHelper.retrieveRecordsFromDb(QuoteActivity.this, null, "quote_item", categoryID, "", quoteID);
    }

    /**
     *  Saves the category title, and the indices necessary to retrieve the quote from the
     *  database, in a SharedPreferences file. This allows the specific quote being viewed to be
     *  retrieved again (via an option in the options menu) after the Activity's lifecycle ends.
     */
    @Override
    protected void onPause() {
        super.onPause();

        //create shared prefs, give it a name so we can refer to it in other Activities
        SharedPreferences prefs = getSharedPreferences("QUOTE_INDICES", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        //retrieve category title from intent extras
        editor.putString("category_title", getIntent().getExtras().getString("category_title"));
        //we need the indices to pull the specific quote from the database later
        editor.putInt("quote_index", quoteID);
        editor.putInt("category_index", categoryID);
        //save data
        editor.commit();
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

        //the following is to underline the attributed name
        SpannableString content = new SpannableString(quote.getAttributed());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        //set the information of the quote into the Text Views
        attributedTV.setText(content);
        dateTV.setText(quote.getDate_added());
        birthdateTV.setText(quote.getDob());
        fullquoteTV.setText(quote.getQuote_full());

        //set clickable link
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
        attributedTV = (TextView) findViewById(R.id.attributedTxt);
        dateTV = (TextView) findViewById(R.id.dateTxt);
        birthdateTV = (TextView) findViewById(R.id.birthdateTxt);
        fullquoteTV = (TextView) findViewById(R.id.quoteFullTxt);
        refTV = (TextView) findViewById(R.id.refTxt);
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
        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            @Override public String transformUrl(Matcher match, String url) {
                return link;
            }
        };
        Linkify.addLinks(textView, Pattern.compile(patternToMatch), null, null,
                filter);
    }

}
