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


public class QuoteListActivity extends Activity {

    private int categoryID;
    private String categoryTitle;

    private DBHelperUtil dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView categoryTitleTV = (TextView) findViewById(R.id.categoryTitleTV);
        if ( getIntent().hasExtra("category_name") != false &&
                getIntent().getExtras().getString("category_name") != null) {
            categoryTitle = getIntent().getExtras().getString("category_name");
            categoryTitleTV.setText(categoryTitle);
        }

        if ( getIntent().hasExtra("category_index") != false &&
                getIntent().getExtras().getString("category_index") != null) {
            categoryID = Integer.parseInt(getIntent().getExtras().getString("category_index")) + 1;
        }

        dbHelper = new DBHelperUtil();

        ListView list = (ListView) findViewById(R.id.listViewCat);

        dbHelper.retrieveCategoriesFromDb(QuoteListActivity.this, list, "quote_short", categoryID, categoryTitle);
    }
}
