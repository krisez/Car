package cn.krisez.car.ui.video;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.krisez.car.R;
import cn.krisez.car.adapter.VideoHistoryAdapter;
import cn.krisez.car.base.BaseActivity;
import cn.krisez.car.entity.VideoQuery;
import cn.krisez.car.presenter.Presenter;
import cn.krisez.car.presenter.VideoPresenter;
import cn.krisez.car.utils.DensityUtil;
import cn.krisez.car.widget.DividerDecoration;
import cn.krisez.car.widget.RefreshView;
import cn.krisez.car.widget.SpreadView;

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
    protected void init(Bundle bundle) {
        mAdapter = new VideoHistoryAdapter(mList, this);
        RecyclerView recyclerView = findViewById(R.id.rv_video_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerDecoration(this, DividerDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);

        mPresenter.attachIncomingIntent(getIntent());
        mPresenter.getVideoList(pager++, false);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isMore) {
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

        if (bundle != null) {
            s = bundle.getString("s");
            e = bundle.getString("e");
        }

    }

    protected PopupWindow mPopupWindow;

    @Override
    protected void filterClick(View v) {
        SpreadView layout = (SpreadView) getLayoutInflater().inflate(R.layout.popwindow, null);
        initLayout(layout);
        mPopupWindow = new PopupWindow(layout,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //点击空白处时，隐藏掉pop窗口
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources()));


        mPopupWindow.showAtLocation(v, Gravity.TOP, 0, barBottomY());
        ObjectAnimator animator = ObjectAnimator.ofFloat(layout, "rate", 0f, 1f);
        animator.setDuration(500);
        animator.start();
    }

    private String s, e;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initLayout(SpreadView layout) {
        TextView start = layout.findViewById(R.id.pop_tv_start);
        TextView end = layout.findViewById(R.id.pop_tv_end);
        Calendar calendar = Calendar.getInstance();
        if (s == null)
            start.setText(DensityUtil.strDate(calendar));
        else start.setText(s);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        if (e == null)
            end.setText(DensityUtil.strDate(calendar));
        else end.setText(e);

        start.setOnClickListener(v1 -> {
            DatePickerDialog dialog = new DatePickerDialog(this, android.R.style.Theme_Material_Dialog_Alert, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    s = year + "/" + (month + 1) + "/" + dayOfMonth;
                    start.setText(s);
                }
            }, 2018, 8 - 1, 9);
            dialog.show();
        });
        end.setOnClickListener(v1 -> {
            DatePickerDialog dialog = new DatePickerDialog(this, android.R.style.Theme_Material_Dialog_Alert, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    e = year + "/" + (month + 1) + "/" + dayOfMonth;
                    end.setText(e);
                }
            }, 2018, 8 - 1, 31);
            dialog.show();
        });

        layout.findViewById(R.id.pop_reset).setOnClickListener(v -> {
            mAdapter.refresh();
            mPresenter.getVideoList(pager = 1, false);
            pager++;
            mPopupWindow.dismiss();
        });
        layout.findViewById(R.id.pop_sure).setOnClickListener(v -> {
            filterOp(start.getText().toString(), end.getText().toString());
        });

        TextView distance = layout.findViewById(R.id.pop_tv_set_dis);
        distance.setOnClickListener(v -> {

        });
    }

    private void filterOp(String s, String e) {
        Map<Integer, VideoQuery> map = new HashMap<>();
        int i = 0;
        for (VideoQuery query : mList) {
            if (compare(s, query.getTime().split(" ")[0], 1)) {
                map.put(i++, query);
            }
            if (compare(e, query.getTime().split(" ")[0], 2)) {
                map.put(i++, query);
            }
        }
        for (i = 0; i < map.size(); i++) {
            mList.remove(map.get(i));
        }
        mAdapter.notifyDataSetChanged();
        mPopupWindow.dismiss();
        //有点问题
       /* new Handler().postDelayed(() -> {
            mList.removeAll(mList);
            mList.addAll(temp);
        }, 500);*/
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("s", s);
        outState.putString("e", e);
        super.onSaveInstanceState(outState);
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
