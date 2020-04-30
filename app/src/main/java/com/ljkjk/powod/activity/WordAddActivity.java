package com.ljkjk.powod.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.ljkjk.powod.R;
import com.ljkjk.powod.entity.Word;
import com.ljkjk.powod.net.GetCharInfo;
import com.ljkjk.powod.net.GetWordInfo;
import com.ljkjk.powod.utils.DatabaseUtils;
import com.ljkjk.powod.utils.Utils;
import com.ljkjk.powod.utils.WordListUtils;

import java.sql.Date;

public class WordAddActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText editTextCtnt, editTextPron, editTextMean, editTextTags, editTextSyno, editTextAnto;
    MaterialButton btnGetNetInfo;
    DatabaseUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_add);

        init();
        setToolbar();
        setBtnGetNetInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_word_add){
            action_word_add();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void action_word_add(){
        String ctnt = editTextCtnt.getText().toString().trim();
        String pron = editTextPron.getText().toString().toLowerCase().trim();
        String mean = editTextMean.getText().toString().trim();
        String tags = editTextTags.getText().toString().trim();
        String syno = editTextSyno.getText().toString().trim();
        String anto = editTextAnto.getText().toString().trim();
        int freq = 0;
        Date addt = new Date(System.currentTimeMillis());

        if (!ctnt.isEmpty()){
            Word word = new Word(ctnt, pron, mean, tags, syno, anto, freq, addt);
            System.out.println(word.toString());
            if (db.insertWord(word)){
                WordListUtils.add(word);
                MainActivity.isChanged = true; // 词表变化
                Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(WordAddActivity.this, WordDetailsActivity.class);
                intent.putExtra("ctnt", ctnt);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "词语已在列表中", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "词语为空无法添加", Toast.LENGTH_LONG).show();
        }
    }


    void init(){
        toolbar = findViewById(R.id.toolbar_word_add);
        editTextCtnt = findViewById(R.id.word_add_ctnt);
        editTextPron = findViewById(R.id.word_add_pron);
        editTextMean = findViewById(R.id.word_add_mean);
        editTextTags = findViewById(R.id.word_add_tags);
        editTextSyno = findViewById(R.id.word_add_syno);
        editTextAnto = findViewById(R.id.word_add_anto);
        btnGetNetInfo = findViewById(R.id.btn_word_add_get_net_info);
        db = new DatabaseUtils(getApplicationContext());
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

    void setBtnGetNetInfo(){
        btnGetNetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable(WordAddActivity.this)){
                    final String ctnt = editTextCtnt.getText().toString().trim();

                    // 判断中文字数
                    switch (Utils.getZhCharCount(ctnt)){
                        case 0:
                            Toast.makeText(getApplicationContext(), "词语为空", Toast.LENGTH_LONG).show();
                            return;
                        case 1:
                            getNetInfoCharZh(ctnt);
                            Utils.hideSoftKeyboard(WordAddActivity.this);
                            return;
                        default:
                            getNetInfoWordZh(ctnt);
                            Utils.hideSoftKeyboard(WordAddActivity.this);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void getNetInfoWordZh(final String ctnt){
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                if (data.getBoolean("has")) {
                    editTextPron.setText(data.getString("pron"));
                    editTextMean.setText(data.getString("mean"));
                    editTextSyno.setText(data.getString("syno"));
                    editTextAnto.setText(data.getString("anto"));

                    Toast.makeText(getApplicationContext(), "补全完成", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "内容暂无", Toast.LENGTH_LONG).show();
                }
            }
        };

        new Thread(new GetWordInfo(ctnt, handler)).start();
    }

    void getNetInfoCharZh(final String ctnt){
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                if (data.getBoolean("has")) {
                    editTextPron.setText(data.getString("pron"));
                    editTextMean.setText(data.getString("mean"));

                    Toast.makeText(getApplicationContext(), "补全完成", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "内容暂无", Toast.LENGTH_LONG).show();
                }
            }
        };

        new Thread(new GetCharInfo(ctnt, handler)).start();
    }
}
