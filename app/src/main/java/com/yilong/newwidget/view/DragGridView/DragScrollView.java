package com.yilong.newwidget.view.DragGridView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;

/**
 * @author leichenrd
 * @date 2019/7/12.
 * @desc
 */
public class DragScrollView extends ScrollView {

    /**
     * 是否可以拖拽，默认不可以
     */
    private boolean isDrag = false;
    /**
     * DragGridView自动滚动的速度
     */
    private static final int speed = 20;
    /**
     * DragGridView自动向下滚动的边界值
     */
    private int mDownScrollBorder;

    /**
     * DragGridView自动向上滚动的边界值
     */
    private int mUpScrollBorder;

    /**
     * 震动器
     */
    private Vibrator mVibrator;
    /**
     * item镜像的布局参数
     */
    private WindowManager.LayoutParams mWindowLayoutParams;
    private WindowManager mWindowManager;
    private int touchMoveX, touchMoveY;
    ImageView mDragImageView;
    private Handler mHandler = new Handler();
    private Context mContext;

    public DragScrollView(Context context) {
        super(context);
    }

    public DragScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

    }

    public void createDragView(Bitmap mDragBitmap, int mDownX, int mDownY) {
        isDrag = true; //设置可以拖拽
        mVibrator.vibrate(50); //震动一下
        //根据我们按下的点显示item镜像
        createDragImage(mDragBitmap, mDownX, mDownY);
    }

    /**
     * 创建拖动的镜像
     *
     * @param bitmap
     * @param downX  按下的点相对父控件的X坐标
     * @param downY  按下的点相对父控件的X坐标
     */
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = downX;
        mWindowLayoutParams.y = downY;
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragImageView = new ImageView(mContext.getApplicationContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }


    public void hindDragView() {
        isDrag = false;
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
                mDownScrollBorder = getHeight() / 5;
                //获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
                mUpScrollBorder = getHeight() * 4 / 5;
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mScrollRunnable);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (isDrag && mDragImageView != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    touchMoveX = (int) ev.getX();
                    touchMoveY = (int) ev.getY();
                    //拖动item
                    onDragItem(touchMoveX, touchMoveY);
                    break;
                case MotionEvent.ACTION_UP:
                    hindDragView();
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                hindDragView();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动
     * 当moveY的值小于向下滚动的边界值，触发GridView自动向下滚动
     * 否则不进行滚动
     */
    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            int scrollY;
            if (touchMoveY > mUpScrollBorder) {
                scrollY = speed;
                mHandler.postDelayed(mScrollRunnable, 800);
            } else if (touchMoveY < mDownScrollBorder) {
                scrollY = -speed;
                mHandler.postDelayed(mScrollRunnable, 800);
            } else {
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }
            smoothScrollBy(0, scrollY);
        }
    };

    /**
     * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
     *
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY) {
        mWindowLayoutParams.x = moveX;
        mWindowLayoutParams.y = moveY;
        mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新镜像的位置
//        onSwapItem(moveX, moveY);

        //ScrollView自动滚动
        mHandler.post(mScrollRunnable);
    }


}
