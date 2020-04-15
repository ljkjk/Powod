package com.ljkjk.powod.activity;

import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ljkjk.powod.R;
import com.ljkjk.powod.entity.Word;
import com.ljkjk.powod.utils.DBUtils;
import com.ljkjk.powod.utils.Utils;
import com.ljkjk.powod.utils.WordListUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class WordDetailsActivity extends AppCompatActivity {

    Word word;

    TextView ctntView;
    TextView pronView;
    TextView meanView;
    TextView tagsView;
    TextView synoView;
    TextView antoView;
    TextView freqView;
    TextView addtView;

    DBUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_details);

        init();
        setText();
    }

    void init(){
        ctntView = findViewById(R.id.detail_ctnt);
        pronView = findViewById(R.id.detail_pron);
        meanView = findViewById(R.id.detail_mean);
        tagsView = findViewById(R.id.detail_tags);
        synoView = findViewById(R.id.detail_syno);
        antoView = findViewById(R.id.detail_anto);
        freqView = findViewById(R.id.detail_freq);
        addtView = findViewById(R.id.detail_addt);
        db = new DBUtils(getApplicationContext());
        word = db.getWord(getIntent().getStringExtra("ctnt"));
    }

    void setText() {
        ctntView.setText(word.getCtnt());
        pronView.setText(word.getPron());
        meanView.setText(word.getMean());
        tagsView.setText(word.getTags());
        synoView.setText(word.getSyno());
        antoView.setText(word.getAnto());
        freqView.setText(String.valueOf(word.getFreq()));
        addtView.setText(word.getAddt().toString());
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

        ctnt.setText(ctntView.getText(), TextView.BufferType.NORMAL);
        ctnt.setEnabled(false);
        pron.setText(pronView.getText(),TextView.BufferType.EDITABLE);
        mean.setText(meanView.getText(),TextView.BufferType.EDITABLE);
        tags.setText(tagsView.getText(),TextView.BufferType.EDITABLE);
        syno.setText(synoView.getText(),TextView.BufferType.EDITABLE);
        anto.setText(antoView.getText(),TextView.BufferType.EDITABLE);


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
                        setText();
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
                int curFreq = Integer.parseInt(freqView.getText().toString());
                db.updateWordFreq(word.getCtnt(), 1);
                word.setFreq(curFreq+1);
                freqView.setText(String.valueOf(word.getFreq()));
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
                int curFreq = Integer.parseInt(freqView.getText().toString());
                if (curFreq == 0) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "不能再减少了", Toast.LENGTH_LONG).show();
                } else {
                    db.updateWordFreq(word.getCtnt(), -1);
                    word.setFreq(curFreq-1);
                    freqView.setText(String.valueOf(word.getFreq()));

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
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String pron = data.getString("pron");
                String mean = data.getString("mean");
                String syno = data.getString("syno");
                String anto = data.getString("anto");

                word.setPron(pron);
                pronView.setText(pron);
                word.setMean(mean);
                meanView.setText(mean);
                word.setSyno(syno);
                synoView.setText(syno);
                word.setAnto(anto);
                antoView.setText(anto);

                db.updateWordByNet(ctnt, pron, mean, syno, anto);

                Toast.makeText(getApplicationContext(), "补全完成", Toast.LENGTH_LONG).show();

                // 当收到消息时就会执行这个方法
            }
        };

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    Bundle data = new Bundle();

                    Document doc = Jsoup.connect("http://webdict.youzhi.net/index.do")
                            .data("id", "65bd6dc3c1b7488a80dbc0c24fcbf0e5")
                            .data("words", ctnt)
                            .userAgent("Mozilla")
                            .post();

                    // 拿details，包含读音和可能的近反义词
                    Elements elsDetails = doc.select("div.details");
                    Element elDetail = elsDetails.first();
                    Elements elsP = elDetail.getElementsByTag("p");
                    List<String> detail = elsP.eachText();
                    // 拿读音
                    data.putString("pron", detail.get(0).split("：")[1]);
                    // 拿近反义词
                    data.putString("syno", "");
                    data.putString("anto", "");
                    for (int i = 1; i < detail.size(); i++) {
                        String[] str = detail.get(i).split("：");
                        if (str[0].contentEquals("同义词")){
                            data.putString("syno", str[1].replaceAll(",", " "));
                        } else {
                            data.putString("anto", str[1].replaceAll(",", " "));
                        }
                    }

                    // 拿到h2：解释标题
                    Elements elsH2 = doc.getElementsByTag("h2");
                    String[] h2List = new String[3];
                    for (int i = 0; i < elsH2.size(); i++) {
                        h2List[i] = "-" + elsH2.get(i).text() + "-";
                    }
                    // 拿到ul：解释内容
                    StringBuilder sb = new StringBuilder();
                    Elements elsUl = doc.getElementsByTag("ul");
                    for (int i = 0; i < elsUl.size(); i++) {
                        // 添加标题
                        sb.append(h2List[i]);
                        sb.append("\n");
                        // 添加当前标题下具体解释
                        Elements elsLi = elsUl.get(i).getElementsByTag("li");
                        for (int j = 0; j < elsLi.size(); j++) {
                            sb.append(j+1);
                            sb.append(". ");
                            sb.append(elsLi.get(j).text());
                            if (!(i == elsUl.size()-1 && j == elsLi.size()-1)){
                                sb.append("\n");
                            }
                        }
                        if (i != elsUl.size()-1){
                            sb.append("\n");
                        }
                    }
                    data.putString("mean", sb.toString());

                    msg.setData(data);
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void getNetInfoCharZh(final String ctnt){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String pron = data.getString("pron");
                String mean = data.getString("mean");

                word.setPron(pron);
                pronView.setText(pron);
                word.setMean(mean);
                meanView.setText(mean);

                db.updateWordByNet(ctnt, pron, mean);

                Toast.makeText(getApplicationContext(), "补全完成", Toast.LENGTH_LONG).show();

                // 当收到消息时就会执行这个方法
            }
        };

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("http://webdict.youzhi.net/index.do")
                            .data("id", "65bd6dc3c1b7488a80dbc0c24fcbf0e5")
                            .data("words", ctnt)
                            .userAgent("Mozilla")
                            .post();

                    Message msg = new Message();
                    Bundle data = new Bundle();

                    // 拿读音
                    Elements elsPron = doc.select("li.pingyin");
                    Element elPron = elsPron.first();
                    data.putString("pron", elPron.text());

                    StringBuilder sb = new StringBuilder();

                    // 拿到h2：解释标题
                    Elements elsH2 = doc.getElementsByTag("h2");
                    String h2 = elsH2.first().text();
                    sb.append("-");
                    sb.append(h2);
                    sb.append("-");
                    sb.append("\n");

                    // 拿到ul：解释内容

                    Elements elsMean = doc.select("div.result-main2");
                    Element elMean = elsMean.first();
                    Elements elsLi = elMean.getElementsByTag("li");
                    for (int i = 0; i < elsLi.size(); i++){
                        sb.append(i+1);
                        sb.append(". ");
                        sb.append(elsLi.get(i).text());
                        if (i != elsLi.size()-1){
                            sb.append("\n");
                        }
                    }

                    data.putString("mean", sb.toString());

                    msg.setData(data);
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
