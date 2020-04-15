package com.ljkjk.powod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.ljkjk.powod.R;
import com.ljkjk.powod.entity.Word;
import com.ljkjk.powod.utils.DBUtils;
import com.ljkjk.powod.utils.WordAdapter;
import com.ljkjk.powod.utils.WordListUtils;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ResultListActivity extends AppCompatActivity {

    ListView resultListView;

    List<Word> tempWordList;

    DBUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        db = new DBUtils(getApplicationContext());
        resultListView = findViewById(R.id.result_list_view);
        search();

        resultListView.setAdapter(new WordAdapter(ResultListActivity.this, R.layout.word_list_item, tempWordList));
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ResultListActivity.this, WordDetailsActivity.class);
                intent.putExtra("ctnt", tempWordList.get(position).getCtnt());
                startActivity(intent);
            }
        });
    }

    void search(){
        String key = getIntent().getStringExtra("key");
        tempWordList = WordListUtils.getWordListByKey(db, key);
        Collections.sort(tempWordList, new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                return Collator.getInstance(Locale.CHINESE).compare(o1.getCtnt(), o2.getCtnt());
            }
        });
    }

}
