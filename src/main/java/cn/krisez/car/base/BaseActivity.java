package cn.krisez.car.base;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Objects;

import cn.krisez.car.R;
import cn.krisez.car.presenter.Presenter;
import cn.krisez.car.ui.trace.IView;
import cn.krisez.car.widget.RefreshView;

public abstract class BaseActivity extends AppCompatActivity implements RefreshView.RefreshListener, IView {

    public RefreshView mRefreshView;
    private Presenter mPresenter;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        mRefreshView = findViewById(R.id.base_layout);
        View view = newView();
        assert view != null;
        mRefreshView.addView(view);

        mRefreshView.refreshing();
        mRefreshView.setRefreshListener(this);
        mRefreshView.setRefreshMaxHeight(mRefreshView.dp(40));
        mRefreshView.setRefreshViewHeight(mRefreshView.dp(40));

        mPresenter = presenter();
        if (mPresenter != null) {
            mPresenter.onCreate();
        }
        init(savedInstanceState);

        TextView textView = findViewById(R.id.tv_filter);
        textView.setOnClickListener(this::filterClick);
    }

    protected abstract View newView();

    protected abstract Presenter presenter();

    protected abstract void init(Bundle bundle);

    protected abstract void filterClick(View v);

    public int barBottomY(){
        return Objects.requireNonNull(getSupportActionBar()).getHeight()+getStatusBarHeight();

    }

    public void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            Log.d("asdasd", "get status bar height fail");
            e1.printStackTrace();
            return 75;
        }
    }

    /**
     * 1，2 分别是trace & video
     * maybe 还有更多
     *
     * @return
     */
    protected abstract String type();

    @Override
    public void error(String s) {
        toast(s);
        if (mRefreshView.getRefreshState() == RefreshView.REFRESHING) {
            mRefreshView.finishRefresh(false);
        }
    }

    protected boolean compare(String s, String p, int o) {
        String[] ss = s.split("/");
        String[] ll = p.split("-");
        switch (o) {
            //  >=
            case 1:
                if (Integer.parseInt(ss[0]) == Integer.parseInt(ll[0])) {
                    if (Integer.parseInt(ss[1]) == Integer.parseInt(ll[1])){
                        return Integer.parseInt(ss[2]) > Integer.parseInt(ll[2]);
                    }else return Integer.parseInt(ss[1]) > Integer.parseInt(ll[1]);
                } else return Integer.parseInt(ss[0]) > Integer.parseInt(ll[0]);

                // <=
            case 2:
                if (Integer.parseInt(ss[0]) == Integer.parseInt(ll[0])) {
                    if (Integer.parseInt(ss[1]) == Integer.parseInt(ll[1])){
                        return Integer.parseInt(ss[2]) < Integer.parseInt(ll[2]);
                    }else return Integer.parseInt(ss[1]) < Integer.parseInt(ll[1]);
                } else return Integer.parseInt(ss[0]) < Integer.parseInt(ll[0]);
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }
}
