package com.starwood.anglerslong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kennystreit on 8/10/15.
 */
public class ChecklistAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context context;
    private ArrayList<ChecklistItem> checklist = new ArrayList<ChecklistItem>();

    public ChecklistAdapter(Context context, ArrayList<ChecklistItem> checklist) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.checklist = checklist;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.checklist_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.checklist_item);
            holder.checkbox = (CheckBox) convertView
                    .findViewById(R.id.checklist_item_check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(checklist.get(position).getName());
        holder.checkbox
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        checklist.get(position).setChecked(isChecked);
                    }
                });
        holder.checkbox.setChecked(checklist.get(position).isChecked());

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        CheckBox checkbox;
    }

    @Override
    public int getCount() {
        return checklist.size();
    }

    @Override
    public ChecklistItem getItem(int arg0) {
        return checklist.get(arg0);
    }
}
