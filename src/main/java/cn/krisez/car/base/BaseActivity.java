package cn.krisez.car.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.krisez.car.R;
import cn.krisez.car.presenter.Presenter;
import cn.krisez.car.ui.trace.IView;
import cn.krisez.car.widget.QQRefreshView;

public abstract class BaseActivity extends AppCompatActivity implements QQRefreshView.RefreshListener, IView {

    public QQRefreshView mRefreshView;
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

        mRefreshView.setRefreshState(QQRefreshView.REFRESHING);
        mRefreshView.setRefreshListener(this);

        mPresenter = presenter();
        if (mPresenter != null) {
            mPresenter.onCreate();
        }
        init();

        TextView textView = findViewById(R.id.tv_filter);
        textView.setOnClickListener(this::filterClick);
    }

    protected abstract View newView();

    protected abstract Presenter presenter();

    protected abstract void init();

    protected abstract void filterClick(View v);

    public void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
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
        if (mRefreshView.getRefreshState()==QQRefreshView.REFRESHING) {
            mRefreshView.finishRefresh(false);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }
}
