package cn.krisez.car.ui.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import cn.krisez.car.R;
import cn.krisez.car._interface.AppBarStateChangeListener;
import cn.krisez.car.entity.VideoQuery;
import cn.krisez.car.utils.LandLayoutVideo;

public class VideoDetailActivity extends AppCompatActivity {
    private boolean isPlay;
    private boolean isPause;
    private boolean isSamll;

    private LandLayoutVideo mLandLayoutVideo;
    private OrientationUtils mOrientationUtils;

    private AppBarLayout appBar;
    private CollapsingToolbarLayout toolbarLayout;

    private AppBarStateChangeListener.State curState;

    private VideoQuery mVideoQuery;

    public static void intentTo(Context context, VideoQuery videoQuery){
        Intent intent = new Intent(context,VideoDetailActivity.class);
        intent.putExtra("video",videoQuery);
        context.startActivity(intent);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        appBar = findViewById(R.id.app_bar);

        toolbarLayout = findViewById(R.id.toolbar_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        appBar.addOnOffsetChangedListener(appBarStateChangeListener);

        mLandLayoutVideo = findViewById(R.id.detail_player);
        mVideoQuery = (VideoQuery) getIntent().getSerializableExtra("video");

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //Bitmap bitmap = DensityUtil.readBitMap(this, R.drawable.video_info);
        //imageView.setImageBitmap(bitmap);
        Glide.with(this).load(mVideoQuery.getThumb()).into(imageView);

        //增加title
        mLandLayoutVideo.getTitleTextView().setVisibility(View.GONE);
        mLandLayoutVideo.getBackButton().setVisibility(View.GONE);

        //外部辅助的旋转，帮助全屏
        mOrientationUtils = new OrientationUtils(this, mLandLayoutVideo);
        //初始化不打开外部的旋转
        mOrientationUtils.setEnable(false);

        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setThumbImageView(imageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setSeekRatio(1)
                .setUrl(mVideoQuery.getUrl())
                .setCacheWithPlay(false)
                .setVideoTitle(mVideoQuery.getAddr())
                .setVideoAllCallBack(new GSYSampleCallBack() {

                    @Override
                    public void onPrepared(String url, Object... objects) {
                        Debuger.printfError("***** onPrepared **** " + objects[0]);
                        Debuger.printfError("***** onPrepared **** " + objects[1]);
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        mOrientationUtils.setEnable(true);
                        isPlay = true;
                    }

                    @Override
                    public void onEnterFullscreen(String url, Object... objects) {
                        super.onEnterFullscreen(url, objects);
                        Debuger.printfError("***** onEnterFullscreen **** " + objects[0]);//title
                        Debuger.printfError("***** onEnterFullscreen **** " + objects[1]);//当前全屏player
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                    }

                    @Override
                    public void onClickStartError(String url, Object... objects) {
                        super.onClickStartError(url, objects);
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        Debuger.printfError("***** onQuitFullscreen **** " + objects[0]);//title
                        Debuger.printfError("***** onQuitFullscreen **** " + objects[1]);//当前非全屏player
                        if (mOrientationUtils != null) {
                            mOrientationUtils.backToProtVideo();
                        }
                    }
                })
                .setLockClickListener((view, lock) -> {
                    if (mOrientationUtils != null) {
                        //配合下方的onConfigurationChanged
                        mOrientationUtils.setEnable(!lock);
                    }
                })
                .setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> Debuger.printfLog(" progress " + progress + " secProgress " + secProgress + " currentPosition " + currentPosition + " duration " + duration))
                .build(mLandLayoutVideo);

        mLandLayoutVideo.getFullscreenButton().setOnClickListener(v -> {
            //直接横屏
            mOrientationUtils.resolveByClick();

            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            mLandLayoutVideo.startWindowFullscreen(VideoDetailActivity.this, true, true);
        });
        mLandLayoutVideo.setLinkScroll(true);
    }


    @Override
    public void onBackPressed() {

        if (mOrientationUtils != null) {
            mOrientationUtils.backToProtVideo();
        }

        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurPlay().onVideoResume();
        appBar.addOnOffsetChangedListener(appBarStateChangeListener);
        mLandLayoutVideo.setReleaseWhenLossAudio(true);
        isPause = false;
    }

    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            getCurPlay().release();
        }
        if (mOrientationUtils != null)
            mOrientationUtils.releaseListener();
        GSYVideoManager.releaseAllVideos();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            mLandLayoutVideo.onConfigurationChanged(this, newConfig, mOrientationUtils, true, true);
        }
    }

    private GSYVideoPlayer getCurPlay() {
        if (mLandLayoutVideo.getFullWindowPlayer() != null) {
            return mLandLayoutVideo.getFullWindowPlayer();
        }
        return mLandLayoutVideo;
    }

    AppBarStateChangeListener appBarStateChangeListener = new AppBarStateChangeListener() {
        @Override
        public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State
                state) {
            if (state == AppBarStateChangeListener.State.EXPANDED) {
                //展开状态
                curState = state;
                toolbarLayout.setTitle("");
            } else if (state == AppBarStateChangeListener.State.COLLAPSED) {
                //折叠状态
                //如果是小窗口就不需要处理
                toolbarLayout.setTitle(mVideoQuery.getAddr());
                if (!isSamll && isPlay) {
                    isSamll = true;
                    int size = CommonUtil.dip2px(VideoDetailActivity.this, 150);
                    mLandLayoutVideo.showSmallVideo(new Point(size, size), true, true);
                    mOrientationUtils.setEnable(false);
                }
                curState = state;
            } else {
                if (curState == AppBarStateChangeListener.State.COLLAPSED) {
                    //由折叠变为中间状态
                    toolbarLayout.setTitle("");
                    if (isSamll) {
                        isSamll = false;
                        mOrientationUtils.setEnable(true);
                        //必须
                        mLandLayoutVideo.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLandLayoutVideo.hideSmallVideo();
                            }
                        }, 50);
                    }
                }
                curState = state;
                //中间状态
            }
        }
    };
}
