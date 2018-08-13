package cn.krisez.car.ui.trace;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.krisez.car.R;
import cn.krisez.car.adapter.TraceHistoryAdapter;
import cn.krisez.car.base.BaseActivity;
import cn.krisez.car.entity.TraceQuery;
import cn.krisez.car.presenter.Presenter;
import cn.krisez.car.presenter.TracePresenter;
import cn.krisez.car.utils.DensityUtil;
import cn.krisez.car.widget.RefreshView;
import cn.krisez.car.widget.SpreadView;

public class TraceHistoryActivity extends BaseActivity implements ITraceView {
    @Override
    protected View newView() {
        return View.inflate(this, R.layout.activity_trace_history, null);
    }

    @Override
    protected Presenter presenter() {
        return mTracePresenter = new TracePresenter(this, this);
    }

    private RecyclerView mRecyclerView;
    private List<TraceQuery> mList = new ArrayList<>();
    private TracePresenter mTracePresenter;
    private TraceHistoryAdapter mAdapter;

    @Override
    protected void init(Bundle bundle) {
        mRecyclerView = findViewById(R.id.rv_trace_history);
        mAdapter = new TraceHistoryAdapter(mList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mTracePresenter.getTraceList();

        if(bundle!=null){
            s = bundle.getString("s");
            e = bundle.getString("e");
        }
    }

    private PopupWindow mPopupWindow;

    @Override
    protected void filterClick(View v) {
        //TODO:根据距离、时间筛选
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

    private String s;
    private String e;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initLayout(SpreadView layout) {
        TextView start = layout.findViewById(R.id.pop_tv_start);
        TextView end = layout.findViewById(R.id.pop_tv_end);
        Calendar calendar = Calendar.getInstance();
        if(s==null)
            start.setText(DensityUtil.strDate(calendar));
        else start.setText(s);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        if(e==null)
            end.setText(DensityUtil.strDate(calendar));
        else end.setText(e);

        start.setOnClickListener(v1 -> {
            DatePickerDialog dialog = new DatePickerDialog(this, android.R.style.Theme_Material_Dialog_Alert, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    s = year + "/" + (month+1) + "/" + dayOfMonth;
                    start.setText(s);
                }
            }, 2018, 8 - 1, 9);
            dialog.show();
        });
        end.setOnClickListener(v1 -> {
            DatePickerDialog dialog = new DatePickerDialog(this, android.R.style.Theme_Material_Dialog_Alert, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    e = year + "/" + (month+1) + "/" + dayOfMonth;
                    end.setText(e);
                }
            }, 2018, 8 - 1, 31);
            dialog.show();
        });

        layout.findViewById(R.id.pop_reset).setOnClickListener(v -> {
            mRefreshView.refreshing();
            mTracePresenter.getTraceList();
            mPopupWindow.dismiss();
        });
        layout.findViewById(R.id.pop_sure).setOnClickListener(v -> filterOp(start.getText().toString(),end.getText().toString()));

        TextView textView = layout.findViewById(R.id.pop_tv_distance);
        String dd = textView.getText().toString() + "(暂不可用)";
        textView.setText(dd);
        TextView distance = layout.findViewById(R.id.pop_tv_set_dis);
        distance.setOnClickListener(v->{
            int d = Integer.parseInt(distance.getText().toString()) + 10;
            if(d>100) d = 0;
            distance.setText("" + d);
        });
    }

    private void filterOp(String s, String e) {
        Map<Integer, TraceQuery> map = new HashMap<>();
        int i = 0;
        for (TraceQuery query : mList) {
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
        return "trace";
    }

    @Override
    public void onRefresh() {
        mTracePresenter.getTraceList();
    }

    @Override
    public void update(List<TraceQuery> list) {
        if (mRefreshView.getRefreshState() == RefreshView.REFRESHING) {
            mList.removeAll(mList);
            mList.addAll(list);
            mAdapter.notifyDataSetChanged();
            mRefreshView.finishRefresh(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("s",s);
        outState.putString("e",e);
        super.onSaveInstanceState(outState);
    }
}
