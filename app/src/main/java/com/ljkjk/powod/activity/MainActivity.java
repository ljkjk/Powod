package com.ljkjk.powod.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ljkjk.powod.R;
import com.ljkjk.powod.SortType;
import com.ljkjk.powod.entity.Word;
import com.ljkjk.powod.list.WordClassifiedAdapter;
import com.ljkjk.powod.utils.DatabaseUtils;
import com.ljkjk.powod.MyApplication;
import com.ljkjk.powod.utils.Utils;
import com.ljkjk.powod.utils.WordListUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    public ListView listView;
    // TextView nTotal;
    FloatingActionButton fab;

    ProgressDialog pDialog;
    AlertDialog aDialog;

    DatabaseUtils db;
    SharedPreferences sharedPreferences;
    SortType currSortType;

    public static boolean isChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_main);
        fab = findViewById(R.id.fab);
        listView = findViewById(R.id.list_view);
        // nTotal = findViewById(R.id.text_total);
        db = new DatabaseUtils(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        sharedPreferences = getSharedPreferences("serverConfig", MODE_PRIVATE);
        currSortType = SortType.DEFAULT;

        setSupportActionBar(toolbar);
        refreshWordList(currSortType);
        Utils.setUrl(sharedPreferences.getString("ip", ""),
                sharedPreferences.getString("port", ""),
                sharedPreferences.getString("proj", ""));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChanged = false;
                Intent intent = new Intent(MainActivity.this, WordAddActivity.class);
                intent.putExtra("pa", "ADD");
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isChanged = false;
                Intent intent = new Intent(MainActivity.this, WordDetailsActivity.class);
                intent.putExtra("ctnt", ((WordClassifiedAdapter.WordTypeItem) (listView.getAdapter().getItem(position))).getWord().getCtnt());
                intent.putExtra("pa", "MAIN");
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        // 让listiview也能发起滚动
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }
    }

    // 刷新列表
    void refreshWordList(SortType sortType) {
        WordListUtils.getWordList(db);
        WordListUtils.sort(sortType);
        // nTotal.setText(String.format(getResources().getString(R.string.text_total), WordListUtils.size()));
        listView.setAdapter(new WordClassifiedAdapter(MainActivity.this, WordListUtils.list(), sortType));
    }

    void refreshWordListNotUpdateList(SortType sortType) {
        WordListUtils.sort(sortType);
        listView.setAdapter(new WordClassifiedAdapter(MainActivity.this, WordListUtils.list(), sortType));
    }

    // 返回页面
    @Override
    public void onResume() {
        super.onResume();
        if (isChanged) {
            refreshWordList(currSortType);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.action_refresh:
                refreshWordList(currSortType);
                Toast.makeText(getApplicationContext(), "刷新成功",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_tag:
                actionTag();
                return true;
            case R.id.action_search:
                actionSearch();
                return true;
            case R.id.action_sorts:
                actionSorts();
                return true;
            case R.id.action_upload:
                actionUpload();
                return true;
            case R.id.action_download:
                actionDownload();
                return true;
            case R.id.action_settings:
                actionSetting();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void actionSearch(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.search, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("搜索");
        dialogBuilder.setCancelable(false);

        final EditText editTextSearch = dialogView.findViewById(R.id.search);

        dialogBuilder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
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
                if(editTextSearch.getText().length() > 0){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        editTextSearch.addTextChangedListener(textWatcher);

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setEnabled(false);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, ResultListActivity.class);
                        intent.putExtra("key", editTextSearch.getText().toString().trim());
                        startActivity(intent);
                    }
                });
            }
        });

        alertDialog.show();
    }

    void actionTag(){
        final String[] items = WordListUtils.getTags(db);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("选择标签");

        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                aDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, ResultListActivity.class);
                intent.putExtra("tag", items[i]);
                startActivity(intent);
            }
        });
        aDialog = dialogBuilder.create();
        aDialog.show();
    }


    void actionSorts(){
        final String[] items = {"默认", "时间", "频次"};
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("排序方式");

        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (items[i]) {
                    case "默认":
                        currSortType = SortType.DEFAULT;
                        break;
                    case "时间":
                        currSortType = SortType.DATE;
                        break;
                    case "频次":
                        currSortType = SortType.FREQUENCY;
                        break;
                }
                refreshWordListNotUpdateList(currSortType);
                aDialog.dismiss();
            }
        });
        aDialog = dialogBuilder.create();
        aDialog.show();
    }

    void actionUpload(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setTitle("确认上传");
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.isUrlValid()){
                    if (Utils.isNetworkAvailable(MainActivity.this)){
                        pDialog.setMessage("上传中...");
                        showDialog();
                        upload();
                        hideDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"请将服务器信息填写完整", Toast.LENGTH_LONG).show();
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

    void upload(){
        List<Word> tempWordList = WordListUtils.fullList(db);
        for (Word word: tempWordList){
            uploadOneWord(word);
            System.out.println(word.getCtnt());
        }
        Toast.makeText(getApplicationContext(),"上传完成", Toast.LENGTH_LONG).show();
        // 这个就算没网也会显示，下个版本改一下
    }

    void uploadOneWord(final Word word) {
        String tag_string_req = "req_upload";

        StringRequest strReq = new StringRequest(Request.Method.POST, Utils.UPLOAD_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // 检查json的error标记
                            if (error) {
                                String errorMsg = jObj.getString("message");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                String errorMessage = "上传失败";
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("ctnt", word.getCtnt());
                params.put("pron", word.getPron());
                params.put("mean", word.getMean());
                params.put("tags", word.getTags());
                params.put("syno", word.getSyno());
                params.put("anto", word.getAnto());
                params.put("freq", String.valueOf(word.getFreq()));
                params.put("addt", Utils.date2String(word.getAddt()));

                return params;
            }
        };

        // 添加request到request队列
        strReq.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    void actionDownload(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setTitle("确认下载");
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println(Utils.MAIN_URL);
                if (Utils.isUrlValid()) {
                    if (Utils.isNetworkAvailable(MainActivity.this)){
                        pDialog.setMessage("下载中...");
                        showDialog();
                        download();
                        hideDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"请将服务器信息填写完整", Toast.LENGTH_LONG).show();
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

    void download(){
        String tag_string_req = "req_download";

        StringRequest strReq = new StringRequest(Request.Method.POST, Utils.DOWNLOAD_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);

                            int count =jObj.getInt("cnt");
                            System.out.println(count);
                            for (int i = 0; i < count; i++) {
                                JSONObject jsWord = jObj.getJSONObject("word"+i);
                                Word word = new Word();

                                word.setCtnt(jsWord.getString("ctnt"));
                                word.setPron(jsWord.optString("pron"));
                                word.setMean(jsWord.optString("mean"));
                                word.setTags(jsWord.optString("tags"));
                                word.setSyno(jsWord.optString("syno"));
                                word.setAnto(jsWord.optString("anto"));
                                word.setFreq(jsWord.optInt("freq"));
                                word.setAddt(Utils.string2Date(jsWord.optString("addt")));

                                db.insertWord(word);
                            }

                            refreshWordList(currSortType); //不知道为什么它不刷新
                            Toast.makeText(getApplicationContext(), "完成下载 " + WordListUtils.size() + " 项", Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                String errorMessage = "下载失败";
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

        };

        // 添加request到request队列
        strReq.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    void actionSetting(){
        isChanged = false;
        Intent intent = new Intent(MainActivity.this, SettingActivityTemp.class);
        startActivity(intent);
    }


    void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
