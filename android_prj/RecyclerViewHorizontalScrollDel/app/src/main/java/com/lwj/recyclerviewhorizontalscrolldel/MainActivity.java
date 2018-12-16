package com.lwj.recyclerviewhorizontalscrolldel;

import android.content.ClipData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity implements ItemAdapter.IonSlidingViewClickListener{

    private RecyclerView mRecyclerView;

    private ItemAdapter mAdapter;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setAdapter();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerview);
    }


    private void setAdapter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ItemAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "click pos = " + position);
    }

    @Override
    public void onDeleteBtnClick(View view, int position) {
        Log.d(TAG, "del pos = " + position);
        mAdapter.delData(position);
    }
}
