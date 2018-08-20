package com.kevin.hoverdemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.RvVH> {

    private List<Integer> mList;

    public RvAdapter(List<Integer> list) {

        mList = list;
    }

    @NonNull
    @Override
    public RvVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 100);

        TextView tv = new TextView(viewGroup.getContext());
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(lp);
        return new RvVH(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull RvVH rvVH, int i) {
        rvVH.setData(String.valueOf(mList.get(i)));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class RvVH extends RecyclerView.ViewHolder {
        public RvVH(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(String str) {
            ((TextView)itemView).setText(str);
        }
    }
}
