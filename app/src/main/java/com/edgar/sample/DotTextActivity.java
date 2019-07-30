package com.edgar.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Edgar on 2018/12/28.
 */
public class DotTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dot_text_activity);
        TextView textView = findViewById(R.id.dot_text);
        textView.setText("父母赐予我们肉身与生命，但这并不代表我们的一切都属于父母，因为我们从老师处学得治学之道，从同伴处学得相处之道，从田野里学得自然之道，这些后天的获得便是我们自己的。");
    }
}