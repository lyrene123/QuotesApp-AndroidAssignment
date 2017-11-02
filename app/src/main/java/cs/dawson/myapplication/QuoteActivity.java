package cs.dawson.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class QuoteActivity extends Activity {

    TextView attributedTV, dateTV, birthdateTV, fullquoteTV, refTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);

        TextView quoteTitleTV = (TextView) findViewById(R.id.quoteTitle);
        if ( getIntent().hasExtra("category_title") != false &&
                getIntent().getExtras().getString("category_name") != null) {
            quoteTitleTV.setText(quoteTitleTV.getText()
                    + " " + getIntent().getExtras().getString("category_name"));
        }
    }

    private void retrieveHandleToTextViews(){
        //attributedTV = (TextView) findViewById(R.)
    }
}
