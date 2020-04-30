package com.ljkjk.powod.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ljkjk.powod.R;
import com.ljkjk.powod.SortType;
import com.ljkjk.powod.entity.Word;
import com.ljkjk.powod.utils.PinYinUtils;
import com.ljkjk.powod.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class WordClassifiedAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_COUNT = 2; // 布局数目
    private static final int VIEW_TYPE_HEADER = 0; // 分组头部
    private static final int VIEW_TYPE_ITEM = 1; // 词语

    private LayoutInflater mInflater;
    private List<TypeItem> items;
    private SortType sortType;

    public WordClassifiedAdapter(Context context, List<Word> items, SortType sortType) {
        mInflater = LayoutInflater.from(context);
        this.sortType = sortType;
        this.items = generateItems(items);

    }

    // 实体基类，包含itemType，以便区分不同的布局,子类需要的其他数据可自行指定
    public class TypeItem {
        int itemType;

        TypeItem(int itemType) {
            this.itemType = itemType;
        }
    }

    // 分类头部实体，指定itemType为VIEW_TYPE_ALPHA_HEADER，包含分组中头部的字母
    public class AlphaHeaderTypeItem extends TypeItem {
        char header;

        AlphaHeaderTypeItem(char header) {
            super(VIEW_TYPE_HEADER);
            this.header = header;
        }
    }

    public class DateHeaderTypeItem extends TypeItem {
        String header;

        DateHeaderTypeItem(String header) {
            super(VIEW_TYPE_HEADER);
            this.header = header;
        }
    }

    public class FreqHeaderTypeItem extends TypeItem {
        int header;

        FreqHeaderTypeItem(int header) {
            super(VIEW_TYPE_HEADER);
            this.header = header;
        }
    }

    // 词语列表实体，指定itemType为VIEW_TYPE_ITEM，包含Word实体
    public class WordTypeItem extends TypeItem {
        Word wordItem;

        WordTypeItem(Word wordItem) {
            super(VIEW_TYPE_ITEM);
            this.wordItem = wordItem;
        }

        public Word getWord() {
            return wordItem;
        }
    }

    // 根据传入的wordItem list，构造带有header的TypeItem
    private List<TypeItem> generateItems(List<Word> wordItems) {
        List<TypeItem> items = new ArrayList<>();
        int size = wordItems == null ? 0 : wordItems.size();
        switch (sortType){
            case DEFAULT:
                char currIndexAlpha;
                char preIndexAlpha = '{';
                for (int i = 0; i < size; i++) {
                    currIndexAlpha = PinYinUtils.getPinYin(wordItems.get(i).getCtnt()).charAt(0);
                    // 因为pinyin4j不会判断多音字，可以利用词语的注音数据标识正确的首字母
                    String pron = wordItems.get(i).getPron();
                    if (!pron.isEmpty()) {
                        // 拿出自己的注音的首字母
                        char index = Utils.toUpperCase(pron.substring(pron.indexOf('[')+1).trim().charAt(0));
                        currIndexAlpha = index;
                    }

                    // 如果不是正常字词，则用#代替
                    if (!Utils.isAlpha(currIndexAlpha)) {
                        currIndexAlpha = '#';
                    }
                    // 是第一个item或者两个数据的拼音首字母不相等则插入头部
                    if (i == 0 || currIndexAlpha != preIndexAlpha) {
                        items.add(new AlphaHeaderTypeItem(currIndexAlpha));
                    }
                    items.add(new WordTypeItem(wordItems.get(i)));
                    preIndexAlpha = currIndexAlpha;
                }
                break;
            case DATE:
                String currIndexDate;
                String preIndexDate = "ZERO";
                for (int i = 0; i < size; i++) {
                    currIndexDate = Utils.date2String(wordItems.get(i).getAddt());
                    System.out.println(currIndexDate);
                    if (i == 0 || !currIndexDate.equals(preIndexDate)) {
                        items.add(new DateHeaderTypeItem(currIndexDate));
                    }
                    items.add(new WordTypeItem(wordItems.get(i)));
                    preIndexDate = currIndexDate;
                }
                break;
            case FREQUENCY:
                int currIndexFreq;
                int preIndexFreq = -1;
                for (int i = 0; i < size; i++) {
                    currIndexFreq = wordItems.get(i).getFreq();
                    if (i == 0 || currIndexFreq != preIndexFreq) {
                        items.add(new FreqHeaderTypeItem(currIndexFreq));
                    }
                    items.add(new WordTypeItem(wordItems.get(i)));
                    preIndexFreq = currIndexFreq;
                }
                break;
        }
        return items;
    }

    // ViewHolder基类，itemView用于查找子view
    class ViewHolder {
        View itemView;

        ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView can not be null!");
            }
            this.itemView = itemView;
        }
    }

    // 分类头部ViewHolder
    class HeaderViewHolder extends ViewHolder {
        TextView header;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.item_header);
        }
    }

    // 词语列表ViewHolder
    class WordViewHolder extends ViewHolder {
        TextView ctnt;
        TextView tags;

        WordViewHolder(View itemView) {
            super(itemView);
            ctnt = itemView.findViewById(R.id.item_ctnt);
            tags = itemView.findViewById(R.id.item_tags);
        }
    }

    @Override
    public View getView(int postion, View convertView, ViewGroup parent) {
        TypeItem item = items.get(postion);
        ViewHolder viewHolder;
        if (convertView == null) {
            // 根据不同的viewType，初始化不同的布局
            switch (getItemViewType(postion)) {
                case VIEW_TYPE_HEADER:
                    viewHolder = new HeaderViewHolder(mInflater.inflate(R.layout.word_list_header_item, null));
                    break;
                case VIEW_TYPE_ITEM:
                    viewHolder = new WordViewHolder(mInflater.inflate(R.layout.word_list_item, null));
                    break;
                default:
                    throw new IllegalArgumentException("invalid view type : " + getItemViewType(postion));
            }

            // 缓存header与item视图
            convertView = viewHolder.itemView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 根据初始化的不同布局，绑定数据
        if (viewHolder instanceof HeaderViewHolder) {
            switch (sortType) {
                case DEFAULT:
                    ((HeaderViewHolder) viewHolder).header.setText(String.valueOf(((AlphaHeaderTypeItem) item).header));
                    break;
                case DATE:
                    ((HeaderViewHolder) viewHolder).header.setText(((DateHeaderTypeItem) item).header);
                    break;
                case FREQUENCY:
                    ((HeaderViewHolder) viewHolder).header.setText(String.valueOf(((FreqHeaderTypeItem) item).header));
                    break;
            }

        } else if (viewHolder instanceof WordViewHolder) {
            onBindWord((WordViewHolder) viewHolder, ((WordTypeItem) item).wordItem);
        }
        return convertView;
    }

    private void onBindWord(WordViewHolder viewHolder, Word wordItem) {
        viewHolder.ctnt.setText(wordItem.getCtnt());
        viewHolder.tags.setText(wordItem.getTags());
    }

    @Override
    public int getItemViewType(int position) {
        if (items != null) {
            return items.get(position).itemType;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        if (items != null && position > 0 && position < items.size()) {
            return items.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int postion) {
        return postion;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != VIEW_TYPE_HEADER;
    }
}
