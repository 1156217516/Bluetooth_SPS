package com.example.admin.bluetooth_sps.Ui.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin.bluetooth_sps.Base.BaseActivity;
import com.example.admin.bluetooth_sps.R;

/**
 * Created by 11562 on 2018/4/10.
 */

public class AboutActivity extends BaseActivity {
    private LocalBroadcastManager localBroadcastManager;
    private Intent broadIntent;
    private GestureDetector mDetector;
    private LinearLayout backLayout;
    private TextView tvMenuVersion;
    private TextView tvMenuUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_about_layout);
        registerBroad();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        backLayout=(LinearLayout)findViewById(R.id.back_layout);
        tvMenuVersion = (TextView) findViewById(R.id.tv_version);
        tvMenuUpdate = (TextView) findViewById(R.id.tv_update);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
            }
        });

        tvMenuUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localBroadcastManager.sendBroadcast(broadIntent);
                finish();
            }
        });
        tvMenuVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AboutActivity.this,VersionActivity.class));
            }
        });
        mDetector=new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    //e1滑动起点，e2滑动终点，velocityX水平速度，velocityY纵向速度
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        //右划
                        if(e2.getRawX()-e1.getRawX()>200) {
                            finish();
                            overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
                            return true;
                        }

                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

    }

    private void registerBroad(){
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        broadIntent = new Intent("com.zega.action.message");
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
