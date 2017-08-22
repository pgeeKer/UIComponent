package com.uicomponents.framework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.uicomponents.framework.R;
import com.uicomponents.framework.fragment.BaseFragment;

/**
 * Activity 的基类
 *
 * <p>
 * - activity 不做任何的业务和视图展示逻辑，activity 只负责装载 Fragment 这一件事
 * - 子类Activity 需要调用 initFragment 方法，以及对外提供一些 launch 方法，供跳转调用即可
 * </p>
 *
 * @author panxiangxing
 * @data 17/8/21
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected BaseFragment fragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        fragment = initFragment();
        replaceFragment(fragment);
    }

    /**
     * 初始化 fragment
     *
     * 例：Fragment.instantiate(this, xxFragment.class.getName());
     */
    protected abstract BaseFragment initFragment();

    protected int getLayoutId() {
        return R.layout.activity_framework_base;
    }

    protected void replaceFragment(Fragment newFragment) {
        replaceFragment(newFragment, null, false);
    }

    protected void replaceFragment(Fragment newFragment, Bundle arguments, boolean isAddStack) {
        if (isFinishing()) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (arguments != null) {
            newFragment.setArguments(arguments);
        }
        transaction.replace(R.id.layout_fragment_container, newFragment);
        if (isAddStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fragment != null) {
            if (fragment.onKeyDown(keyCode, event)) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (fragment != null) {
            fragment.onNewIntent(intent);
        }
    }
}
