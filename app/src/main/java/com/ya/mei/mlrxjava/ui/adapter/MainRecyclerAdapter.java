package com.ya.mei.mlrxjava.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ya.mei.mlrxjava.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chenliang3 on 2016/3/10.
 */
public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.BaseViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private int[] titles;
    private OnItemClickListener onItemClickListener;

    public MainRecyclerAdapter(Context context, int[] titles){
        this.context = context;
        this.titles = titles;
        inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener{
        void OnItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = new BaseViewHolder(inflater.inflate(R.layout.item_main_recycler, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        holder.textView.setText(titles[position]);

        if (onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.OnItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_main_textView)
        TextView textView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
