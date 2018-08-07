package cn.krisez.car.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.krisez.car.R;
import cn.krisez.car.enevt.TraceEvent;
import cn.krisez.car.entity.TraceQuery;
import cn.krisez.car.utils.Const;

public class TraceHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TraceQuery> mList;
    private Context mContext;

    public TraceHistoryAdapter(List<TraceQuery> list, Context context) {
        mList = list;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_trace_history, parent, false);
        return new TraceHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TraceQuery traceQuery = mList.get(position);
        ((TraceHolder)holder).start.setText(traceQuery.getStart());
        ((TraceHolder)holder).end.setText(traceQuery.getEnd());
        ((TraceHolder)holder).time.setText(traceQuery.getTime());
        ((TraceHolder)holder).distance.setText(traceQuery.getDistance());

        ((TraceHolder)holder).play.setOnClickListener(v->{
            EventBus.getDefault().post(new TraceEvent(traceQuery.getId()));
            ((Activity)mContext).finish();
        });
        ((TraceHolder)holder).videoHistory.setOnClickListener(v->{
            mContext.startActivity(new Intent(Const.ACTION_VIDEO_LIST)
                    .addCategory(Const.CATEGORY_VIDEO_HISTORY_TRACE_ID)
                    .putExtra("trace_id",traceQuery.getId()));
        });
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }

    class TraceHolder extends RecyclerView.ViewHolder {

        private TextView start;
        private TextView end;
        private TextView time;
        private TextView distance;
        private Button play;
        private Button videoHistory;

        TraceHolder(View itemView) {
            super(itemView);

            start = itemView.findViewById(R.id.item_trace_start);
            end = itemView.findViewById(R.id.item_trace_end);
            time = itemView.findViewById(R.id.item_trace_time);
            distance = itemView.findViewById(R.id.item_trace_distrance);
            play = itemView.findViewById(R.id.item_trace_animator_start);
            videoHistory = itemView.findViewById(R.id.item_trace_video_looking);


        }
    }
}
