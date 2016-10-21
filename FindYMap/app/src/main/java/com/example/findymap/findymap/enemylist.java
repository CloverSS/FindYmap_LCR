package com.example.findymap.findymap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class enemylist extends Activity {
    private ListView mListView;
    private EnemyLAdapter adpter;
    private Dialog addDialog = null;
    int NowPosition;
    ArrayList<persons> Enemydata = new ArrayList<persons>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.enemies_list);

        Enemydata = (ArrayList<persons>) getObject("enemies_list");


        mListView = (ListView) findViewById(R.id.lvw_enemies_list);
        adpter = new EnemyLAdapter(this, Enemydata);
        mListView.setAdapter(adpter); //为ListView设置适配器
        //按下list中元素，进入编辑页面，传入对象
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NowPosition=position;
                Intent intent = new Intent(enemylist.this, editEne.class);
                persons NowFri =  Enemydata.get(position);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("nowenemy", NowFri);
                intent.putExtras(mBundle);
                startActivityForResult(intent, 1);
            }
        });

        Button Return_Main = (Button) findViewById(R.id.btn_enemies_list_radar);
        Return_Main.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View arg0) {
                                               Intent intent = new Intent(enemylist.this, MainActivity.class);
                                               startActivity(intent);
                                               finish();
                                           }
                                       });

        Button Go_friend = (Button) findViewById(R.id.btn_enemies_list_friends);
        Go_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(enemylist.this, Friendslist.class);
                startActivity(intent);
                finish();
            }
        });

        Button add_fri = (Button) findViewById(R.id.btn_enemies_list_add);
        add_fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addDialog = new Dialog(enemylist.this);
                addDialog.show();
                addDialog.getWindow().setContentView(R.layout.dialog_add_enemy);
                final EditText add_name = (EditText) addDialog.getWindow().findViewById(R.id.txt_enemy_name);
                final EditText add_num = (EditText) addDialog.getWindow().findViewById(R.id.txt_enemy_number);
                addDialog.getWindow()
                        .findViewById(R.id.btn_dialog_ok)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Enemydata.add(new persons(add_num.getText().toString(), add_name.getText().toString(),0,0));
                                saveObject("enemies_list");
                                adpter = new EnemyLAdapter(enemylist.this,  Enemydata);
                                mListView.setAdapter(adpter);
                                addDialog.dismiss();
                            }
                        });
                addDialog.getWindow()
                        .findViewById(R.id.btn_dialog_close)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addDialog.dismiss();
                            }
                        });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    persons nowData= Enemydata.get(NowPosition);
                    nowData.setNames(data.getStringExtra("new_name"));
                    nowData.setNum(data.getStringExtra("new_num"));
                    Enemydata.set(NowPosition,nowData);
                    saveObject("enemies_list");
                    adpter = new EnemyLAdapter(enemylist.this,  Enemydata);
                    mListView.setAdapter(adpter);
                }
                break;
            default:
        }
    }

    //将数据保存到本地
    private void saveObject(String name) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = this.openFileOutput(name, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject( Enemydata);
        } catch (Exception e) {
            e.printStackTrace();
            //这里是保存文件产生异常
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //fos流关闭异常
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    //oos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }

    //读取文件
    private Object getObject(String name) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = this.openFileInput(name);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            //这里是读取文件产生异常
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    //fis流关闭异常
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    //ois流关闭异常
                    e.printStackTrace();
                }
            }
        }
        //读取产生异常，返回null
        return null;
    }

    /**
     * 实体类
     */
    public final class ViewHolder {
        public TextView Friname;
        public Button button_del;
    }

    class EnemyLAdapter extends BaseAdapter {
        private List<persons> mData;       //创建Diary类型的List表
        private LayoutInflater mInflater;               //定义线性布局过滤器

        public EnemyLAdapter(Context context, List<persons> data) {
            this.mData = data;
            mInflater = LayoutInflater.from(context);       //获取布局
        }

        /**
         * 得到列表长度
         *
         * @return
         */
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public long getItemId(int position) {
            return position;    //得到子项位置id
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.enemies_list_item, null);
                holder.Friname = (TextView) convertView.findViewById(R.id.name_cell);
                holder.button_del = (Button) convertView.findViewById(R.id.delete_button_cell);
                holder.Friname.setText(mData.get(position).getNames());
                holder.button_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        addDialog = new AlertDialog.Builder(enemylist.this).create();
                        addDialog.show();
                        addDialog.getWindow().setContentView(R.layout.dialog_delete);
                        addDialog.getWindow().findViewById(R.id.btn_dialog_ok)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Enemydata.remove(position);
                                        saveObject("enemies_list");
                                        adpter = new EnemyLAdapter(enemylist.this,  Enemydata);
                                        mListView.setAdapter(adpter);
                                        addDialog.dismiss();
                                    }
                                });
                        addDialog.getWindow().findViewById(R.id.btn_dialog_close)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addDialog.dismiss();
                                    }
                                });
                    }
                });
                convertView.setTag(holder);
            } else {
                convertView.getTag();
            }
            return convertView;
        }
    }
}

