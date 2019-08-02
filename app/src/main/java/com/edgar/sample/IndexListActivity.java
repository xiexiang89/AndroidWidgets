package com.edgar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edgar on 2018/12/28.
 * Sample list
 */
public class IndexListActivity extends AppCompatActivity {

    private ListView mIndexListView;
    private List<IndexItem> mIndexItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIndexList();
        setContentView(R.layout.index_list_activity);
        mIndexListView = findViewById(R.id.index_list);
        mIndexListView.setAdapter(new ArrayAdapter<IndexItem>(this,R.layout.index_list_item,android.R.id.text1,
                mIndexItems));
        mIndexListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IndexItem indexItem = mIndexItems.get(position);
                startActivity(indexItem.getActivityClass());
            }
        });
    }

    private void initIndexList() {
        mIndexItems.add(new IndexItem(getString(R.string.switch_sample),SwitchActivity.class));
        mIndexItems.add(new IndexItem(getString(R.string.dottext_sample),DotTextActivity.class));
        mIndexItems.add(new IndexItem(getString(R.string.rounded_image_sample),RoundedImageActivity.class));
        mIndexItems.add(new IndexItem(getString(R.string.material_button),MaterialButtonActivity.class));
    }

    private void startActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(this,clazz));
    }
}