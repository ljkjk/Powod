package com.ljkjk.powod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.ljkjk.powod.R;
import com.ljkjk.powod.entity.Word;
import com.ljkjk.powod.utils.DBUtils;
import com.ljkjk.powod.utils.WordListUtils;

import java.sql.Date;

public class WordAddActivity extends AppCompatActivity {

    EditText editTextCtnt, editTextPron, editTextMean, editTextTags, editTextSyno, editTextAnto;
    MaterialButton btnWordAdd;
    DBUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_add);

        init();
        setBtnWordAdd();
    }

    void init(){
        editTextCtnt = findViewById(R.id.word_add_ctnt);
        editTextPron = findViewById(R.id.word_add_pron);
        editTextMean = findViewById(R.id.word_add_mean);
        editTextTags = findViewById(R.id.word_add_tags);
        editTextSyno = findViewById(R.id.word_add_syno);
        editTextAnto = findViewById(R.id.word_add_anto);
        btnWordAdd = findViewById(R.id.btn_word_add);
        db = new DBUtils(getApplicationContext());
    }

    void setBtnWordAdd(){
        btnWordAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }
}
