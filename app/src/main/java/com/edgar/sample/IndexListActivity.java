package com.edgar.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Edgar on 2018/12/28.
 * Sample list
 */
public class IndexListActivity extends AppCompatActivity {

    private ListView mIndexListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_list_activity);
        mIndexListView = findViewById(R.id.index_list);
        mIndexListView.setAdapter(new ArrayAdapter<String>(this,R.layout.index_list_item,android.R.id.text1,
                getResources().getStringArray(R.array.sample_index)));
        mIndexListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handlerItemClick(position);
            }
        });
    }

    private void handlerItemClick(int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(this,SwitchActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, DotTextActivity.class));
                break;
        }
    }
}