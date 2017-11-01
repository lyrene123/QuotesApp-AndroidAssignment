package cs.dawson.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String email = "user@droid.com";
    private final String password = "iloveandroid";

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;

    private String[] categoriesArr;
    private ImageView[] categoriesImgArr;
    private String[] imgSourceStr;


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
        loadCategoriesImgToImageViews();

        ListView list = (ListView) findViewById(R.id.listViewCat);
        list.setAdapter(new CategoryAdapter(this, categoriesArr, categoriesImgArr));
    }

    private void retrieveCategoriesFromDb(){
        ArrayList<String> listCategories = new ArrayList<>();
        ArrayList<String> listCategoriesImg = new ArrayList<>();

        for(int i = 1; i < 6; i++){
            String item = mDatabase.child("categories").child(i+"").child("name").toString();
            String imgSrc = mDatabase.child("categories").child(i+"").child("image").toString();
            listCategories.add(item);
            listCategoriesImg.add(imgSrc);
        }

        imgSourceStr = (String[]) listCategoriesImg.toArray();
        categoriesArr = (String[]) listCategories.toArray();
    }

    private void loadCategoriesImgToImageViews(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        ArrayList<ImageView> listCategoriesImg = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            ImageView img = null;
            StorageReference imgRef = storageReference.child(imgSourceStr[i]);

            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(imgRef)
                    .into(img);
            listCategoriesImg.add(img);
        }

        categoriesImgArr = (ImageView[]) listCategoriesImg.toArray();
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
        String [] categoriesArr;
        ImageView[] categoriesImgArr;
        LayoutInflater inflater;

        public CategoryAdapter(Context c,
                               String[] categoriesArr, ImageView[] categoriesImgArr) {
            this.context = c;
            this.categoriesArr = categoriesArr;
            this.categoriesImgArr = categoriesImgArr;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return this.categoriesArr.length;
        }

        @Override
        public Object getItem(int i) {
            return this.categoriesArr[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public class ViewHolder {
            TextView categoryTV; ImageView categoryImgView;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHolder vh = new ViewHolder();
            View row = view;

            if (view == null) {
                row = inflater.inflate(R.layout.custom_category_list, null);

                vh.categoryTV = (TextView) row.findViewById(R.id.categoryTxt);
                vh.categoryImgView = (ImageView) row.findViewById(R.id.categoryImg);
                vh.categoryTV.setText(categoriesArr[position]);
                vh.categoryImgView.setImageDrawable(categoriesImgArr[position].getDrawable());
                row.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
                vh.categoryTV.setText(categoriesArr[position]);
                vh.categoryImgView.setImageDrawable(categoriesImgArr[position].getDrawable());
            }

            return row;
        }
    }
}
