package com.yilong.newwidget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author leichenrd
 * @date 2019/7/11.
 * @desc 自定义滑动widgetview
 */
public class CustomWidgetView extends FrameLayout implements AdapterView.OnItemClickListener {

    private Context mContext;
    private GridView gridView;
    private int[] unselectedImageId;
    private int[] selectedImageId;
    private ArrayList<WidgetInfo> widgetInfos = new ArrayList<>();
    private MyAdapter myAdapter;
    private int selectPosition = -1;

    private Scroller mScroller;
    private View middleAndBottom, topView, tip;

    /**
     * 是否为打开状态，默认第一次为收起状态
     */
    private boolean isOpenStatus;
    /**
     * 默认为初始化状态
     */
    private boolean initStatus = true;
    //    最大的拖动大小
    private int maxScrollHeight = 0;

    private int mTouchSlop;

    private boolean isMoveUp = false;
    private boolean mDragging;

    private int mLastY;
    private boolean isMoving = false;

    private View root, upArrow, arrowDownRoot;

    public CustomWidgetView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomWidgetView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        inflate(context, R.layout.buttom_widget_bar, this);
        root = findViewById(R.id.root);
        upArrow = findViewById(R.id.upArrow);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(context));
        tip = findViewById(R.id.tip);
        middleAndBottom = findViewById(R.id.middleAndBottom);
        topView = findViewById(R.id.topView);
        mScroller = new Scroller(mContext);

        arrowDownRoot = findViewById(R.id.arrowDownRoot);
        arrowDownRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenStatus) {
                    hindWidget();
                    isOpenStatus = false;
                    arrowDownRoot.setVisibility(View.GONE);
                    tip.setVisibility(View.GONE);
                    upArrow.setVisibility(View.VISIBLE);
                } else {
                    showWidget();
                    isOpenStatus = true;
                    tip.setVisibility(View.VISIBLE);
                    upArrow.setVisibility(View.GONE);
                    arrowDownRoot.setVisibility(View.VISIBLE);
                }
            }
        });
        ViewTreeObserver observer = middleAndBottom.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                middleAndBottom.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                maxScrollHeight = middleAndBottom.getMeasuredHeight();
                mScroller.startScroll(0, 0, 0, -maxScrollHeight);
            }
        });

        topView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int y = (int) event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMoving = false;
                        mLastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int offsetY = y - mLastY;
                        //追踪
                        if (Math.abs(offsetY) > mTouchSlop) {
                            mDragging = true;
                        }
                        if (mDragging) {
                            isMoving = true;
                            CustomWidgetView.this.scrollBy(0, -offsetY);
                            if (offsetY < 0) {
                                isMoveUp = true;
                            } else {
                                isMoveUp = false;
                            }
                            Log.i("zzzzzzzz", "move   offsetY = " + offsetY);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mDragging = false;
                        if (isMoving) {
                            if (isMoveUp) {
                                Log.i("zzzzzzzz", "向上滑  ");
                                showWidget();
                                isOpenStatus = true;
                                tip.setVisibility(View.VISIBLE);
                                upArrow.setVisibility(View.GONE);
                                arrowDownRoot.setVisibility(View.VISIBLE);
                            } else {
                                Log.i("zzzzzzzz", "向下滑   ");
                                hindWidget();
                                isOpenStatus = false;
                                tip.setVisibility(View.GONE);
                                upArrow.setVisibility(View.VISIBLE);
                                arrowDownRoot.setVisibility(View.GONE);
                            }
                        }
                        isMoving = false;
                        break;
                }

                return true;
            }
        });
    }

    public void showWidget() {
        mScroller.startScroll(0, getScrollY(), 0, maxScrollHeight - getScrollY(), 300);
        postInvalidate();

    }

    public void hindWidget() {
        mScroller.startScroll(0, getScrollY(), 0, -maxScrollHeight - getScrollY(), 300);
        postInvalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        gridView = findViewById(R.id.gridView);

        unselectedImageId = new int[]{R.mipmap.unselected_check_in, R.mipmap.unselected_check_out,
                R.mipmap.unselected_go_out, R.mipmap.unselected_goback, R.mipmap.unselected_overtime_check_in,
                R.mipmap.unselected_overtime_check_out};
        selectedImageId = new int[]{R.mipmap.selected_check_in, R.mipmap.selected_check_out,
                R.mipmap.selected_go_out, R.mipmap.selected_goback, R.mipmap.selected_overtime_check_in,
                R.mipmap.selected_overtime_check_out};

        String[] title = new String[]{"上班签到", "上班签退", "外出时间", "外出返回", "加班签到", "加班签退"};

        for (int x = 0; x < 6; x++) {
            WidgetInfo widgetInfo = new WidgetInfo();
            widgetInfo.name = title[x];
            widgetInfo.unselectedImage = unselectedImageId[x];
            widgetInfo.selectedImage = selectedImageId[x];
            widgetInfos.add(widgetInfo);
        }
        myAdapter = new MyAdapter();
        gridView.setAdapter(myAdapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        /**
         * 防止滑动超出边界
         */
        if (y >= 0) {
            y = 0;
        }
        if (y <= -maxScrollHeight) {
            y = -maxScrollHeight;
        }

        if (initStatus) {
            if (y == -maxScrollHeight) {
                root.setVisibility(View.VISIBLE);
                initStatus = false;
            }
        }

        super.scrollTo(x, y);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectPosition = position;
        myAdapter.notifyDataSetChanged();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return widgetInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = View.inflate(mContext, R.layout.item_widget, null);

            ImageView imageView = convertView.findViewById(R.id.img);
            TextView textView = convertView.findViewById(R.id.text);
            final WidgetInfo widgetInfo = widgetInfos.get(position);

            if (selectPosition == position) {
                imageView.setBackgroundResource(widgetInfo.selectedImage);
            } else {
                imageView.setBackgroundResource(widgetInfo.unselectedImage);
            }

            textView.setText(widgetInfo.name);
            return convertView;
        }
    }


}
