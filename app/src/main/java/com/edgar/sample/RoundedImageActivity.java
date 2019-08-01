package com.edgar.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;

import com.edgar.widget.RoundedImageView;

/**
 * Created by Edgar on 2018/12/28.
 */
public class RoundedImageActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private RoundedImageView mRoundedImageView;
    private SeekBar mStrokeSeekBar;
    private SeekBar mTopLeftSeekBar;
    private SeekBar mTopRightSeekBar;
    private SeekBar mBottomRightSeekBar;
    private SeekBar mBottomLeftSeekBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_image_activity);
        mRoundedImageView = findViewById(R.id.rounded_image);
        mStrokeSeekBar = findViewById(R.id.stroke_seek_bar);
        mTopLeftSeekBar = findViewById(R.id.top_left_seek_bar);
        mTopRightSeekBar = findViewById(R.id.top_right_seek_bar);
        mBottomRightSeekBar = findViewById(R.id.bottom_right_seek_bar);
        mBottomLeftSeekBar = findViewById(R.id.bottom_left_seek_bar);
        mStrokeSeekBar.setOnSeekBarChangeListener(this);
        mTopLeftSeekBar.setOnSeekBarChangeListener(this);
        mTopRightSeekBar.setOnSeekBarChangeListener(this);
        mBottomRightSeekBar.setOnSeekBarChangeListener(this);
        mBottomLeftSeekBar.setOnSeekBarChangeListener(this);
        mTopLeftSeekBar.setMax(100);
        mTopRightSeekBar.setMax(100);
        mBottomRightSeekBar.setMax(100);
        mBottomLeftSeekBar.setMax(100);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) return;
        switch (seekBar.getId()) {
            case R.id.stroke_seek_bar:
                mRoundedImageView.setBorderSize(progress);
                break;
            case R.id.top_left_seek_bar:
                mRoundedImageView.setTopLeftRadii(getProgressRadius(progress));
                break;
            case R.id.top_right_seek_bar:
                mRoundedImageView.setTopRightRadii(getProgressRadius(progress));
                break;
            case R.id.bottom_left_seek_bar:
                mRoundedImageView.setBottomLeftRadii(getProgressRadius(progress));
                break;
            case R.id.bottom_right_seek_bar:
                mRoundedImageView.setBottomRightRadii(getProgressRadius(progress));
                break;
        }
    }

    private int getProgressRadius(int progress) {
        return (int) ((float) progress / 100 * mRoundedImageView.getWidth()) / 2;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}