package com.ljkjk.powod.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ljkjk.powod.R;
import com.ljkjk.powod.entity.Word;

import java.util.List;

public class WordUnclassifiedAdapter extends ArrayAdapter<Word> {

    private int resourceId; // 子项布局的id

    public WordUnclassifiedAdapter(Context context, int textViewResourceId, List<Word> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取当前项的Word实例
        Word word = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null){
            // inflate出子项布局，实例化其中的图片控件和文本控件
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new ViewHolder();
            // 通过id得到图片控件实例
            viewHolder.item_ctnt = view.findViewById(R.id.item_ctnt);
            // 通过id得到文本空间实例
            viewHolder.item_tags = view.findViewById(R.id.item_tags);
            // 缓存图片控件和文本控件的实例
            view.setTag(viewHolder);
        }else{
            view = convertView;
            // 取出缓存
            viewHolder = (ViewHolder) view.getTag();
        }

        // 直接使用缓存中的图片控件和文本控件的实例
        assert word != null;
        viewHolder.item_ctnt.setText(word.getCtnt());
        // 文本控件设置文本内容
        viewHolder.item_tags.setText(word.getTags());

        return view;
    }

    // 内部类
    static class ViewHolder{
        TextView item_ctnt;
        TextView item_tags;
    }
}
