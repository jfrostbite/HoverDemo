package com.kevin.hoverdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HoverItemDecoration.HoverState {

    private RecyclerView mRv;
    private ArrayList<Integer> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        mRv = findViewById(R.id.rv);

        mRv.setLayoutManager(new LinearLayoutManager(this));

        mRv.addItemDecoration(new HoverItemDecoration(this));

        mRv.setAdapter(new RvAdapter(mList));
    }

    private void initData() {
        mList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mList.add(i);
        }
    }

    @Override
    public boolean isHoverTag(int position) {
        return position % 5 == 0;
    }

    @Override
    public String getHoverTitle(int position) {
        return String.valueOf(position / 5);
    }
}
