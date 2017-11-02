package cs.dawson.myapplication.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cs.dawson.myapplication.QuoteActivity;
import cs.dawson.myapplication.QuoteListActivity;
import cs.dawson.myapplication.R;

/**
 *
 */
public class CustomAdapter extends BaseAdapter {

    private Context context;
    private List<String> categoriesList;
    private LayoutInflater inflater;
    private String activity;
    private int categoryID;
    private String categoryTitle;

    public CustomAdapter(Context c, List<String> categoriesArr, String activity,
                         int categoryID, String categoryTitle) {
        this.context = c;
        this.categoriesList = categoriesArr;
        this.activity = activity;
        this.categoryID = categoryID;
        this.categoryTitle = categoryTitle;
        this.inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.categoriesList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.categoriesList.get(i);
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
            tv.setText(categoriesList.get(position));
            row.setTag(tv);
        } else {
            tv = (TextView) view.getTag();
            tv.setText(categoriesList.get(position));
        }

        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(activity.equalsIgnoreCase("MainActivity")) {
                    Intent i = new Intent(context, QuoteListActivity.class);
                    i.putExtra("category_index", position + "");
                    Log.d("QUOTES-MainActivity", "category position: " + position);
                    i.putExtra("category_name", categoriesList.get(position));
                    Log.d("QUOTES-MainActivity", "category name: " + categoriesList.get(position));
                    context.startActivity(i);
                }

                if(activity.equalsIgnoreCase("QuoteListActivity")){
                    Intent i = new Intent(context, QuoteActivity.class);
                    i.putExtra("category_index", categoryID+"");
                    i.putExtra("quote_index", position+"");
                    i.putExtra("category_title", categoryTitle);
                    context.startActivity(i);
                }
            }
        });

        return row;
    }
}
