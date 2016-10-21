package com.example.findymap.findymap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by apple on 2016/9/21.
 */
public class editEne extends Activity implements View.OnClickListener {
    private TextView Fnumber;
    private Button button_done;
    private Button button_list;
    private Dialog editDialog;
    persons enemydetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.enemy_detail);
        Intent intent = getIntent();
        enemydetail = (persons) intent.getSerializableExtra("nowenemy");

        Fnumber = (TextView) findViewById(R.id.txt_enemy_number);
        String s=enemydetail.getNum();
        Fnumber.setText(s);

        button_done = (Button) findViewById(R.id.btn_enemies_list_edit);
        button_done.setOnClickListener(this);

        Button Go_rader = (Button) findViewById(R.id.btn_radar);
        Go_rader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(editEne.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button_list=(Button)findViewById(R.id.btn_enemies_list);
                button_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent();
                        intent.putExtra("new_num", enemydetail.getNum());
                        intent.putExtra("new_name", enemydetail.getNames());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enemies_list_edit:
                editDialog = new Dialog(editEne.this);
                editDialog.show();
                editDialog.getWindow().setContentView(R.layout.dialog_add_enemy);
                final EditText edit_name = (EditText) editDialog.getWindow().findViewById(R.id.txt_enemy_name);
                final EditText edit_num = (EditText) editDialog.getWindow().findViewById(R.id.txt_enemy_number);
                edit_name.setText(enemydetail.getNames());
                edit_num.setText(enemydetail.getNum());
                editDialog.getWindow()
                        .findViewById(R.id.btn_dialog_ok)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String name=edit_name.getText().toString();
                                String num=edit_num.getText().toString();
                                enemydetail.setNum(num);
                                enemydetail.setNames(name);
                                Fnumber.setText(enemydetail.getNum());
                                editDialog.dismiss();
                            }
                        });
                editDialog.getWindow().findViewById(R.id.btn_dialog_close)
                        .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    editDialog.dismiss();
                                                }
                                            });

        }
    }

}
