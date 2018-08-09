package cn.krisez.car.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import cn.krisez.car.R;

public class SpreadView extends ConstraintLayout {

    //左上
    public static final int LEFT_TOP = 1;
    //左下
    public static final int LEFT_BOTTOM = 2;
    //右上
    public static final int RIGHT_TOP = 3;
    //右下
    public static final int RIGHT_BOTTOM = 4;

    private View mContentView;
    private Context mContext;
    //绘制进度-----[0,1]
    private float rate;

    private int width;
    private int height;
    private double totalRadius;
    private int startPosition = RIGHT_TOP;

    public SpreadView(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public SpreadView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpreadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.DKGRAY);
        //setBackgroundColor(Color.parseColor("#00ffffff"));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);


        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        //记录如果是wrap_content是设置的宽和高
        width = 0;
        height = 0;

        int cCount = getChildCount();

        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;

        // 用于计算左边两个childView的高度
        int lHeight = 0;
        // 用于计算右边两个childView的高度，最终高度取二者之间大值
        int rHeight = 0;

        // 用于计算上边两个childView的宽度
        int tWidth = 0;
        // 用于计算下面两个childiew的宽度，最终宽度取二者之间大值
        int bWidth = 0;

        /**
         * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
         */
        for (int i = 0; i < cCount; i++) {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            cParams = (MarginLayoutParams) childView.getLayoutParams();

            // 上面两个childView
            if (i == 0 || i == 1) {
                tWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
            }

            if (i == 2 || i == 3) {
                bWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
            }

            if (i == 0 || i == 2) {
                lHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }

            if (i == 1 || i == 3) {
                rHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }

        }

        width = Math.max(tWidth, bWidth);
        height = Math.max(lHeight, rHeight);

        /**
         * 如果是wrap_content设置为我们计算的值
         * 否则：直接设置为父容器计算的值
         */
        int w = widthMode == MeasureSpec.EXACTLY ? sizeWidth : width;
        int h = heightMode == MeasureSpec.EXACTLY ? sizeHeight : height;
        totalRadius = Math.sqrt(w * w + h * h);
        setMeasuredDimension(w, h);
        width = w;
        height = h;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getChildAt(0).setVisibility(INVISIBLE);
    }

    /**
     * 仿造
     *
     * @param canvas
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rate > 0 && rate < 1) {
            drawNow(new Paint(), canvas);
        } else if (rate == 1f) {
            setBackgroundColor(Color.DKGRAY);
            getChildAt(0).setVisibility(VISIBLE);
        }
    }

    public void drawNow(Paint paint, Canvas canvas) {
        int w = 0;
        int h = 0;
        switch (getStartPosition()) {
            case LEFT_TOP:
                break;
            case LEFT_BOTTOM:
                h = height;
                break;
            case RIGHT_TOP:
                w = width;
                break;
            case RIGHT_BOTTOM:
                w = width;
                h = height;
                break;
        }
        paint.reset();
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);
        canvas.drawCircle(w, h, (float) (totalRadius * rate), paint);
    }

    public void setRate(float rate) {
        this.rate = rate;
        invalidate();
    }

    public float getRate() {
        return rate;
    }

    public void setContentView(View v) {
        this.mContentView = v;
    }

    public View getContentView() {
        return mContentView;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

}
