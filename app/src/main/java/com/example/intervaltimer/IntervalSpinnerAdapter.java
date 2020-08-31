package com.example.intervaltimer;

import android.database.DataSetObserver;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class IntervalSpinnerAdapter implements SpinnerAdapter, AdapterView.OnItemSelectedListener {

    static final String[] INTERVAL_TIME = {"---", "30", "60", "90", "180"};

    private final DynamicTheme m_theme;
    private View m_lastView = null;

    IntervalSpinnerAdapter(DynamicTheme myTheme) {
        m_theme = myTheme;
    }


    private View getViewPrivate(int position, View convertView, ViewGroup parent) {
        TextView returnView;
        if (convertView instanceof TextView) {
            returnView = (TextView) convertView;
        } else {
            returnView = new TextView(parent.getContext());
            returnView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
            returnView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        returnView.setText(INTERVAL_TIME[position]);
        m_theme.apply((ViewGroup) parent.getParent());// this seems like doing nothing, but apply actually applies the colors
        m_theme.remove((ViewGroup) parent.getParent());// and we can then remove it(since the user won't be able to change the colors while in this menu).
        return returnView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getViewPrivate(position, convertView, parent);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        //our data is static, no need for this
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        //our data is static, no need for this
    }

    @Override
    public int getCount() {
        return INTERVAL_TIME.length;
    }

    @Override
    public Object getItem(int position) {
        return INTERVAL_TIME[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewPrivate(position, convertView, parent);//I guess this is the same as getViewPrivate??
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (m_lastView != view) {
            m_theme.remove(parent);
            m_lastView = view;
            m_theme.apply(parent);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
