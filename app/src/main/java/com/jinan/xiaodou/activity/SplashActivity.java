package com.jinan.xiaodou.activity;

import android.os.Bundle;
import android.view.WindowManager;

import com.jinan.xiaodou.BaseActivity;
import com.jinan.xiaodou.R;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //做自动登录
        mSuperHandler.postDelayed(new Runnable(){
            public void run() {
                jump(TabMainActivity.class, true, new Bundle());
            }
        }, 3000);

    }

    protected boolean showTitleBar() {
        return false;
    }
}
