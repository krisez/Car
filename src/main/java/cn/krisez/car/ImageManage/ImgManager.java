package cn.krisez.car.ImageManage;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

public class ImgManager {
    private static ImgManager sManager;
    private RequestManager mRequestManager;
    private Context mContext;
    private Activity mActivity;
    private Fragment mFragment;
    private View mView;
    private RequestBuilder<Drawable> mRequestBuilder;

    private ImgManager() {
    }

    public static ImgManager INSTANCE() {
        if (sManager == null) {
            synchronized (ImgManager.class) {
                if (sManager == null) {
                    sManager = new ImgManager();
                }
            }
        }
        return sManager;
    }

    /**
     * 基本使用
     * view,context,activity,fragment,fragmentActivity
     *
     * @Glide.with(context).load(url).into(target);
     */
    public ImgManager bind(Context context) {
        this.mContext = context;
        mRequestManager = Glide.with(context);
        return this;
    }

    public ImgManager bind(Activity activity) {
        this.mActivity = activity;
        mRequestManager = Glide.with(activity);
        return this;
    }

    public ImgManager bind(View view) {
        this.mView = view;
        mRequestManager = Glide.with(view);
        return this;
    }

    public ImgManager bind(Fragment fragment) {
        this.mFragment = fragment;
        mRequestManager = Glide.with(fragment);
        return this;
    }

    public ImgManager load(String url) {
        mRequestBuilder = mRequestManager.load(url);
        return this;
    }

    public void into(ImageView img) {
        mRequestBuilder.into(img);
        destroy();
    }

    private void destroy() {
        mRequestManager = null;
        mContext = null;
        mActivity = null;
        mFragment = null;
        mView = null;
        mRequestBuilder = null;
    }

}