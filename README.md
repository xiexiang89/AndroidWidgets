# Android Switch widget

## Switch attrs
属性名 | 作用 | 类型
--- | --- | ---
android:thumb | 设置Switch滑块图标 | drawable
app:trackWidth | 设置Switch轨道的宽度 | dimension
app:trackHeight | 设置Switch轨道的高度 | dimension
app:track_uncheck_color | 设置Switch轨道未选中状态颜色 | color
app:track_check_color | 设置Switch轨道选中状态颜色 | color
app:track_radius | 设置Switch轨道背景的圆角值 | dimension
app:thumbPadding | 设置Switch滑块的横向内边距 | dimension

## Example

### layout:
```xml
    <com.edgar.widget.SwitchButton
        android:id="@+id/switch_btn"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:trackWidth="@dimen/default_track_width"
        app:trackHeight="@dimen/default_track_height"
        android:thumb="@drawable/default_thumb"
        app:track_uncheck_color="@color/default_unchecked_color"
        app:track_check_color="@color/default_checked_color"
        app:track_radius="@dimen/default_track_radius"
        android:layout_gravity="center"/>
```
### Java
```
        final SwitchButton switchButton = findViewById(R.id.switch_btn);
        switchButton.setChecked(true);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getApplicationContext(),isChecked?"选中":"未选中",Toast.LENGTH_SHORT).show();
            }
        });
```