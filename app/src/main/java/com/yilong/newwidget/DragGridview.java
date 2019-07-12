package com.yilong.newwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author leichenrd
 * @date 2019/7/12.
 * @desc
 */
public class DragGridview extends GridView {
    public DragGridview(Context context) {
        super(context);
    }

    public DragGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
