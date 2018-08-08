package cn.krisez.car.ui.video;

import android.content.res.Configuration;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.ArrayList;
import java.util.List;

import cn.krisez.car.R;
import cn.krisez.car.adapter.VideoHistoryAdapter;
import cn.krisez.car.base.BaseActivity;
import cn.krisez.car.entity.VideoQuery;
import cn.krisez.car.presenter.Presenter;
import cn.krisez.car.presenter.VideoPresenter;
import cn.krisez.car.widget.DividerDecoration;
import cn.krisez.car.widget.RefreshView;

public class VideoListActivity extends BaseActivity implements IVideoView {

    private VideoPresenter mPresenter;
    private List<VideoQuery> mList = new ArrayList<>();
    private VideoHistoryAdapter mAdapter;

    private boolean isPause;
    private boolean isLoading = false;
    private int pager = 1;


    @Override
    protected View newView() {
        return View.inflate(this, R.layout.activity_video_list, null);
    }

    @Override
    protected Presenter presenter() {
        return mPresenter = new VideoPresenter(this, this);
    }

    @Override
    protected void init() {
        mAdapter = new VideoHistoryAdapter(mList, this);
        RecyclerView recyclerView = findViewById(R.id.rv_video_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerDecoration(this, DividerDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);

        mPresenter.attachIncomingIntent(getIntent());
        mPresenter.getVideoList(pager++, false);
        Log.d("VideoListActivity", "init:" + pager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(isMore) {
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int lastVisiableItemPosition = manager.findLastVisibleItemPosition();
                    if (lastVisiableItemPosition + 1 == mList.size()) {
                        if (!isLoading) {
                            isLoading = true;
                            mPresenter.getVideoList(pager++, true);
                            mAdapter.addFooter();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mRefreshView.setRefreshListener(() -> {
            mAdapter.refresh();
            mPresenter.getVideoList(pager = 1, false);
            pager++;
        });

    }

    @Override
    protected void filterClick(View v) {

    }

    @Override
    protected String type() {
        return "video";
    }

    @Override
    public void onRefresh() {
        mAdapter.refresh();
        mPresenter.getVideoList(pager = 1, false);
        pager++;
    }

    @Override
    public void update(List<VideoQuery> list) {
        if (mRefreshView.getRefreshState() == RefreshView.REFRESHING) {
            mList.removeAll(mList);
            mList.addAll(list);
            mAdapter.notifyDataSetChanged();
            mRefreshView.finishRefresh(true);
            isMore = true;
            count = list.size();
        }
    }

    private int count;
    private boolean isMore;

    @Override
    public void skip(List<VideoQuery> next) {
        if (next.size() != 0) {
            mList.addAll(next);
            mAdapter.notifyItemRangeInserted(count, next.size());
            count += next.size() - 1;
        } else {
            if (isMore)
                error("没有更多...........");
            isMore = false;
        }
        mAdapter.removeFooter();
        isLoading = false;


    }

    @Override
    public void onBackPressed() {
        //为了支持重力旋转
        onBackPressAdapter();

        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (mAdapter != null) {
            mAdapter.onDestroy();
        }
    }

    /********************************为了支持重力旋转********************************/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mAdapter != null && mAdapter.getListNeedAutoLand() && !isPause) {
            mAdapter.onConfigurationChanged(this, newConfig);
        }
    }

    private void onBackPressAdapter() {
        //为了支持重力旋转
        if (mAdapter != null && mAdapter.getListNeedAutoLand()) {
            mAdapter.onBackPressed();
        }
    }
}
