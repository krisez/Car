package cn.krisez.car.video;

import android.view.View;

import cn.krisez.car.R;
import cn.krisez.car.base.BaseActivity;
import cn.krisez.car.presenter.Presenter;
import cn.krisez.car.presenter.VideoPresenter;

public class VideoListActivity extends BaseActivity{

    private VideoPresenter mPresenter;

    @Override
    protected View newView() {
        return View.inflate(this, R.layout.activity_video_list,null);
    }

    @Override
    protected Presenter presenter() {
        return null;
    }

    @Override
    protected void init() {

    }

    @Override
    protected String type() {
        return "video";
    }

    @Override
    public void onRefresh() {

    }
}
