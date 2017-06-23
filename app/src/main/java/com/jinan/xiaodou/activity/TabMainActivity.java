package com.jinan.xiaodou.activity;

import android.Manifest;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.jinan.xiaodou.ActivityTaskManager;
import com.jinan.xiaodou.BaseActivity;
import com.jinan.xiaodou.R;
import com.jovision.xunwei.junior.lib.util.ToastUtils;

import static com.jinan.xiaodou.util.Contants.RequestCode.存储权限;


@SuppressWarnings("deprecation")
public class TabMainActivity extends BaseActivity implements
        OnCheckedChangeListener {

    private TabHost mTabHost;
    private RadioGroup mRadioGroup;
    private LocalActivityManager mActivityManager;

    private Bundle mBundle;

    public static final String TAG = "tag";
    public static final String 申请审批 = "申请审批";
    public static final String 农残检测 = "农残检测";
    public static final String 投入品监管 = "投入品监管";
    public static final String 基地管理 = "基地管理";
    public static final String[] TAGS = new String[]{申请审批, 农残检测, 投入品监管, 基地管理};
    public static String tagInvokeStartActivty = null;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_main);
        mBundle = new Bundle();
        // 获取TabHost对象
        mTabHost = (TabHost) findViewById(R.id.tabhost);
        mActivityManager = new LocalActivityManager(this, false);
        mActivityManager.dispatchCreate(savedInstanceState);
        // 如果没有继承TabActivity时，通过该种方法加载启动tabHost
        mTabHost.setup(mActivityManager);
        mRadioGroup = (RadioGroup) this.findViewById(R.id.tab_radiogroup);
        mRadioGroup.setOnCheckedChangeListener(this);
        addTabs();
        openStoragePermission();

    }


    /**
     * 为tabHost添加各个标签项
     */
    private void addTabs() {
        addTab(申请审批, NewsActivity.class, mBundle);
        addTab(农残检测, MainActivity.class, mBundle);
        addTab(投入品监管, MainActivity.class, mBundle);
        addTab(基地管理, MainActivity.class, mBundle);
    }

    private void addTab(String tag, Class<? extends Activity> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(TAG, tag);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        mTabHost.addTab(this.buildTagSpec(tag, R.string.k_null, R.drawable.ic_launcher, intent));
    }

    /**
     * 自定义创建标签项的方法
     *
     * @param tagName  标签标识
     * @param tagLable 标签文字
     * @param icon     标签图标
     * @param content  标签对应的内容
     * @return
     */
    private TabHost.TabSpec buildTagSpec(String tagName, int tagLable,
                                         int icon, Intent content) {
        return mTabHost
                .newTabSpec(tagName)
                .setIndicator(getResources().getString(tagLable),
                        getResources().getDrawable(icon)).setContent(content);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // 改变字体颜色
        int childCount = mRadioGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioButton button = (RadioButton) mRadioGroup.getChildAt(i);
            if (button.isChecked()) {
                button.setTextColor(getResources().getColor(R.color.main_color));
            } else {
                button.setTextColor(getResources().getColor(R.color.gray));
            }
        }
        //获取当前选中的tag
        String checkedTag = null;
        switch (checkedId) {
            case R.id.申请审批:
                checkedTag = 申请审批;
                break;
            case R.id.农残检测:
                checkedTag = 农残检测;
                break;
            case R.id.投入品监管:
                checkedTag = 投入品监管;
                break;
            case R.id.基地管理:
                checkedTag = 基地管理;
                break;
        }
        mTabHost.setCurrentTabByTag(checkedTag);
        //回调
        for (String tag : TAGS) {
            Activity activity = mActivityManager.getActivity(tag);

            if (!(activity instanceof TabActivity)) {
                continue;
            }
            if (checkedTag.equals(tag)) {
                ((TabActivity) activity).onTabResume();
            } else {
                ((TabActivity) activity).onTabPause();
            }
        }
    }

    protected boolean showTitleBar() {
        return false;
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.show(this, R.string.app_exit);
            exitTime = System.currentTimeMillis();
        } else {
            ActivityTaskManager.getInstance().closeAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 获取当前活动的Activity实例
        // Activity subActivity = mActivityManager.getCurrentActivity();//  获取不到
        Activity subActivity = mActivityManager.getActivity(tagInvokeStartActivty);
        if (subActivity instanceof TabActivity) {
            TabActivity tabActivity = (TabActivity) subActivity;
            tabActivity.onTabActivityResult(requestCode, resultCode, data);
        }
    }

    private void openStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 存储权限);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 存储权限);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 存储权限:
                    break;
            }
        } else {
            switch (requestCode) {
                case 存储权限:
                    ToastUtils.show(this, "禁止存储权限将导致app更新失败，请到设置里面开启存储权限");
                    break;
            }
        }
    }

}
