package com.ya.mei.mlrxjava.ui.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.ya.mei.mlrxjava.R;

import java.util.List;

/**
 * Created by chenliang3 on 2016/3/11.
 */
public class LogAdapter extends ArrayAdapter {

    public LogAdapter(Context context, List<String> logs) {
        super(context, R.layout.item_log, R.id.item_log, logs);
    }

}
