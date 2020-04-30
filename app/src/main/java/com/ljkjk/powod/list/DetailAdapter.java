package com.ljkjk.powod.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ljkjk.powod.R;
import com.ljkjk.powod.entity.Detail;

import java.util.List;

public class DetailAdapter extends ArrayAdapter<Detail> {

    private int resourceId; // 子项布局的id

    public DetailAdapter(Context context, int textViewResourceId, List<Detail> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取当前项的Word实例
        Detail di = getItem(position);
        View view;
        DetailAdapter.ViewHolder viewHolder;

        if (convertView == null){
            // inflate出子项布局，实例化其中的图片控件和文本控件
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new DetailAdapter.ViewHolder();
            // 通过id得到图片控件实例
            viewHolder.item_title = view.findViewById(R.id.detail_item_title);
            // 通过id得到文本空间实例
            viewHolder.item_content = view.findViewById(R.id.detail_item_content);
            // 缓存图片控件和文本控件的实例
            view.setTag(viewHolder);
        }else{
            view = convertView;
            // 取出缓存
            viewHolder = (DetailAdapter.ViewHolder) view.getTag();
        }

        // 直接使用缓存中的图片控件和文本控件的实例
        viewHolder.item_title.setText(di.getTitle());
        // 文本控件设置文本内容
        viewHolder.item_content.setText(di.getContent());

        return view;
    }

    // 内部类
    class ViewHolder{
        TextView item_title;
        TextView item_content;
    }
}

