package com.example.termproject_applight;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SettingItem> listItems = new ArrayList<SettingItem>();
    private OnApplyClickListener apply;

    public interface OnApplyClickListener {
        void onApply(View v, int pos);
    }

    public SettingAdapter(Context context, OnApplyClickListener listener) {
        this.context = context;
        this.apply = listener;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    public String getName(int i) {
        return listItems.get(i).getName();
    }

    public String getColor(int i) {
        return listItems.get(i).getColor();
    }

    public String getTime(int i) {
        return listItems.get(i).getTime();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // list_item.xml 레이아웃을 inflate해서 참조획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_settings, parent, false);
        }

        // list_item.xml 의 참조 획득
        final TextView colorTextView = (TextView) convertView.findViewById(R.id.settingColor);
        final TextView timeTextView = (TextView) convertView.findViewById(R.id.settingTime);

        Button deleteButton = (Button) convertView.findViewById(R.id.buttonDelete);
        SettingItem listItem = listItems.get(position);

        // 가져온 데이터를 텍스트뷰에 입력
        colorTextView.setText(listItem.getColor());
        timeTextView.setText(listItem.getTime());
        String name = colorTextView.getText().toString();
        // 리스트 아이템 삭제
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apply.onApply(view, position);
            }
        });

        return convertView;
    }

    public void addItem(String name, String color, String time) {
        SettingItem listItem = new SettingItem();

        listItem.setName(name);
        listItem.setColor(color);
        listItem.setTime(time);

        listItems.add(listItem);
    }

    public void removeItem(int pos) {
        listItems.remove(pos);
        notifyDataSetChanged();
    }


}
