package com.ljkjk.powod.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ljkjk.powod.R;
import com.ljkjk.powod.SortType;
import com.ljkjk.powod.entity.Word;
import com.ljkjk.powod.list.WordClassifiedAdapter;
import com.ljkjk.powod.utils.DatabaseUtils;
import com.ljkjk.powod.utils.WordListUtils;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ResultListActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView resultListView;
    TextView resultHint;

    List<Word> tempWordList;

    DatabaseUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        init();
        setToolbar();
        if (generateWordList()){
            // 有结果时设置结果列表
            setResultList();
        } else {
            // 无结果时提示
            resultHint.setText("无结果");
        }
    }

    void init(){
        db = new DatabaseUtils(getApplicationContext());
        resultListView = findViewById(R.id.result_list_view);
        resultHint = findViewById(R.id.result_hint);
        toolbar = findViewById(R.id.toolbar_result);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resultListView.setNestedScrollingEnabled(true);
        }
    }

    void setToolbar(){
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    boolean generateWordList(){
        String key = getIntent().getStringExtra("key");
        String tag = getIntent().getStringExtra("tag");

        if (tag != null) {
            tempWordList = WordListUtils.getWordListByTag(db, tag);
        } else {
            tempWordList = WordListUtils.getWordListByKey(db, key);
        }
        return tempWordList.size() > 0;
    }

    void setResultList(){
        // 结果按拼音排序
        Collections.sort(tempWordList, new Comparator<Word>() {
        @Override
        public int compare(Word o1, Word o2) {
            return Collator.getInstance(Locale.CHINESE).compare(o1.getCtnt(), o2.getCtnt());
            }
        });

        resultListView.setAdapter(new WordClassifiedAdapter(ResultListActivity.this, tempWordList, SortType.DEFAULT));
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ResultListActivity.this, WordDetailsActivity.class);
                intent.putExtra("ctnt", ((WordClassifiedAdapter.WordTypeItem) resultListView.getAdapter().getItem(position)).getWord().getCtnt());
                intent.putExtra("pa", "RESULT");
                startActivity(intent);
            }
        });
    }

}
