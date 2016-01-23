package com.starwood.anglerslong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by kennystreit on 8/4/15.
 */
public class DrawerListViewAdapter extends BaseAdapter {

    Context context;
    String[] mOptionsMenu;
    int[] mImagesMenu;
    private static LayoutInflater inflater = null;

    public DrawerListViewAdapter(Context context, String[] mOptionsMenu, int[] mImagesMenu) {
        this.context = context;
        this.mOptionsMenu = mOptionsMenu;
        this.mImagesMenu = mImagesMenu;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mOptionsMenu.length;
    }

    @Override
    public Object getItem(int position) {
        return mOptionsMenu[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null)
            v = inflater.inflate(R.layout.drawer_listview_item, null);
        ImageView image = (ImageView) v.findViewById(R.id.drawer_listview_image);
        image.setImageResource(mImagesMenu[position]);
        TextView text = (TextView) v.findViewById(R.id.drawer_listview_text);
        text.setText(mOptionsMenu[position]);
        return v;
    }

}
