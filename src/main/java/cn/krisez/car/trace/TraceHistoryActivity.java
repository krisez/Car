package cn.krisez.car.trace;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.krisez.car.R;
import cn.krisez.car.adapter.TraceHistoryAdapter;
import cn.krisez.car.base.BaseActivity;
import cn.krisez.car.entity.TraceQuery;
import cn.krisez.car.presenter.Presenter;
import cn.krisez.car.presenter.TracePresenter;
import cn.krisez.car.widget.DividerDecoration;

public class TraceHistoryActivity extends BaseActivity implements ITraceView {
    @Override
    protected View newView() {
        return View.inflate(this, R.layout.activity_trace_history, null);
    }

    @Override
    protected Presenter presenter() {
        return mTracePresenter = new TracePresenter(this,this);
    }

    private RecyclerView mRecyclerView;
    private List<TraceQuery> mList = new ArrayList<>();
    private TracePresenter mTracePresenter;
    private TraceHistoryAdapter mAdapter;

    @Override
    protected void init() {
        mRecyclerView = findViewById(R.id.rv_trace_history);
        mAdapter = new TraceHistoryAdapter(mList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerDecoration(this, DividerDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mAdapter);
        mTracePresenter.getTraceList();
    }

    @Override
    protected String type() {
        return "trace";
    }

    @Override
    public void onRefresh() {
        mTracePresenter.getTraceList();
    }

    @Override
    public void update(List<TraceQuery> list) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mList.removeAll(mList);
            mList.addAll(list);
            mAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
