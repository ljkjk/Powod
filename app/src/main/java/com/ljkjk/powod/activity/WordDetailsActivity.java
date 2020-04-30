package com.ljkjk.powod.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ljkjk.powod.R;
import com.ljkjk.powod.entity.Word;
import com.ljkjk.powod.list.DetailAdapter;
import com.ljkjk.powod.listener.OnSwipeTouchListener;
import com.ljkjk.powod.net.GetCharInfo;
import com.ljkjk.powod.net.GetWordInfo;
import com.ljkjk.powod.utils.DatabaseUtils;
import com.ljkjk.powod.utils.DetailsListUtils;
import com.ljkjk.powod.utils.Utils;
import com.ljkjk.powod.utils.WordListUtils;

public class WordDetailsActivity extends AppCompatActivity {
    
    Word word;
    String pa;

    View view;
    Toolbar toolbar;
    TextView ctntView;
    TextView pronView;
    ListView detailsListView;

    DatabaseUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_details);

        init();
        setToolbar();
        setGesture();
        setText();
    }

    void init(){
        view = findViewById(R.id.detail_view);
        toolbar = findViewById(R.id.toolbar_word_details);
        ctntView = findViewById(R.id.detail_ctnt);
        pronView = findViewById(R.id.detail_pron);
        detailsListView = findViewById(R.id.details_list_view);
        db = new DatabaseUtils(getApplicationContext());
        word = db.getWord(getIntent().getStringExtra("ctnt"));
        pa = getIntent().getStringExtra("pa");

        // 让listiview也能发起滚动
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            detailsListView.setNestedScrollingEnabled(true);
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

    @SuppressLint("ClickableViewAccessibility")
    void setGesture() {
        view.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeTop() { }

            public void onSwipeRight() {
                prev();
            }

            public void onSwipeLeft() {
                next();
            }

            public void onSwipeBottom() { }
        });

        detailsListView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeTop() { }

            public void onSwipeRight() {
                prev();
            }

            public void onSwipeLeft() {
                next();
            }

            public void onSwipeBottom() { }
        });
    }

    void next() {
        if (pa.contentEquals("MAIN")) {
            Intent intent = new Intent(WordDetailsActivity.this, WordDetailsActivity.class);
            String nextWordCtnt = WordListUtils.nextWordCtnt(word.getCtnt());
            if (nextWordCtnt != null) {
                intent.putExtra("ctnt", nextWordCtnt);
                intent.putExtra("pa", "MAIN");
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        }
    }

    void prev() {
        if (pa.contentEquals("MAIN")) {
            Intent intent = new Intent(WordDetailsActivity.this, WordDetailsActivity.class);
            String prevWordCtnt = WordListUtils.prevWordCtnt(word.getCtnt());
            if (prevWordCtnt != null) {
                intent.putExtra("ctnt", prevWordCtnt);
                intent.putExtra("pa", "MAIN");
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        }
    }

    void setText() {
        ctntView.setText(word.getCtnt());
        pronView.setText(word.getPron());
        DetailsListUtils.setDetailsList(word);
        detailsListView.setAdapter(new DetailAdapter(WordDetailsActivity.this, R.layout.word_details_item, DetailsListUtils.list()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_edit:
                actionEdit();
                return true;
            case R.id.action_get_net_info:
                actionGetNetInfo();
                return true;
            case R.id.action_delete:
                actionDelete();
                return true;
            case R.id.action_increase:
                actionIncrease();
                return true;
            case R.id.action_decrease:
                actionDecrease();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void actionEdit() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(WordDetailsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("编辑词条");
        dialogBuilder.setCancelable(false);

        final EditText ctnt = dialogView.findViewById(R.id.edit_ctnt);
        final EditText pron = dialogView.findViewById(R.id.edit_pron);
        final EditText mean = dialogView.findViewById(R.id.edit_mean);
        final EditText syno = dialogView.findViewById(R.id.edit_syno);
        final EditText anto = dialogView.findViewById(R.id.edit_anto);
        final EditText tags = dialogView.findViewById(R.id.edit_tags);

        ctnt.setText(word.getCtnt(), TextView.BufferType.NORMAL);
        ctnt.setEnabled(false);
        pron.setText(word.getPron(),TextView.BufferType.EDITABLE);
        mean.setText(word.getMean(),TextView.BufferType.EDITABLE);
        tags.setText(word.getTags(),TextView.BufferType.EDITABLE);
        syno.setText(word.getSyno(),TextView.BufferType.EDITABLE);
        anto.setText(word.getAnto(),TextView.BufferType.EDITABLE);


        dialogBuilder.setPositiveButton("保存",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // empty
            }
        });

        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        pron.addTextChangedListener(textWatcher);
        mean.addTextChangedListener(textWatcher);
        tags.addTextChangedListener(textWatcher);
        syno.addTextChangedListener(textWatcher);
        anto.addTextChangedListener(textWatcher);

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setEnabled(false);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newPron = pron.getText().toString().toLowerCase().trim();
                        String newMean = mean.getText().toString().trim();
                        String newTags = tags.getText().toString().trim();
                        String newSyno = syno.getText().toString().trim();
                        String newAnto = anto.getText().toString().trim();
                        if (!newTags.contentEquals(word.getTags())) {
                            MainActivity.isChanged = true; // 词表改变
                        }
                        try {
                            word.setPron(newPron);
                            word.setMean(newMean);
                            word.setTags(newTags);
                            word.setSyno(newSyno);
                            word.setAnto(newAnto);
                            db.updateWord(word);
                            setText();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        // setText();
                    }
                });
            }
        });

        alertDialog.show();

    }

    void actionIncrease() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(WordDetailsActivity.this);
        dialogBuilder.setTitle("确认增频");
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int curFreq = Integer.parseInt(DetailsListUtils.getContent("使用频次"));
                db.updateWordFreq(word.getCtnt(), 1);
                word.setFreq(curFreq+1);
                //freqView.setText(String.valueOf(word.getFreq()));
                //省事
                setText();
                Toast.makeText(getApplicationContext(), "增频成功", Toast.LENGTH_LONG).show();
            }
        });

        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    void actionDecrease() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(WordDetailsActivity.this);
        dialogBuilder.setTitle("确认减频");
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int curFreq = Integer.parseInt(DetailsListUtils.getContent("使用频次"));
                if (curFreq == 0) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "不能再减少了", Toast.LENGTH_LONG).show();
                } else {
                    db.updateWordFreq(word.getCtnt(), -1);
                    word.setFreq(curFreq-1);
                    //freqView.setText(String.valueOf(word.getFreq()));
                    //省事不省时
                    setText();
                    Toast.makeText(getApplicationContext(), "减频成功", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    void actionDelete() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(WordDetailsActivity.this);
        dialogBuilder.setTitle("确认删除");
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteWord(word.getCtnt());
                WordListUtils.delete(word.getCtnt());
                MainActivity.isChanged = true; // 词表改变
                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    void actionGetNetInfo() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(WordDetailsActivity.this);
        dialogBuilder.setTitle("确认补全");
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.isNetworkAvailable(WordDetailsActivity.this)){
                    final String ctnt = word.getCtnt();

                    // 判断中文字数
                    if (Utils.getZhCharCount(ctnt) > 1) {
                        getNetInfoWordZh(ctnt);
                    } else {
                        getNetInfoCharZh(ctnt);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    void getNetInfoWordZh(final String ctnt){
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                if (data.getBoolean("has")) {
                    String pron = data.getString("pron");
                    String mean = data.getString("mean");
                    String syno = data.getString("syno");
                    String anto = data.getString("anto");

                    word.setPron(pron);
                    word.setMean(mean);
                    word.setSyno(syno);
                    word.setAnto(anto);
                    setText();

                    db.updateWordByNet(ctnt, pron, mean, syno, anto);

                    Toast.makeText(getApplicationContext(), "补全完成", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "内容暂无", Toast.LENGTH_LONG).show();
                }
            }
        };

        new Thread(new GetWordInfo(ctnt, handler)).start();
    }

    void getNetInfoCharZh(final String ctnt){
        // 当收到消息时就会执行这个方法
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                if (data.getBoolean("has")) {
                    String pron = data.getString("pron");
                    String mean = data.getString("mean");

                    word.setPron(pron);
                    word.setMean(mean);
                    setText();

                    db.updateWordByNet(ctnt, pron, mean);

                    Toast.makeText(getApplicationContext(), "补全完成", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "内容暂无", Toast.LENGTH_LONG).show();
                }
            }
        };

        new Thread(new GetCharInfo(ctnt, handler)).start();
    }
}
