package cs.dawson.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs.dawson.myapplication.model.QuoteItem;
import cs.dawson.myapplication.util.DBHelperUtil;


public class QuoteActivity extends Activity {

    private TextView attributedTV, dateTV, birthdateTV, fullquoteTV, refTV;
    private List<TextView> textviews;
    private DBHelperUtil dbHelper;
    private QuoteItem quote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        retrieveHandleToTextViews();

        TextView quoteTitleTV = (TextView) findViewById(R.id.quoteTitle);
        if ( getIntent().hasExtra("category_title") != false &&
                getIntent().getExtras().getString("category_name") != null) {
            quoteTitleTV.setText(quoteTitleTV.getText()
                    + " " + getIntent().getExtras().getString("category_name"));
        }

        int quoteID = 0;
        if ( getIntent().hasExtra("quote_index") != false &&
                getIntent().getExtras().getString("quote_index") != null) {
            quoteID = Integer.parseInt(getIntent().getExtras().getString("quote_index")) + 1;
        }

        int categoryID = 0;
        if ( getIntent().hasExtra("category_index") != false &&
                getIntent().getExtras().getString("category_index") != null) {
            categoryID = Integer.parseInt(getIntent().getExtras().getString("category_index"));
        }

        dbHelper = new DBHelperUtil();
        dbHelper.retrieveRecordsFromDb(QuoteActivity.this, null, "quote_item", categoryID, "", quoteID, textviews);
    }

    public void displayQuoteInfoInTextViews(QuoteItem quote){
        SpannableString content = new SpannableString(quote.getAttributed());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        attributedTV.setText(content);
        dateTV.setText(quote.getDate_added());
        birthdateTV.setText(quote.getDob());
        fullquoteTV.setText(quote.getQuote_full());
        addLink(refTV, "^Reference", quote.getReference());

        this.quote = quote;

        attributedTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayBlurbDialog();
            }
        });
    }

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

    private void retrieveHandleToTextViews(){
        attributedTV = (TextView) findViewById(R.id.attributedTxt);
        dateTV = (TextView) findViewById(R.id.dateTxt);
        birthdateTV = (TextView) findViewById(R.id.birthdateTxt);
        fullquoteTV = (TextView) findViewById(R.id.quoteFullTxt);
        refTV = (TextView) findViewById(R.id.refTxt);

        textviews = new ArrayList<>(Arrays.asList(attributedTV, dateTV, birthdateTV, fullquoteTV, refTV));
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
