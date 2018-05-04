package com.example.admin.bluetooth_sps.Ui.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
 * Created by admin on 2018/1/29.
 */

public class VersionActivity extends BaseActivity {
    private GestureDetector mDetector;
    private LinearLayout backLayout;
    private TextView tvVersion;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_version_layout);
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

        tvVersion = findViewById(R.id.tv_version);
        backLayout=findViewById(R.id.back_layout);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
            }
        });
        tvVersion.setText("艾停 "+getVersionName());

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

    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            //System.out.println("versionName=" + versionName + ";versionCode=" + versionCode);
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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
