package cn.krisez.car.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import cn.krisez.car.R;
import cn.krisez.car._interface.BackHandleInterface;
import cn.krisez.car.frag.AboutAppFragment;
import cn.krisez.car.frag.SettingFragment;
import cn.krisez.car.utils.FManager;

public class LoadFragment extends AppCompatActivity implements BackHandleInterface{
    String cls;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_fragment);

        Intent intent = getIntent();
        cls = intent.getStringExtra("cls");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(cls);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        switch (cls) {
            case "set":
                FManager.fmReplace(getSupportFragmentManager(), new SettingFragment(), R.id.load_fragment);
                break;
            case "about":
                //mAboutAppFragment = new AboutAppFragment();
                FManager.fmReplace(getSupportFragmentManager(),new AboutAppFragment(), R.id.load_fragment);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(backHandleFragment == null || !backHandleFragment.onBackPressed()){
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                super.onBackPressed();
            }else{
                getSupportFragmentManager().popBackStack();
            }
        }
    }
    private BackHandleFragment backHandleFragment;

    @Override
    public void onSelectedFragment(BackHandleFragment backHandleFragment) {
        this.backHandleFragment = backHandleFragment;
    }
}
