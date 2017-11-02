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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String email = "user@droid.com";
    private final String password = "iloveandroid";

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;

    private List<String> categoriesArr;
    private CategoryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        //sign in into firebase to retrieve data
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadMainActivityView();
                        } else {
                            displayErrorAuthentication(task);
                        }
                    }
                });
    }



    private void loadMainActivityView(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        retrieveCategoriesFromDb();

    }

    public void addCategoryToList(String category){
        categoriesArr.add(category);
    }

    //solution based on https://stackoverflow.com/questions/41434475/how-to-list-data-from-firebase-database-in-listview
    private void retrieveCategoriesFromDb(){
        categoriesArr = new ArrayList<>();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    addCategoryToList((String)item.child("name").getValue());
                    Log.d("QUOTES-MainActivity", "Category item is: " +
                            (String)item.child("name").getValue());

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("QUOTES-MainActivity", "Failed to read value.", databaseError.toException());
            }
        };

        mDatabase.child("categories").addValueEventListener(listener);

        ListView list = (ListView) findViewById(R.id.listViewCat);
        adapter = new CategoryAdapter(this, categoriesArr);
        list.setAdapter(adapter);
    }


    private void displayErrorAuthentication(Task<AuthResult> task){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(task.getException().getMessage())
                .setTitle(R.string.login_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class CategoryAdapter extends BaseAdapter {
        private Context context;
        private List<String> categoriesArr;
        private LayoutInflater inflater;

        public CategoryAdapter(Context c, List<String> categoriesArr) {
            this.context = c;
            this.categoriesArr = categoriesArr;
            this.inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return this.categoriesArr.size();
        }

        @Override
        public Object getItem(int i) {
            return this.categoriesArr.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            TextView tv;
            View row = view;
            if (view == null) {
                row = inflater.inflate(R.layout.category_custom_item, null);

                tv = (TextView) row.findViewById(R.id.categoryTV);
                tv.setText(categoriesArr.get(position));
                row.setTag(tv);
            } else {
                tv = (TextView) view.getTag();
                tv.setText(categoriesArr.get(position));
            }

            row.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, QuoteListActivity.class);
                    i.putExtra("category_index", position+"");
                    Log.d("QUOTES-MainActivity", "category position: " + position);
                    i.putExtra("category_name", categoriesArr.get(position));
                    Log.d("QUOTES-MainActivity", "category name: " + categoriesArr.get(position));
                    context.startActivity(i);
                }
            });

            return row;
        }
    }
}
