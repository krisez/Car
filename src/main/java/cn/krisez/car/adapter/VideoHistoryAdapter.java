package cn.krisez.car.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.List;

import cn.krisez.car.R;
import cn.krisez.car.entity.VideoQuery;
import cn.krisez.car.utils.DensityUtil;
import cn.krisez.car.utils.SampleCoverVideo;

public class VideoHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_NOMORE = 3;

    private List<VideoQuery> mList;
    private Context mContext;

    private StandardGSYVideoPlayer curPlayer;
    private OrientationUtils orientationUtils;
    private boolean isPlay;
    private boolean isFull;


    public VideoHistoryAdapter(List<VideoQuery> list, Context context) {
        mList = list;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getListSize()
                && getListSize() > 0) {
            return TYPE_NORMAL;
        }
        if (noMore) {
            return TYPE_NOMORE;
        }
        return TYPE_FOOTER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_footer, parent, false);
            return new FooterHolder(v);
        } else if (viewType == TYPE_NORMAL) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_video_history, parent, false);
            return new VideoHolder(v);
        } else if (viewType == TYPE_NOMORE) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_no_more_footer, parent, false);
            return new FooterHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VideoHolder) {
            VideoQuery query = mList.get(position);
            ((VideoHolder) holder).player.loadCoverImage(query.getThumb(), R.drawable.video_info);

            ((VideoHolder) holder).time.setText(query.getTime());
            ((VideoHolder) holder).address.setText(query.getAddr());

            ((VideoHolder) holder).player.setUpLazy(query.getUrl(), true, null, null, "这是title");
            ((VideoHolder) holder).player.getTitleTextView().setVisibility(View.GONE);
            ((VideoHolder) holder).player.getBackButton().setVisibility(View.GONE);

            ((VideoHolder) holder).player.getFullscreenButton().setOnClickListener(v -> resolveFullBtn(((VideoHolder) holder).player));
            ((VideoHolder) holder).player.setRotateViewAuto(!getListNeedAutoLand());
            ((VideoHolder) holder).player.setLockLand(!getListNeedAutoLand());
            ((VideoHolder) holder).player.setPlayTag("VideoPlayer");
            ((VideoHolder) holder).player.setAutoFullWithSize(true);
            ((VideoHolder) holder).player.setReleaseWhenLossAudio(false);
            ((VideoHolder) holder).player.setShowFullAnimation(!getListNeedAutoLand());
            ((VideoHolder) holder).player.setIsTouchWiget(false);
            ((VideoHolder) holder).player.setNeedLockFull(true);
            ((VideoHolder) holder).player.setPlayPosition(position);
            ((VideoHolder) holder).player.setVideoAllCallBack(new GSYSampleCallBack() {
                @Override
                public void onClickStartIcon(String url, Object... objects) {
                    super.onClickStartIcon(url, objects);
                }

                @Override
                public void onPrepared(String url, Object... objects) {
                    super.onPrepared(url, objects);
                    Debuger.printfLog("onPrepared");
                    boolean full = ((VideoHolder) holder).player.getCurrentPlayer().isIfCurrentIsFullscreen();
                    if (!((VideoHolder) holder).player.getCurrentPlayer().isIfCurrentIsFullscreen()) {
                        GSYVideoManager.instance().setNeedMute(true);
                    }
                    curPlayer = (StandardGSYVideoPlayer) objects[1];
                    isPlay = true;
                    if (getListNeedAutoLand()) {
                        //重力全屏工具类
                        initOrientationUtils(((VideoHolder) holder).player, full);
                        VideoHistoryAdapter.this.onPrepared();
                    }
                }

                @Override
                public void onQuitFullscreen(String url, Object... objects) {
                    super.onQuitFullscreen(url, objects);
                    isFull = false;
                    GSYVideoManager.instance().setNeedMute(true);
                    if (getListNeedAutoLand()) {
                        VideoHistoryAdapter.this.onQuitFullscreen();
                    }
                }

                @Override
                public void onEnterFullscreen(String url, Object... objects) {
                    super.onEnterFullscreen(url, objects);
                    GSYVideoManager.instance().setNeedMute(false);
                    isFull = true;
                    ((VideoHolder) holder).player.getCurrentPlayer().getTitleTextView().setText(query.getAddr());
                }

                @Override
                public void onAutoComplete(String url, Object... objects) {
                    super.onAutoComplete(url, objects);
                    curPlayer = null;
                    isPlay = false;
                    isFull = false;
                    if (getListNeedAutoLand()) {
                        VideoHistoryAdapter.this.onAutoComplete();
                    }
                }

            });

            ((VideoHolder) holder).share.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE,"分享给你看个视频");
                intent.putExtra(Intent.EXTRA_TEXT,query.getUrl());
                intent = Intent.createChooser(intent,"share");
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (!noMore) {
            return mList.size() + (moreLoad ? 1 : 0);
        } else {
            return mList.size() + 1;
        }
    }

    private int getListSize() {
        return mList.size();
    }

    private boolean moreLoad = false;

    public void removeFooter() {
        moreLoad = false;
        notifyItemRemoved(mList.size());
    }

    public void addFooter() {
        moreLoad = true;
        notifyItemInserted(mList.size());
    }

    private boolean noMore = false;

    public void addNoMoreFooter() {
        noMore = true;
        notifyItemInserted(mList.size());
    }

    private void resolveFullBtn(final StandardGSYVideoPlayer standardGSYVideoPlayer) {
        if (getListNeedAutoLand() && orientationUtils != null) {
            resolveFull();
        }
        standardGSYVideoPlayer.startWindowFullscreen(mContext, false, true);
    }

    private void resolveFull() {
        if (getListNeedAutoLand() && orientationUtils != null) {
            //直接横屏
            orientationUtils.resolveByClick();
        }
    }

    private void initOrientationUtils(StandardGSYVideoPlayer standardGSYVideoPlayer, boolean full) {
        orientationUtils = new OrientationUtils((Activity) mContext, standardGSYVideoPlayer);
        //是否需要跟随系统旋转设置
        //orientationUtils.setRotateWithSystem(false);
        orientationUtils.setEnable(false);
        orientationUtils.setIsLand((full) ? 1 : 0);
    }

    public boolean getListNeedAutoLand() {
        return false;
    }

    public void refresh() {
        GSYVideoManager.releaseAllVideos();
    }

    private void onQuitFullscreen() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
    }

    public void onAutoComplete() {
        if (orientationUtils != null) {
            orientationUtils.setEnable(false);
            orientationUtils.releaseListener();
            orientationUtils = null;
        }
        isPlay = false;
    }

    public void onPrepared() {
        if (orientationUtils == null) {
            return;
        }
        //开始播放了才能旋转和全屏
        orientationUtils.setEnable(true);
    }

    public void onConfigurationChanged(Activity activity, Configuration newConfig) {
        //如果旋转了就全屏
        if (isPlay && curPlayer != null && orientationUtils != null) {
            curPlayer.onConfigurationChanged(activity, newConfig, orientationUtils, false, true);
        }
    }

    public OrientationUtils getOrientationUtils() {
        return orientationUtils;
    }


    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
    }

    public void onDestroy() {
        if (isPlay && curPlayer != null) {
            curPlayer.getCurrentPlayer().release();
        }
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
            orientationUtils = null;
        }
        if (curPlayer != null) {
            ((SampleCoverVideo) curPlayer).clear();
        }
    }

    class VideoHolder extends RecyclerView.ViewHolder {

        private TextView time;
        private ImageView share;
        private SampleCoverVideo player;
        private TextView address;

        VideoHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.item_video_time);
            share = itemView.findViewById(R.id.item_video_share);
            player = itemView.findViewById(R.id.item_video_list_player);
            address = itemView.findViewById(R.id.item_video_address);
        }
    }

    class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(View itemView) {
            super(itemView);
        }
    }
}
