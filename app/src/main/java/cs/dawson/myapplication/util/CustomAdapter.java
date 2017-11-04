package cs.dawson.myapplication.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import cs.dawson.myapplication.MainActivity;
import cs.dawson.myapplication.QuoteActivity;
import cs.dawson.myapplication.QuoteListActivity;
import cs.dawson.myapplication.R;

/**
 * Custom adapter class that's used to accept a list of Strings and
 * provides methods that will return a specific element of the list,
 * the total count of items in the list, the item itself and methods
 * that will inflate the items into a list. This Custom adapter is
 * mostly used by the MainActivity and the QuoteListActivity to display
 * the list of category names or the short quotes of a category.
 *
 * @author Lyrene Labor, Peter Bellefleur
 */
public class CustomAdapter extends BaseAdapter {

    private Context context;
    private List<String> categoriesList;
    private List<String> images;
    private LayoutInflater inflater;
    private Activity activity;
    private int categoryID;
    private String categoryTitle;

    /**
     * Constructor that will initialize the list of strings, the context,
     * the string name of the activity that created the instance of the custom
     * adapter, the id of the category that is clicked from a list, the name of
     * the category that was clicked. Initializes as well the inflater used
     * to inflate the items of the list into a ListView
     *
     * @param c Context of an activity that is using the adapter
     * @param categoriesArr list of categories strings
     * @param categoryID the ID number of the category clicked from the list
     * @param categoryTitle the title of the category clicked from the list
     */
    public CustomAdapter(Context c, List<String> categoriesArr, List<String> images,
                         int categoryID, String categoryTitle) {
        this.context = c;
        this.categoriesList = categoriesArr;
        this.images = images;
        this.categoryID = categoryID;
        this.categoryTitle = categoryTitle;

        if(c instanceof MainActivity)
            this.activity = (MainActivity) c;
        if(c instanceof QuoteListActivity)
            this.activity = (QuoteListActivity) c;

        this.inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Returns the total items in the category list
     *
     * @return int
     */
    @Override
    public int getCount() {
        return this.categoriesList.size();
    }

    /**
     * Returns the item at index i
     *
     * @param i index
     * @return String item from the list
     */
    @Override
    public Object getItem(int i) {
        return this.categoriesList.get(i);
    }

    /**
     * Returns the index of an item
     *
     * @param i index
     * @return int
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * A helper class used to map a TextView with its corresponding ImageView
     */
    public class ViewHolder {
        TextView tv; ImageView iv;
    }

    /**
     * Returns a View object containing an item from the list at a particular
     * position. Inflates the view into a ListView and sets a click listener
     * on an item and depending on the activity that is using the adapter,
     * the QuoteListActivity or the QuoteActivity will be launched.
     *
     * @param position position of the item that was selected
     * @param view View in which the item was inflated into
     * @param viewGroup ViewGroup
     * @return The View that was selected
     */
    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder vh = new ViewHolder();

        View row = view;
        if (view == null) {
            //inflate the item in a view using the custom layout
            row = inflater.inflate(R.layout.category_custom_item, null);

            //retrieve the TextView in which to display the category names and display the name
            vh.tv = (TextView) row.findViewById(R.id.categoryTV);
            vh.tv.setText(categoriesList.get(position));

            if(activity instanceof MainActivity) {
                Log.d("QUOTES-CustomAdapter", "loading image for category :" +
                        categoriesList.get(position));

                //retrieve the image view and display the category image
                vh.iv = (ImageView) row.findViewById(R.id.catImg);
                loadImageIntoImageView(images.get(position), vh.iv);
            }

            row.setTag(vh);
        } else {
            //if view already exists, just retrieve info from the tag
            vh = (ViewHolder) view.getTag();
            vh.tv.setText(categoriesList.get(position));

            if(activity instanceof MainActivity) {
                //retrieve the image view and display the category image
                vh.iv = (ImageView) row.findViewById(R.id.catImg);
                loadImageIntoImageView(images.get(position), vh.iv);
            }
        }

        //set a click listener
        row.setOnClickListener(new View.OnClickListener() {
            /**
             * Click event handler that will launch the QuoteListActivity or the QuoteActivity
             * depending on the activity that called this instance, passing some data to
             * the next activity.
             *
             * @param v The View that was clicked from the list
             */
            @Override
            public void onClick(View v) {
                //if MainActivity called the current instance of adapter, then
                //launch the QuoteListActivity
                if(activity instanceof MainActivity) {
                    Intent i = new Intent(context, QuoteListActivity.class);
                    i.putExtra("category_index", position + "");
                    Log.d("QUOTES-MainActivity", "category position: " + position);
                    i.putExtra("category_name", categoriesList.get(position));
                    Log.d("QUOTES-MainActivity", "category name: " + categoriesList.get(position));
                    context.startActivity(i);
                }

                //if QuoteListActivity called the current instance of adapter, then
                //launch the QuoteActivity
                if(activity instanceof QuoteListActivity){
                    Intent i = new Intent(context, QuoteActivity.class);
                    i.putExtra("category_index", categoryID+"");
                    i.putExtra("quote_index", position+"");
                    i.putExtra("category_title", categoryTitle);
                    context.startActivity(i);
                }
            }
        });

        return row; //return the view to inflate into the ListView
    }

    //https://github.com/codepath/android_guides/wiki/Displaying-Images-with-the-Glide-Library
    private void loadImageIntoImageView(String imgName, final ImageView imgView){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageReference.child(imgName);
        Log.d("QUOTES-CustomAdapter", "Loading image: " + imgName);


        ref.getDownloadUrl().addOnSuccessListener(activity, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("QUOTES-CustomeAdapter", "Uri image: " + uri);
                Glide.with(activity)
                        .load(uri)
                        .into(imgView);
            }
        });
    }
}
