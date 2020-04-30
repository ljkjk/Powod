package com.ljkjk.powod.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.ljkjk.powod.R;
import com.ljkjk.powod.utils.DatabaseUtils;
import com.ljkjk.powod.utils.Utils;
import com.ljkjk.powod.utils.WordListUtils;

public class SettingActivityTemp extends AppCompatActivity {

    Toolbar toolbar;
    EditText ipEditText;
    EditText projEditText;
    EditText portEditText;
    MaterialButton btnViewLog, btnClear;

    SharedPreferences sharedPreferences;

    DatabaseUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_temp);

        init();
        setUrl();
        setToolbar();
        setBtnClear();
        setBtnViewLog();
    }

    void init(){
        toolbar = findViewById(R.id.toolbar_setting);
        ipEditText = findViewById(R.id.setting_input_ip);
        projEditText = findViewById(R.id.setting_input_project);
        portEditText = findViewById(R.id.setting_input_port);
        btnViewLog  = findViewById(R.id.btn_setting_view_log);
        btnClear = findViewById(R.id.btn_setting_clear);
        db = new DatabaseUtils(getApplicationContext());
        sharedPreferences = getSharedPreferences("serverConfig", MODE_PRIVATE);
    }

    void setUrl(){
        ipEditText.setText(sharedPreferences.getString("ip", ""));
        portEditText.setText(sharedPreferences.getString("port", ""));
        projEditText.setText(sharedPreferences.getString("proj", ""));
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

    void setBtnClear(){
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingActivityTemp.this);
                dialogBuilder.setTitle("将会同时清空数据库！");
                dialogBuilder.setCancelable(false);

                dialogBuilder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WordListUtils.clear();
                        db.reset();
                        Toast.makeText(getApplicationContext(), "这就是你想要的吗", Toast.LENGTH_LONG).show();
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
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ip = ipEditText.getText().toString().trim();
                String port = portEditText.getText().toString().trim();
                String proj = projEditText.getText().toString().trim();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ip", ip);
                editor.putString("port", port);
                editor.putString("proj", proj);
                editor.apply();
                Utils.setUrl(ip, port, proj);
            }
        };

        ipEditText.addTextChangedListener(textWatcher);
        projEditText.addTextChangedListener(textWatcher);
    }

    void setBtnViewLog(){
        btnViewLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingActivityTemp.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.update_log, null);

                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("更新日志");
                dialogBuilder.setCancelable(false);

                dialogBuilder.setNegativeButton("了解", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

            }
        });
    }
}
