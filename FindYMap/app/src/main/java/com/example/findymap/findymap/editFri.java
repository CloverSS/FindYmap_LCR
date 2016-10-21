package com.example.findymap.findymap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.util.Calendar;

/**
 * Created by apple on 2016/9/21.
 */
public class editFri extends Activity implements View.OnClickListener {
    private TextView Fnumber;
    private Button button_done;
    private Button button_list;
    private Dialog editDialog;
    persons Friendetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.friend_detail);
        Intent intent = getIntent();
        Friendetail = (persons) intent.getSerializableExtra("nowfriend");

        Fnumber = (TextView) findViewById(R.id.txt_friend_number);
        String s=Friendetail.getNum();
        Fnumber.setText(s);

        button_done = (Button) findViewById(R.id.btn_friends_list_edit);
        button_done.setOnClickListener(this);

        Button Go_rader = (Button) findViewById(R.id.btn_radar);
        Go_rader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(editFri.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        button_list=(Button)findViewById(R.id.btn_friends_list);
                button_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent();
                        intent.putExtra("new_num", Friendetail.getNum());
                        intent.putExtra("new_name", Friendetail.getNames());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_friends_list_edit:
                editDialog = new Dialog(editFri.this);
                editDialog.show();
                editDialog.getWindow().setContentView(R.layout.dialog_add_friend);
                final EditText edit_name = (EditText) editDialog.getWindow().findViewById(R.id.txt_friend_name);
                final EditText edit_num = (EditText) editDialog.getWindow().findViewById(R.id.txt_friend_number);
                edit_name.setText(Friendetail.getNames());
                edit_num.setText(Friendetail.getNum());
                editDialog.getWindow()
                        .findViewById(R.id.btn_dialog_ok)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String name=edit_name.getText().toString();
                                String num=edit_num.getText().toString();
                                Friendetail.setNum(num);
                                Friendetail.setNames(name);
                                Fnumber.setText(Friendetail.getNum());
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
