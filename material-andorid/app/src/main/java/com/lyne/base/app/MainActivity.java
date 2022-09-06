package com.lyne.base.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.lyne.base.R;
import com.lyne.base.function.home.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView mNavView;
    private View mActivity_side;
    DrawerLayout mDrawer;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNav();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_side_fragment);
        //获取子fragemnt中当前的活动的fragement
        Fragment primaryNavigationFragment = fragment.getChildFragmentManager().getPrimaryNavigationFragment();
        //实现活动的fragent中的onActivityResult的方法
        primaryNavigationFragment.onActivityResult(requestCode, resultCode, data);

    }

    //初始化导航
    private void initNav() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        //侧边导航
        mNavigationView = findViewById(R.id.main_left);
        //底部导航
        mNavView = findViewById(R.id.nav_view);
        mAppBarConfiguration =
                new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_mine, R.id.nav_order)
                        .setOpenableLayout(mDrawer)
                        .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_side_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(mNavView, navController);
            NavigationUI.setupWithNavController(mNavigationView, navController);
            NavigationUI.setupActionBarWithNavController(this,
                    navController, mAppBarConfiguration);
        }
        View main = findViewById(R.id.main_right);
        mDrawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                int width = drawerView.getWidth();
                main.setTranslationX(width * slideOffset);
            }
        });
    }

    //返回设置
    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}