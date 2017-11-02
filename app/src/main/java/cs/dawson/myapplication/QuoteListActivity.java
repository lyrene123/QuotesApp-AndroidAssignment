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

/**
 * Created by laborlyrene on 2017-11-01.
 */

public class QuoteListActivity extends Activity {

    private final String email = "user@droid.com";
    private final String password = "iloveandroid";

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;

    private List<String> quoteList;
    private QuoteListAdapter adapter;
    private int categoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView categoryTitleTV = (TextView) findViewById(R.id.categoryTitleTV);
        if ( getIntent().hasExtra("category_name") != false &&
                getIntent().getExtras().getString("category_name") != null) {
            categoryTitleTV.setText(getIntent().getExtras().getString("category_name"));
        }

        if ( getIntent().hasExtra("category_index") != false &&
                getIntent().getExtras().getString("category_index") != null) {
            categoryID = Integer.parseInt(getIntent().getExtras().getString("category_index")) + 1;
        }

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        //sign in into firebase to retrieve data
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(QuoteListActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadQuoteListActivityView();
                        } else {
                            displayErrorAuthentication(task);
                        }
                    }
                });
    }

    private void loadQuoteListActivityView(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        retrieveQuoteListFromDb();

    }

    private void addQuoteToList(String quote){
        quoteList.add(quote);
    }

    private void retrieveQuoteListFromDb(){
        quoteList = new ArrayList<>();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    addQuoteToList((String)item.child("quote_short").getValue());
                    Log.d("QUOTES-QLActivity", "Quote-short item is: " +
                            (String)item.child("quote_short").getValue());

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("QUOTES-QLActivity", "Failed to read value.", databaseError.toException());
            }
        };

        mDatabase.child("quotes").child(categoryID+"").addValueEventListener(listener);

        ListView list = (ListView) findViewById(R.id.listViewCat);
        adapter = new QuoteListAdapter(this, quoteList);
        list.setAdapter(adapter);
    }


    private void displayErrorAuthentication(Task<AuthResult> task){
        AlertDialog.Builder builder = new AlertDialog.Builder(QuoteListActivity.this);
        builder.setMessage(task.getException().getMessage())
                .setTitle(R.string.login_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private class QuoteListAdapter extends BaseAdapter {
        private Context context;
        private List<String> quoteList;
        private LayoutInflater inflater;

        public QuoteListAdapter(Context c, List<String> quoteList) {
            this.context = c;
            this.quoteList = quoteList;
            this.inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return this.quoteList.size();
        }

        @Override
        public Object getItem(int i) {
            return this.quoteList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            TextView tv;
            View row = view;
            if (view == null) {
                row = inflater.inflate(R.layout.category_custom_item, null);

                tv = (TextView) row.findViewById(R.id.categoryTV);
                tv.setText(quoteList.get(position));
                row.setTag(tv);
            } else {
                tv = (TextView) view.getTag();
                tv.setText(quoteList.get(position));
            }

           /* row.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, QuoteListActivity.class);
                    i.putExtra("category_index", position);
                    i.putExtra("category_name", categoriesArr.get(position));
                    context.startActivity(i);
                }
            });*/

            return row;
        }
    }
}
