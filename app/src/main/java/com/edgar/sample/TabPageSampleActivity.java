package com.edgar.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edgar.widget.tabs.TabLayout;

public class TabPageSampleActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_page_sample_activity);
        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setTabIndicatorFullWidth(false);
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(new TabPagerAdapter());
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private class TabPagerAdapter extends PagerAdapter {
        private final String[] mTabs = {"Tab1", "Tab2","Tab3"};

        @Override
        public int getCount() {
            return mTabs.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TextView textView = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.pager_item, container,false);
            textView.setText("Position:"+position);
            container.addView(textView);
            return textView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs[position];
        }
    }
}