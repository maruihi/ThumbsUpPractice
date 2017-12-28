package com.mr.thumbsuppractice;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mr.thumbsuppractice.thumbsUpView.ThumbsImgView;
import com.mr.thumbsuppractice.thumbsUpView.ThumbsUpLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private EditText et;
    private ThumbsUpLayout thumbsUpLayout;
    private Context context;
    private TextView tv_ok;
    private RadioGroup rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        initView();

    }

    private void initView() {
        thumbsUpLayout = findViewById(R.id.thumbs_up_layout);
        et = findViewById(R.id.et);
        tv_ok = findViewById(R.id.tv_ok);
        rg = findViewById(R.id.rg);

        tv_ok.setOnClickListener(this);
        rg.setOnCheckedChangeListener(this);

        thumbsUpLayout.setCount(100);
        thumbsUpLayout.setThumbsUpResultListener(new ThumbsImgView.ThumbsUpResultListener() {
            @Override
            public void result(boolean isThumbsUp) {
                if (isThumbsUp) {
                    Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
                    Log.e("mainActivity", "点赞成功");
                } else {
                    Toast.makeText(context, "取消点赞成功", Toast.LENGTH_SHORT).show();
                    Log.e("mainActivity", "取消点赞成功");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_ok:
                if (!TextUtils.isEmpty(et.getText().toString().trim())) {
                    thumbsUpLayout.setCount(Integer.valueOf(et.getText().toString().trim()));
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_ok) {
            thumbsUpLayout.setThumbUp(true);
        } else {
            thumbsUpLayout.setThumbUp(false);
        }

    }
}
