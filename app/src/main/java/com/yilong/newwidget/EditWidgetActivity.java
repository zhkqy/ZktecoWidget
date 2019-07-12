package com.yilong.newwidget;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yilong.newwidget.view.DragGridView.DragGridBaseAdapter;
import com.yilong.newwidget.view.DragGridView.DragGridView;
import com.yilong.newwidget.view.DragGridView.DragScrollView;

import java.util.ArrayList;

/**
 * @author leichenrd
 * @date 2019/7/12.
 * @desc
 */
public class EditWidgetActivity extends Activity {
    private DragScrollView dragScrollView;

    private ArrayList<WidgetInfo> widgetInfos = new ArrayList<>();
    private int[] unselectedImageId;
    private int[] selectedImageId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_widget);

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

        for (int x = 0; x < 6; x++) {
            WidgetInfo widgetInfo = new WidgetInfo();
            widgetInfo.name = title[x];
            widgetInfo.unselectedImage = unselectedImageId[x];
            widgetInfo.selectedImage = selectedImageId[x];
            widgetInfos.add(widgetInfo);
        }


        dragScrollView = findViewById(R.id.dragScrollView);
        DragGridView topGridView = findViewById(R.id.topGridview);
        topGridView.setAdapter(new MyAdapter());

        topGridView.setScrollView(dragScrollView);

//        DragGridView bottomGridView = findViewById(R.id.bottomGridview);
//        bottomGridView.setAdapter(new TopMyAdapter());

    }

    class TopMyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
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

            convertView = View.inflate(EditWidgetActivity.this, R.layout.item_widget, null);

            ImageView imageView = convertView.findViewById(R.id.img);
            TextView textView = convertView.findViewById(R.id.text);
            final WidgetInfo widgetInfo = widgetInfos.get(position);

            imageView.setBackgroundResource(widgetInfo.unselectedImage);

            textView.setText(widgetInfo.name);
            return convertView;
        }
    }


    class MyAdapter extends BaseAdapter implements DragGridBaseAdapter {

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

            convertView = View.inflate(EditWidgetActivity.this, R.layout.item_widget, null);

            ImageView imageView = convertView.findViewById(R.id.img);
            TextView textView = convertView.findViewById(R.id.text);
            final WidgetInfo widgetInfo = widgetInfos.get(position);

            imageView.setBackgroundResource(widgetInfo.unselectedImage);

            textView.setText(widgetInfo.name);
            return convertView;
        }

        @Override
        public void reorderItems(int oldPosition, int newPosition) {

        }

        @Override
        public void setHideItem(int hidePosition) {


        }
    }


}
