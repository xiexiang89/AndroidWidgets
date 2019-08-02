package com.edgar.sample;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;

/**
 * Created by Edgar on 2019/8/2.
 */
public class MaterialButtonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_button_activity);
        MaterialButton materialButton = findViewById(R.id.material_btn);
        materialButton.setTypeface(ResourcesCompat.getFont(this,R.font.pingfang));
    }
}
